package com.ms.reborn.domain.comment.entity;

import com.ms.reborn.domain.product.entity.Product;
import com.ms.reborn.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product; // 댓글이 달리는 대상(게시글, 상품 등)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // ✅ writer → user_id로 연결
    private User writer;

    @Column(nullable = false, length = 500)
    private String content;

    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt; // ✅ 수정 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent; // 대댓글

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
