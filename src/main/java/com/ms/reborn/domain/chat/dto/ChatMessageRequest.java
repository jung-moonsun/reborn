package com.ms.reborn.domain.chat.dto;

import com.ms.reborn.domain.chat.entity.MessageType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequest {
    private Long roomId;
    private Long senderId;
    private String message;
    private String fileUrl;
    private MessageType messageType;
}
