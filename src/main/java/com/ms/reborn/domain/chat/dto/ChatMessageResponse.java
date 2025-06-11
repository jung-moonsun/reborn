package com.ms.reborn.domain.chat.dto;

import com.ms.reborn.domain.chat.entity.ChatMessage;
import com.ms.reborn.domain.chat.entity.MessageType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {
    private Long id;
    private Long roomId;
    private Long senderId;
    private Long receiverId;
    private String message;
    private String fileUrl;
    private MessageType messageType;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessage msg) {
        return ChatMessageResponse.builder()
                .id(msg.getId())
                .roomId(msg.getChatRoom().getId())
                .senderId(msg.getSender().getId())
                .receiverId(msg.getReceiver().getId())
                .message(msg.getMessage())
                .fileUrl(msg.getFileUrl())
                .messageType(msg.getMessageType())
                .isRead(msg.isRead())
                .createdAt(msg.getCreatedAt())
                .build();
    }
}
