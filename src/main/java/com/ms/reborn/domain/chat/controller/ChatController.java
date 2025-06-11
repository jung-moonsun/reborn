package com.ms.reborn.domain.chat.controller;

import com.ms.reborn.domain.chat.dto.ChatMessageRequest;
import com.ms.reborn.domain.chat.dto.ChatMessageResponse;
import com.ms.reborn.domain.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Slf4j
@Controller
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;

    @Operation(summary = "웹소켓 메시지 전송", description = "채팅 메시지를 받아 저장하고 해당 채팅방 구독자에게 전송합니다.")
    @MessageMapping("/chat/message")
    public void sendMessage(@Payload ChatMessageRequest request) {
        log.info("Received message: {}", request);

        ChatMessageResponse savedMessage = chatService.saveMessage(request);

        messagingTemplate.convertAndSend(
                "/topic/chat/" + request.getRoomId(),
                savedMessage
        );
    }
}
