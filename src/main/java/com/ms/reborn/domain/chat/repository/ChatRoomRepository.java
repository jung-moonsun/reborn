package com.ms.reborn.domain.chat.repository;

import com.ms.reborn.domain.chat.entity.ChatRoom;
import com.ms.reborn.domain.product.entity.Product;
import com.ms.reborn.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // ✅ 나간 방 제외하고 유저-상품 기준 방 조회 (기존 사용)
    @Query("""
        SELECT r FROM ChatRoom r
        WHERE r.product = :product
          AND r.buyer = :buyer
          AND r.exitedByBuyer = false
          AND r.exitedBySeller = false
    """)
    Optional<ChatRoom> findActiveByProductAndBuyer(@Param("product") Product product, @Param("buyer") User buyer);

    // ✅ 나간 방 포함한 전체 조회 (복원용)
    @Query("""
        SELECT r FROM ChatRoom r
        WHERE r.product = :product
          AND r.buyer = :buyer
    """)
    Optional<ChatRoom> findByProductAndBuyerIncludingExited(@Param("product") Product product, @Param("buyer") User buyer);

    // ✅ 현재 로그인한 유저가 참여 중인 채팅방 목록 (나간 방 제외)
    @Query("""
        SELECT r FROM ChatRoom r
        WHERE (r.buyer.id = :userId AND r.exitedByBuyer = false)
           OR (r.seller.id = :userId AND r.exitedBySeller = false)
    """)
    Page<ChatRoom> findActiveRooms(@Param("userId") Long userId, Pageable pageable);

    @Modifying
    @Query("""
    DELETE FROM ChatRoom r
    WHERE r.buyer.id = :userId OR r.seller.id = :userId
""")
    void deleteByUserId(@Param("userId") Long userId);
}
