package com.ms.reborn.domain.comment.controller;

import com.ms.reborn.domain.comment.dto.CommentRequest;
import com.ms.reborn.domain.comment.dto.CommentResponse;
import com.ms.reborn.domain.comment.service.CommentService;
import com.ms.reborn.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 목록 트리 조회", description = "모든 댓글과 대댓글을 트리 구조로 조회합니다.")
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getCommentsAsTree(
            @RequestParam("productId") Long productId
    ) {
        List<CommentResponse> comments = commentService.getCommentsAsTree(productId);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @Operation(summary = "댓글/대댓글 등록", description = "댓글 또는 대댓글을 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addComment(
            @Valid @RequestBody CommentRequest req,
            @RequestParam Long userId
    ) {
        commentService.addComment(req, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
    }

    @Operation(summary = "댓글 수정", description = "본인 댓글을 수정합니다.")
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest req,
            @RequestParam Long userId
    ) {
        commentService.updateComment(commentId, req.getContent(), userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "댓글 삭제", description = "본인 댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long userId
    ) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}