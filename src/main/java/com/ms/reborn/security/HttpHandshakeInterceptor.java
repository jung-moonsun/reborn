package com.ms.reborn.security;

import com.ms.reborn.user.entity.User;
import com.ms.reborn.user.repository.UserRepository;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.List;
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
                                   Map<String, Object> attributes) throws Exception {

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
        if (email == null) return false;

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return false;

        attributes.put("email", email);
        attributes.put("userId", user.getId());

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // Optional logging
    }
}

