package com.ms.reborn.product.repository;

import com.ms.reborn.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 추가적인 쿼리 필요하면 여기다 작성
}
