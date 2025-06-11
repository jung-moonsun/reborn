package com.ms.reborn.domain.product.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.reborn.domain.file.service.FileStorageService;
import com.ms.reborn.domain.product.dto.ProductRequest;
import com.ms.reborn.domain.product.dto.ProductResponse;
import com.ms.reborn.domain.product.service.ProductService;
import com.ms.reborn.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    @Operation(
            summary = "상품 등록",
            description = "상품 정보를 등록합니다. 이미지 파일은 선택입니다. (multipart/form-data 형식)"
    )
    @SecurityRequirement(name = "JWT")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> addProduct(
            @Parameter(description = "상품 정보 (JSON 문자열)", required = true)
            @RequestPart("product") String productJson,

            @Parameter(description = "상품 이미지 파일 리스트 (선택)", required = false)
            @RequestPart(value = "images", required = false) List<MultipartFile> images,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        ProductRequest req;

        try {
            req = objectMapper.readValue(productJson, ProductRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid product JSON", e);
        }

        productService.addProduct(req, userId, images, fileStorageService);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "상품 수정", description = "자신이 올린 상품만 수정 가능")
    @SecurityRequirement(name = "JWT")
    @PutMapping("/{productId}")
    public ResponseEntity<Void> updateProduct(
            @PathVariable Long productId,
            @RequestBody @Valid ProductRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        productService.updateProduct(productId, req, userDetails.getUser().getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "상품 삭제", description = "자신이 올린 상품만 삭제 가능")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        productService.deleteProduct(productId, userDetails.getUser().getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "상품 상세 조회")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @Operation(summary = "상품 검색(키워드/페이징)")
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(productService.searchProducts(keyword, page, size));
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "내 상품 목록 조회")
    @GetMapping("/me")
    public ResponseEntity<Page<ProductResponse>> getMyProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(productService.getMyProducts(userDetails.getUser().getId(), page, size));
    }


}
