package com.ms.reborn.domain.product.service;

import com.ms.reborn.domain.file.service.FileStorageService;
import com.ms.reborn.domain.product.dto.ProductRequest;
import com.ms.reborn.domain.product.dto.ProductResponse;
import com.ms.reborn.domain.product.entity.Product;
import com.ms.reborn.domain.product.entity.ProductImage;
import com.ms.reborn.domain.product.repository.ProductRepository;
import com.ms.reborn.domain.user.entity.User;
import com.ms.reborn.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long addProduct(ProductRequest req, Long userId, List<MultipartFile> images, FileStorageService fileStorageService) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));
        Product product = Product.builder()
                .user(user)
                .title(req.getTitle())
                .description(req.getDescription())
                .price(req.getPrice())
                .build();

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String url = fileStorageService.storeFile(image, "products");
                product.getImages().add(ProductImage.builder().imageUrl(url).product(product).build());
            }
        }

        productRepository.save(product);
        return product.getId();
    }

    @Transactional
    public void updateProduct(Long productId, ProductRequest req, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품 없음"));
        if (!product.getUser().getId().equals(userId))
            throw new RuntimeException("수정 권한 없음");
        product.setTitle(req.getTitle());
        product.setDescription(req.getDescription());
        product.setPrice(req.getPrice());
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품 없음"));
        if (!product.getUser().getId().equals(userId))
            throw new RuntimeException("삭제 권한 없음");
        productRepository.delete(product);
    }

    @Transactional
    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품 없음"));
        return ProductResponse.from(product);
    }

    @Transactional
    public Page<ProductResponse> searchProducts(String keyword, int page, int size) {
        Page<Product> products = productRepository
                .findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(keyword, PageRequest.of(page, size));
        return products.map(ProductResponse::from);
    }

    @Transactional
    public Page<ProductResponse> getMyProducts(Long userId, int page, int size) {
        Page<Product> products = productRepository
                .findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
        return products.map(ProductResponse::from);
    }
}
