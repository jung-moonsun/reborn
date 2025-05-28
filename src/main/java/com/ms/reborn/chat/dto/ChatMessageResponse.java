package com.ms.reborn.chat.dto;

import com.ms.reborn.chat.entity.ChatMessage;
import com.ms.reborn.chat.entity.MessageType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ChatMessageResponse {
    private Long messageId;
    private Long roomId;
    private Long senderId;
    private String message;
    private MessageType messageType;
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessage msg) {
        return ChatMessageResponse.builder()
                .messageId(msg.getId())
                .roomId(msg.getChatRoom().getId())
                .senderId(msg.getSender().getId())
                .message(msg.getMessage())
                .messageType(msg.getMessageType())
                .createdAt(msg.getCreatedAt())
                .build();
    }
}
