package com.ms.reborn.global.security;

import com.ms.reborn.domain.user.entity.User;
import com.ms.reborn.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String kakaoId = oAuth2User.getAttribute("id").toString();

            Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
            String email = (kakaoAccount != null && kakaoAccount.get("email") != null)
                    ? kakaoAccount.get("email").toString()
                    : "kakao_" + kakaoId + "@kakao.com";

            User user = userService.findOrCreateOAuthUser("kakao", kakaoId, email);
            String token = jwtUtil.generateToken(user.getEmail(), user.getId());

            String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth/callback")
                    .queryParam("token", token)
                    .build().toUriString();

            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            String errorUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth/callback")
                    .queryParam("error", e.getMessage())
                    .build().toUriString();

            response.sendRedirect(errorUrl);
        }
    }
}
