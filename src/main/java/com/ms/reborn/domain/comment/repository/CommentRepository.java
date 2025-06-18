package com.ms.reborn.domain.comment.repository;

import com.ms.reborn.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByProductIdAndParentIsNull(Long productId, Pageable pageable);
    Page<Comment> findByParentId(Long parentId, Pageable pageable);

    // ✅ 전체 트리 조회용
    List<Comment> findAllByProductId(Long productId, Sort sort);
}