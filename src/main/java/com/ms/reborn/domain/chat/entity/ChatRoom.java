package com.ms.reborn.domain.chat.entity;

import com.ms.reborn.domain.product.entity.Product;
import com.ms.reborn.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)) // ✅ 여기 수정
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    private User seller;

    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean exitedByBuyer = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean exitedBySeller = false;

//    private LocalDateTime buyerExitedAt;
//    private LocalDateTime sellerExitedAt;
}