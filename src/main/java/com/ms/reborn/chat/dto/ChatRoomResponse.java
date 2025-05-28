package com.ms.reborn.chat.dto;

import com.ms.reborn.chat.entity.ChatRoom;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChatRoomResponse {
    private Long roomId;
    private Long productId;
    private Long sellerId;
    private Long buyerId;

    public static ChatRoomResponse from(ChatRoom room) {
        return ChatRoomResponse.builder()
                .roomId(room.getId())
                .productId(room.getProduct().getId())
                .sellerId(room.getSeller().getId())
                .buyerId(room.getBuyer().getId())
                .build();
    }
}

