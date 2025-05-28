package com.ms.reborn.chat.service;

import com.ms.reborn.chat.dto.ChatRoomResponse;
import com.ms.reborn.chat.entity.ChatRoom;
import com.ms.reborn.chat.repository.ChatRoomRepository;
import com.ms.reborn.product.entity.Product;
import com.ms.reborn.product.repository.ProductRepository;
import com.ms.reborn.user.entity.User;
import com.ms.reborn.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
                    .build();
            return chatRoomRepository.save(newRoom);
        });

        return ChatRoomResponse.from(room);
    }

    public ChatRoomResponse getRoom(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방 없음"));

        return ChatRoomResponse.from(room);
    }
}

