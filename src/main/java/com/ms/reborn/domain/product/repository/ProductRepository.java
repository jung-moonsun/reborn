package com.ms.reborn.domain.product.repository;

import com.ms.reborn.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String keyword, Pageable pageable);


    Page<Product> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Product p WHERE p.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
