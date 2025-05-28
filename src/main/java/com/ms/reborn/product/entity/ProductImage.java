package com.ms.reborn.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "product_images")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상품과 다대일 관계 (상품 하나에 여러 이미지)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 이미지 URL (S3 같은 외부 저장소 링크로 관리할 예정)
    @Column(nullable = false)
    private String imageUrl;
}
