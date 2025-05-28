package com.ms.reborn.product.service;

import com.ms.reborn.file.service.FileStorageService;
import com.ms.reborn.product.dto.ProductRequest;
import com.ms.reborn.product.dto.ProductResponse;
import com.ms.reborn.product.entity.Product;
import com.ms.reborn.product.entity.ProductImage;
import com.ms.reborn.product.repository.ProductRepository;
import com.ms.reborn.user.entity.User;
import com.ms.reborn.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public ProductResponse createProduct(ProductRequest request, Long userId, List<MultipartFile> images) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Product product = Product.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .status(request.getStatus() != null ? request.getStatus() : "판매중")
                .images(new ArrayList<>())  // 여기 추가
                .build();

        productRepository.save(product);

        if (images != null) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String imageUrl = fileStorageService.storeFile(image);

                    ProductImage productImage = new ProductImage();
                    productImage.setImageUrl(imageUrl);
                    productImage.setProduct(product);

                    product.addImage(productImage);
                }
            }
            productRepository.save(product);
        }

        return new ProductResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }

    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        return new ProductResponse(product);
    }

    public ProductResponse updateProduct(Long productId, ProductRequest request, Long userId, List<MultipartFile> newImages) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        if (!product.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }

        if (newImages != null && !newImages.isEmpty()) {
            product.clearImages();

            for (MultipartFile image : newImages) {
                if (!image.isEmpty()) {
                    String imageUrl = fileStorageService.storeFile(image);

                    ProductImage productImage = new ProductImage();
                    productImage.setImageUrl(imageUrl);
                    productImage.setProduct(product);

                    product.addImage(productImage);
                }
            }
            productRepository.save(product);
        }

        return new ProductResponse(product);
    }

    public void deleteProduct(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        if (!product.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        productRepository.delete(product);
    }
}
