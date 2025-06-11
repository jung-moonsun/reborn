package com.ms.reborn.domain.product.repository;

import com.ms.reborn.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // 검색 + 최신순 정렬
    Page<Product> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String keyword, Pageable pageable);

    // 내 상품만
    Page<Product> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
