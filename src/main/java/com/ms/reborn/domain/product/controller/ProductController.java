package com.ms.reborn.domain.product.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.reborn.domain.file.service.FileStorageService;
import com.ms.reborn.domain.product.dto.ProductRequest;
import com.ms.reborn.domain.product.dto.ProductResponse;
import com.ms.reborn.domain.product.service.ProductService;
import com.ms.reborn.global.response.ApiResponse;
import com.ms.reborn.global.exception.CustomException;
import com.ms.reborn.global.exception.ErrorCode;
import com.ms.reborn.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "상품 등록", description = "상품 정보를 등록합니다. 이미지 파일은 선택입니다. (multipart/form-data)")
    @SecurityRequirement(name = "JWT")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> addProduct(
            @Parameter(description = "상품 정보 (JSON)")
            @RequestPart("product") String productJson,
            @Parameter(description = "상품 이미지 파일 리스트")
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        try {
            var req = objectMapper.readValue(productJson, ProductRequest.class);
            productService.addProduct(req, userId, images, fileStorageService);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(null));
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
    }

    @Operation(summary = "상품 수정", description = "자신이 올린 상품만 수정 가능")
    @SecurityRequirement(name = "JWT")
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        productService.updateProduct(productId, req, userDetails.getUser().getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "상품 삭제", description = "자신이 올린 상품만 삭제 가능")
    @SecurityRequirement(name = "JWT")
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        productService.deleteProduct(productId, userDetails.getUser().getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "상품 상세 조회")
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @PathVariable Long productId
    ) {
        var resp = productService.getProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(resp));
    }

    @Operation(summary = "상품 검색(키워드/페이징)")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var results = productService.searchProducts(keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @Operation(summary = "내 상품 목록 조회")
    @SecurityRequirement(name = "JWT")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getMyProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        var mine = productService.getMyProducts(userDetails.getUser().getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(mine));
    }
}
