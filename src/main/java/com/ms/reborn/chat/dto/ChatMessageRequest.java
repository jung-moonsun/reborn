package com.ms.reborn.chat.dto;

import com.ms.reborn.chat.entity.MessageType;
import lombok.Data;

@Data
public class ChatMessageRequest {
    private Long roomId;
    private String message;
    private Long senderId;
    private MessageType messageType;
}
