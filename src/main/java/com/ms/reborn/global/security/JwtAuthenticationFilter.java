package com.ms.reborn.global.security;

import com.ms.reborn.domain.user.entity.User;
import com.ms.reborn.domain.user.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = parseJwt(request);

            if (token != null) {
                Long userId = jwtUtil.getUserId(token); // ✅ userId 추출
                logger.info("JWT 검증 성공, userId={}", userId);

                User user = userService.getActiveUser(userId); // ✅ soft delete 고려
                if (user == null) {
                    logger.error("JWT 인증 실패: 존재하지 않는 유저 - userId={}", userId);
                    throw new IllegalArgumentException("존재하지 않는 사용자: " + userId);
                }

                // SecurityContext에 인증 정보 저장
                CustomUserDetails customUserDetails = new CustomUserDetails(user);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                customUserDetails,
                                null,
                                customUserDetails.getAuthorities()
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                request.setAttribute("userId", userId);
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            handleError(response, "JWT 만료됨", e);
        } catch (SignatureException e) {
            handleError(response, "JWT 서명 불일치", e);
        } catch (MalformedJwtException e) {
            handleError(response, "JWT 형식 오류", e);
        } catch (IllegalArgumentException e) {
            handleError(response, "JWT 인증 오류", e);
        } catch (Exception e) {
            handleError(response, "알 수 없는 인증 오류", e);
        }
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    private void handleError(HttpServletResponse response, String message, Exception e) throws IOException {
        logger.error("{}: {}", message, e.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\": \"" + message + ": " + e.getMessage() + "\"}");
    }
}
