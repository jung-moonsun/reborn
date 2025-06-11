package com.ms.reborn.global.security;

import com.ms.reborn.domain.user.entity.User;
import com.ms.reborn.domain.user.repository.UserRepository;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public HttpHandshakeInterceptor(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        try {
            URI uri = request.getURI();
            System.out.println("Handshake URI: " + uri);

            String query = uri.getQuery();
            String token = null;

            if (query != null) {
                for (String param : query.split("&")) {
                    if (param.startsWith("token=")) {
                        token = param.substring("token=".length());
                        break;
                    }
                }
            }

            if (token == null) {
                System.out.println("WebSocket JWT Token not found in query params.");
                return false;
            }

            System.out.println("WebSocket JWT Token: " + token);

            String email = jwtUtil.validateAndGetEmail(token);
            System.out.println("✅ 이메일 검증 통과: " + email);

            if (email == null) {
                System.out.println("❌ 이메일 null");
                return false;
            }

            User user = userRepository.findByEmailAndDeletedFalse(email).orElse(null);
            if (user == null) {
                System.out.println("❌ 해당 이메일로 유저 없음");
                return false;
            }

            attributes.put("email", email);
            attributes.put("userId", user.getId());
            System.out.println("✅ WebSocket 인증 성공: userId=" + user.getId());
            return true;

        } catch (Exception e) {
            System.out.println("❌ WebSocket 인증 중 예외 발생: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // Optional logging
    }
}

