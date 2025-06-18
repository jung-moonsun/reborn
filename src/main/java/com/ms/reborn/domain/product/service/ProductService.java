package com.ms.reborn.domain.product.service;

import com.ms.reborn.domain.file.service.FileStorageService;
import com.ms.reborn.domain.product.dto.ProductRequest;
import com.ms.reborn.domain.product.dto.ProductResponse;
import com.ms.reborn.domain.product.entity.Product;
import com.ms.reborn.domain.product.entity.ProductImage;
import com.ms.reborn.domain.product.repository.ProductRepository;
import com.ms.reborn.domain.user.repository.UserRepository;
import com.ms.reborn.global.exception.CustomException;
import com.ms.reborn.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long addProduct(ProductRequest req, Long userId, List<MultipartFile> images, FileStorageService fileStorageService) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        var product = Product.builder()
                .user(user)
                .title(req.getTitle())
                .description(req.getDescription())
                .price(req.getPrice())
                .build();

        if (images != null) {
            for (MultipartFile image : images) {
                var url = fileStorageService.storeFile(image, "products");
                product.getImages().add(
                        ProductImage.builder()
                                .imageUrl(url)
                                .product(product)
                                .build()
                );
            }
        }
        productRepository.save(product);
        return product.getId();
    }

    @Transactional
    public void updateProduct(Long productId, ProductRequest req, Long userId) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!product.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
        product.setTitle(req.getTitle());
        product.setDescription(req.getDescription());
        product.setPrice(req.getPrice());
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long productId, Long userId) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (!product.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long productId) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> listProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return productRepository.findAll(pageable)
                .map(ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getMyProducts(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return productRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return productRepository.findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(keyword, pageable)
                .map(ProductResponse::from);
    }
}
