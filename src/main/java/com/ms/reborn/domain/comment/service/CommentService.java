package com.ms.reborn.domain.comment.service;

import com.ms.reborn.domain.comment.dto.CommentRequest;
import com.ms.reborn.domain.comment.dto.CommentResponse;
import com.ms.reborn.domain.comment.entity.Comment;
import com.ms.reborn.domain.comment.repository.CommentRepository;
import com.ms.reborn.domain.product.repository.ProductRepository;
import com.ms.reborn.domain.user.repository.UserRepository;
import com.ms.reborn.global.exception.CustomException;
import com.ms.reborn.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void addComment(CommentRequest req, Long userId) {
        var product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        var writer = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Comment parent = null;
        if (req.getParentId() != null) {
            parent = commentRepository.findById(req.getParentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        }

        var comment = Comment.builder()
                .product(product)
                .writer(writer)
                .content(req.getContent())
                .parent(parent)
                .createdAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);
    }

    @Transactional
    public void updateComment(Long commentId, String content, Long userId) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        if (!comment.getWriter().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
        comment.setContent(content);
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        if (!comment.getWriter().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsAsTree(Long productId) {
        List<Comment> flatList = commentRepository.findAllByProductId(productId, Sort.by("createdAt"));

        Map<Long, CommentResponse> map = new HashMap<>();
        List<CommentResponse> roots = new ArrayList<>();

        // 1. 모든 댓글을 DTO로 변환 후 Map에 저장
        for (Comment c : flatList) {
            map.putIfAbsent(c.getId(), CommentResponse.from(c));
        }

        // 2. 부모-자식 관계 구성
        for (Comment c : flatList) {
            Long parentId = c.getParent() != null ? c.getParent().getId() : null;
            CommentResponse current = map.get(c.getId());

            if (parentId == null) {
                roots.add(current);
            } else {
                CommentResponse parent = map.get(parentId);
                if (parent != null) {
                    boolean exists = parent.getReplies().stream()
                            .anyMatch(r -> r.getId().equals(current.getId()));
                    if (!exists) {
                        parent.getReplies().add(current);
                    }
                }
            }
        }

        return roots;
    }
}
