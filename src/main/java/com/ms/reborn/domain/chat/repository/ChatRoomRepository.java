package com.ms.reborn.domain.chat.repository;

import com.ms.reborn.domain.chat.entity.ChatRoom;
import com.ms.reborn.domain.product.entity.Product;
import com.ms.reborn.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByProductAndBuyer(Product product, User buyer);

    @Query("""
        SELECT r FROM ChatRoom r
        WHERE (r.buyer.id = :userId AND r.exitedByBuyer = false)
           OR (r.seller.id = :userId AND r.exitedBySeller = false)
    """)
    Page<ChatRoom> findActiveRooms(@Param("userId") Long userId, Pageable pageable);
}
