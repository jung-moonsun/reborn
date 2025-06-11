package com.ms.reborn.domain.comment.service;

import com.ms.reborn.domain.comment.dto.CommentRequest;
import com.ms.reborn.domain.comment.dto.CommentResponse;
import com.ms.reborn.domain.comment.entity.Comment;
import com.ms.reborn.domain.comment.repository.CommentRepository;
import com.ms.reborn.domain.product.entity.Product;
import com.ms.reborn.domain.product.repository.ProductRepository;
import com.ms.reborn.domain.user.entity.User;
import com.ms.reborn.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void addComment(CommentRequest req, Long userId) {
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("상품 없음"));
        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("작성자 없음"));

        Comment parent = null;
        if (req.getParentId() != null) {
            parent = commentRepository.findById(req.getParentId())
                    .orElseThrow(() -> new RuntimeException("부모 댓글 없음"));
        }

        Comment comment = Comment.builder()
                .product(product)
                .writer(writer)
                .content(req.getContent())
                .parent(parent)
                .build();

        commentRepository.save(comment);
    }

    @Transactional
    public void updateComment(Long commentId, String newContent, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));
        if (!comment.getWriter().getId().equals(userId))
            throw new RuntimeException("수정 권한 없음");
        comment.setContent(newContent);
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));
        if (!comment.getWriter().getId().equals(userId))
            throw new RuntimeException("삭제 권한 없음");
        commentRepository.delete(comment);
    }

    @Transactional
    public Page<CommentResponse> getComments(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findByProductIdAndParentIsNull(productId, pageable);
        return comments.map(CommentResponse::from);
    }
}
