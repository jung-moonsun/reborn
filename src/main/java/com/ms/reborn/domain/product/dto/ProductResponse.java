package com.ms.reborn.domain.product.dto;

import com.ms.reborn.domain.product.entity.Product;
import com.ms.reborn.domain.product.entity.ProductImage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String title;
    private String description;
    private int price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
    private String userNickname;
    private List<String> imageUrls;

    public static ProductResponse from(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .title(p.getTitle())
                .description(p.getDescription())
                .price(p.getPrice())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .userId(p.getUser().getId())
                .userNickname(p.getUser().getNickname())
                .imageUrls(p.getImages() != null ?
                        p.getImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList())
                        : null)
                .build();
    }
}
