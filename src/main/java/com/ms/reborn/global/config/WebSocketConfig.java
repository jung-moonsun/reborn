package com.ms.reborn.global.config;

import com.ms.reborn.global.security.HttpHandshakeInterceptor;
import com.ms.reborn.global.security.JwtUtil;
import com.ms.reborn.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws-chat")           // 엔드포인트는 "/ws-chat"
                .setAllowedOriginPatterns("*")     // 프론트(React)가 어느 주소에서 와도 허용
                .addInterceptors(new HttpHandshakeInterceptor(jwtUtil, userRepository))
                .withSockJS();                     // SockJS 사용
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/pub");
    }
}