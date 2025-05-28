package com.ms.reborn.chat.controller;

import com.ms.reborn.chat.dto.ChatMessageRequest;
import com.ms.reborn.chat.dto.ChatMessageResponse;
import com.ms.reborn.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void sendMessage(@Payload ChatMessageRequest request) {
        log.info("Received message: {}", request);

        ChatMessageResponse savedMessage = chatService.saveMessage(request);

        messagingTemplate.convertAndSend(
                "/topic/chat/" + request.getRoomId(), savedMessage
        );
    }
}
