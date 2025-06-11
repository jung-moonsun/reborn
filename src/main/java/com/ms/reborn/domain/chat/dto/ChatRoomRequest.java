package com.ms.reborn.domain.chat.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomRequest {
    private Long productId;
    private Long buyerId;
}
