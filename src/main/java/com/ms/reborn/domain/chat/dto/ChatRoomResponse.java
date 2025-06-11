package com.ms.reborn.domain.chat.dto;

import com.ms.reborn.domain.chat.entity.ChatRoom;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponse {
    private Long id;
    private Long productId;
    private Long buyerId;
    private Long sellerId;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
    private String lastMessage;
    private long unreadCount;
    private Long opponentId;
    private String opponentNickname;

    public static ChatRoomResponse from(ChatRoom room, String lastMessage, long unreadCount, Long userId, String opponentNickname) {
        return ChatRoomResponse.builder()
                .id(room.getId())
                .productId(room.getProduct().getId())
                .buyerId(room.getBuyer().getId())
                .sellerId(room.getSeller().getId())
                .createdAt(room.getCreatedAt())
                .lastMessageAt(room.getLastMessageAt())
                .lastMessage(lastMessage)
                .unreadCount(unreadCount)
                .opponentId(userId)
                .opponentNickname(opponentNickname)
                .build();
    }
}
