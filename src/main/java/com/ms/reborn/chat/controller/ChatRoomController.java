package com.ms.reborn.chat.controller;

import com.ms.reborn.chat.dto.ChatRoomRequest;
import com.ms.reborn.chat.dto.ChatRoomResponse;
import com.ms.reborn.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<ChatRoomResponse> createOrGetRoom(
            @RequestBody ChatRoomRequest request
    ) {
        ChatRoomResponse room = chatRoomService.createOrGetRoom(
                request.getProductId(), request.getBuyerId()
        );
        return ResponseEntity.ok(room);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoomResponse> getRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(chatRoomService.getRoom(roomId));
    }
}
