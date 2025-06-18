package com.ms.reborn.domain.user.service;

import com.ms.reborn.domain.user.dto.KakaoProfile;
import com.ms.reborn.domain.user.dto.KakaoTokenResponse;
import com.ms.reborn.domain.user.entity.User;
import com.ms.reborn.domain.user.repository.UserRepository;
import com.ms.reborn.global.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class KakaoOAuthService {
    private static final Logger log = LoggerFactory.getLogger(KakaoOAuthService.class);

    private final RestTemplate restTemplate;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final Environment env;

    /** 카카오 인가 URL 생성 */
    public String getAuthorizeUrl() {
        String clientId    = env.getProperty("spring.kakao.client-id");
        String redirectUri = env.getProperty("spring.kakao.redirect-uri");
        return "https://kauth.kakao.com/oauth/authorize"
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri;
    }

    /** Access Token 요청 */
    public String getAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String,String> form = new LinkedMultiValueMap<>();
        form.add("grant_type",   "authorization_code");
        form.add("client_id",    env.getProperty("spring.kakao.client-id"));
        form.add("redirect_uri", env.getProperty("spring.kakao.redirect-uri"));
        form.add("code",         code);

        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(form, headers);
        try {
            ResponseEntity<KakaoTokenResponse> resp = restTemplate.postForEntity(
                    tokenUrl, request, KakaoTokenResponse.class
            );
            KakaoTokenResponse body = resp.getBody();
            if (body == null || body.getAccess_token() == null) {
                log.error("토큰 응답이 유효하지 않습니다: {}", body);
                throw new IllegalStateException("카카오 토큰 응답이 null입니다");
            }
            return body.getAccess_token();
        } catch (HttpClientErrorException hce) {
            log.error("카카오 토큰 요청 실패 ({}): {}", hce.getStatusCode(), hce.getResponseBodyAsString());
            throw new RuntimeException("카카오 토큰 요청 실패", hce);
        } catch (Exception e) {
            log.error("카카오 토큰 요청 예외 발생", e);
            throw new RuntimeException("카카오 토큰 요청 실패", e);
        }
    }

    /** 프로필 조회 */
    public KakaoProfile getKakaoProfile(String accessToken) {
        String profileUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        try {
            ResponseEntity<KakaoProfile> resp = restTemplate.exchange(
                    profileUrl, HttpMethod.GET, new HttpEntity<>(headers), KakaoProfile.class
            );
            return resp.getBody();
        } catch (Exception e) {
            log.error("카카오 프로필 조회 실패", e);
            throw new RuntimeException("카카오 프로필 조회 실패", e);
        }
    }

    /**
     * 가입 또는 로그인 후 JWT 발급
     * 탈퇴된 계정은 재활성화
     */
    public String loginOrRegisterAndGetJwt(String code) {
        String accessToken = getAccessToken(code);
        KakaoProfile prof = getKakaoProfile(accessToken);

        String kakaoId   = String.valueOf(prof.getId());
        String email     = prof.getKakao_account().getEmail();
        String nickname  = prof.getKakao_account().getProfile().getNickname();

        User user = userRepo.findByProviderAndProviderId("kakao", kakaoId)
                .map(u -> {
                    if (u.isDeleted()) {
                        u.setDeleted(false);
                        u.setEmail(email);
                        u.setNickname(nickname);
                        return userRepo.save(u);
                    }
                    return u;
                })
                .orElseGet(() -> {
                    User u = new User();
                    u.setProvider("kakao");
                    u.setProviderId(kakaoId);
                    u.setEmail(email);
                    u.setNickname(nickname);
                    // NOT NULL 제약을 위한 임시 비밀번호
                    u.setPassword(UUID.randomUUID().toString());
                    return userRepo.save(u);
                });

        return jwtUtil.generateToken(user.getEmail(), user.getId());
    }
}
