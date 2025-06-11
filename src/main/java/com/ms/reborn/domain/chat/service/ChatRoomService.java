package com.ms.reborn.domain.chat.service;

import com.ms.reborn.domain.chat.dto.ChatRoomResponse;
import com.ms.reborn.domain.chat.entity.ChatRoom;
import com.ms.reborn.domain.chat.repository.ChatRoomRepository;
import com.ms.reborn.domain.product.entity.Product;
import com.ms.reborn.domain.product.repository.ProductRepository;
import com.ms.reborn.domain.user.entity.User;
import com.ms.reborn.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ChatRoomResponse createOrGetRoom(Long productId, Long buyerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품 없음"));

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("구매자 없음"));

        User seller = product.getUser();

        Optional<ChatRoom> existingRoom = chatRoomRepository.findByProductAndBuyer(product, buyer);

        ChatRoom room = existingRoom.orElseGet(() -> {
            ChatRoom newRoom = ChatRoom.builder()
                    .product(product)
                    .buyer(buyer)
                    .seller(seller)
                    .createdAt(LocalDateTime.now())
                    .lastMessageAt(LocalDateTime.now())
                    .build();
            return chatRoomRepository.save(newRoom);
        });

        room.setLastMessageAt(LocalDateTime.now());
        chatRoomRepository.save(room);

        // 마지막 메시지, 읽지 않은 메시지, 상대방 정보는 여기서 채워넣을 것!
        return ChatRoomResponse.from(room, "", 0, null, "");
    }

    @Transactional
    public void exitRoom(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방 없음"));
        if (room.getBuyer().getId().equals(userId)) room.setExitedByBuyer(true);
        else if (room.getSeller().getId().equals(userId)) room.setExitedBySeller(true);
        else throw new RuntimeException("권한 없음");
        chatRoomRepository.save(room);
    }

    public ChatRoomResponse getRoom(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방 없음"));
        return ChatRoomResponse.from(room, "", 0, null, "");
    }

    public Page<ChatRoomResponse> getUserChatRooms(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastMessageAt").descending());
        Page<ChatRoom> chatRooms = chatRoomRepository.findActiveRooms(userId, pageable);
        return chatRooms.map(room -> ChatRoomResponse.from(room, "", 0, null, ""));
    }
}
