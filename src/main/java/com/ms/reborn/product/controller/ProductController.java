package com.ms.reborn.product.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.reborn.product.dto.ProductRequest;
import com.ms.reborn.product.dto.ProductResponse;
import com.ms.reborn.product.service.ProductService;
import com.ms.reborn.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(
            @RequestPart("product") String productJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal String email) throws Exception {

        ProductRequest request = objectMapper.readValue(productJson, ProductRequest.class);
        Long userId = userService.findUserIdByEmail(email);

        ProductResponse response = productService.createProduct(request, userId, images);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long productId) {
        ProductResponse response = productService.getProduct(productId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestPart("product") String productJson,  // String으로 받기
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal String email) throws Exception {  // Exception 추가

        ProductRequest request = objectMapper.readValue(productJson, ProductRequest.class);

        Long userId = userService.findUserIdByEmail(email);
        ProductResponse response = productService.updateProduct(productId, request, userId, images);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId,
                                              @AuthenticationPrincipal String email) {
        Long userId = userService.findUserIdByEmail(email);
        productService.deleteProduct(productId, userId);
        return ResponseEntity.noContent().build();
    }
}

