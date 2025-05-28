package com.ms.reborn.comment.repository;

import com.ms.reborn.comment.entity.Comment;
import com.ms.reborn.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProduct(Product product);
}
