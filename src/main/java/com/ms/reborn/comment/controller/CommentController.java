package com.ms.reborn.comment.controller;

import com.ms.reborn.comment.dto.CommentRequest;
import com.ms.reborn.comment.dto.CommentResponse;
import com.ms.reborn.comment.service.CommentService;
import com.ms.reborn.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/{productId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @PathVariable Long productId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal String email
    ) {
        Long userId = userService.findUserIdByEmail(email);
        return ResponseEntity.ok(commentService.create(productId, userId, request));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getAll(@PathVariable Long productId) {
        return ResponseEntity.ok(commentService.findAllByProduct(productId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> update(
            @PathVariable Long productId,
            @PathVariable Long commentId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal String email
    ) {
        Long userId = userService.findUserIdByEmail(email);
        return ResponseEntity.ok(commentService.update(commentId, userId, request));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long productId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal String email
    ) {
        Long userId = userService.findUserIdByEmail(email);
        commentService.delete(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
