package com.ms.reborn.product.dto;

import com.ms.reborn.product.entity.Product;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import java.time.LocalDateTime;

@Getter
public class ProductResponse {
    private Long id;
    private String title;
    private String description;
    private int price;
    private String status;
    private Long userId;
    private List<String> imageUrls;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.status = product.getStatus();
        this.userId = product.getUser().getId();
        this.imageUrls = product.getImages() == null
                ? List.of()
                : product.getImages().stream()
                .map(image -> image.getImageUrl())
                .collect(Collectors.toList());
    }
}
