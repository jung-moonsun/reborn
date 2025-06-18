package com.ms.reborn.domain.chat.service;

import com.ms.reborn.global.exception.CustomException;
import com.ms.reborn.global.exception.ErrorCode;
import com.ms.reborn.domain.chat.dto.ChatRoomResponse;
import com.ms.reborn.domain.chat.entity.ChatRoom;
import com.ms.reborn.domain.chat.repository.ChatRoomRepository;
import com.ms.reborn.domain.product.entity.Product;
import com.ms.reborn.domain.product.repository.ProductRepository;
import com.ms.reborn.domain.user.entity.User;
import com.ms.reborn.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
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
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (buyer.isDeleted()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        User seller = product.getUser();
        if (seller.isDeleted()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        Optional<ChatRoom> exitedRoomOpt = chatRoomRepository.findByProductAndBuyerIncludingExited(product, buyer);

        ChatRoom room = exitedRoomOpt.map(r -> {
            if (r.getBuyer().getId().equals(buyerId)) r.setExitedByBuyer(false);
            if (r.getSeller().getId().equals(buyerId)) r.setExitedBySeller(false);
            r.setLastMessageAt(LocalDateTime.now());
            return r;
        }).orElseGet(() -> {
            ChatRoom newRoom = ChatRoom.builder()
                    .product(product)
                    .buyer(buyer)
                    .seller(seller)
                    .createdAt(LocalDateTime.now())
                    .lastMessageAt(LocalDateTime.now())
                    .exitedByBuyer(false)
                    .exitedBySeller(false)
                    .build();
            return newRoom;
        });

        chatRoomRepository.save(room);
        return ChatRoomResponse.from(room, "", 0, null, "");
    }

    @Transactional
    public void exitRoom(Long roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (room.getBuyer().getId().equals(userId)) {
            room.setExitedByBuyer(true);
        } else if (room.getSeller().getId().equals(userId)) {
            room.setExitedBySeller(true);
        } else {
            throw new CustomException(ErrorCode.CHAT_ACCESS_DENIED);
        }

        chatRoomRepository.save(room);
    }

    public ChatRoomResponse getRoom(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        return ChatRoomResponse.from(room, "", 0, null, "");
    }

    public Page<ChatRoomResponse> getUserChatRooms(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastMessageAt").descending());
        Page<ChatRoom> chatRooms = chatRoomRepository.findActiveRooms(userId, pageable);
        return chatRooms.map(room -> ChatRoomResponse.from(room, "", 0, null, ""));
    }
}
