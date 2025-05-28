package com.ms.reborn.chat.repository;

import com.ms.reborn.chat.entity.ChatRoom;
import com.ms.reborn.product.entity.Product;
import com.ms.reborn.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByProductAndBuyer(Product product, User buyer);
}
