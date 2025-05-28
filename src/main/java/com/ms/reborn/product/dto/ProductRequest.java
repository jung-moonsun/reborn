package com.ms.reborn.product.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ProductRequest {
    private String title;
    private String description;
    private int price;
    private String status;  // 추가

    private List<MultipartFile> images;
}