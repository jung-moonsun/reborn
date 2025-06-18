package com.ms.reborn.domain.chat.controller;

import com.ms.reborn.domain.chat.dto.ChatMessageResponse;
import com.ms.reborn.domain.chat.dto.ChatRoomRequest;
import com.ms.reborn.domain.chat.dto.ChatRoomResponse;
import com.ms.reborn.domain.chat.service.ChatRoomService;
import com.ms.reborn.domain.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    @Operation(summary = "채팅방 생성 또는 조회",
            description = "상품 ID와 구매자 ID를 받아 채팅방이 없으면 생성, 있으면 조회합니다.")
    @PostMapping
    public ResponseEntity<ChatRoomResponse> createOrGetRoom(
            @Parameter(description = "채팅방 생성 요청 정보 (상품 ID, 구매자 ID)", required = true)
            @RequestBody ChatRoomRequest request
    ) {
        ChatRoomResponse room = chatRoomService.createOrGetRoom(
                request.getProductId(), request.getBuyerId()
        );
        return ResponseEntity.ok(room);
    }

    @Operation(summary = "채팅방 나가기", description = "채팅방에서 나가기 (soft delete)")
    @PutMapping("/{roomId}/exit")
    public ResponseEntity<Void> exitRoom(
            @Parameter(description = "채팅방 ID", required = true)
            @PathVariable Long roomId,
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId
    ) {
        chatRoomService.exitRoom(roomId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "채팅방 상세 조회", description = "채팅방 ID로 채팅방 정보를 조회합니다.")
    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoomResponse> getRoom(
            @Parameter(description = "조회할 채팅방 ID", required = true)
            @PathVariable Long roomId) {
        return ResponseEntity.ok(chatRoomService.getRoom(roomId));
    }

    @Operation(summary = "채팅 메시지 목록 조회",
            description = "채팅방 ID와 페이징 정보를 받아 해당 채팅방의 메시지 목록을 조회합니다.")
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<Page<ChatMessageResponse>> getMessages(
            @Parameter(description = "메시지를 조회할 채팅방 ID", required = true)
            @PathVariable Long roomId,
            @Parameter(description = "조회할 페이지 번호 (기본값 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 당 메시지 수 (기본값 20)")
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(chatService.getMessages(roomId, page, size));
    }

    @Operation(summary = "채팅방 메시지 읽음 처리", description = "채팅방의 읽지 않은 메시지를 모두 읽음 처리합니다.")
    @PutMapping("/{roomId}/read")
    public ResponseEntity<Void> markAllMessagesAsRead(
            @Parameter(description = "채팅방 ID", required = true)
            @PathVariable Long roomId,
            @Parameter(description = "인증된 사용자 ID", required = true)
            @RequestParam Long receiverId
    ) {
        chatService.markAllMessagesAsRead(roomId, receiverId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "사용자 채팅방 목록 조회",
            description = "사용자 ID와 페이징 정보를 받아 해당 사용자가 참여한 채팅방 목록을 조회합니다.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ChatRoomResponse>> getUserChatRooms(
            @Parameter(description = "채팅방 목록을 조회할 사용자 ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "조회할 페이지 번호 (기본값 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 당 채팅방 수 (기본값 20)")
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<ChatRoomResponse> rooms = chatRoomService.getUserChatRooms(userId, page, size);
        return ResponseEntity.ok(rooms);
    }

    @Operation(summary = "안 읽은 메시지 수 조회", description = "사용자 ID를 기준으로 읽지 않은 채팅 메시지 개수를 반환합니다.")
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(
            @Parameter(description = "수신자 ID", required = true)
            @RequestParam Long receiverId
    ) {
        long count = chatService.getUnreadMessageCount(receiverId);
        return ResponseEntity.ok(count);
    }
}
