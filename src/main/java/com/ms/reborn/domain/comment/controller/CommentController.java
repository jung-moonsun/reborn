package com.ms.reborn.domain.comment.controller;

import com.ms.reborn.domain.comment.dto.CommentRequest;
import com.ms.reborn.domain.comment.dto.CommentResponse;
import com.ms.reborn.domain.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 등록", description = "상품(게시글)에 댓글/대댓글 등록")
    @ApiResponse(responseCode = "201", description = "댓글 등록 성공")
    @PostMapping
    public ResponseEntity<Void> addComment(
            @RequestBody @Valid CommentRequest req,
            @RequestParam Long userId // 실무에서는 인증객체에서 꺼내서 씀
    ) {
        commentService.addComment(req, userId);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "댓글 수정", description = "본인 댓글 내용 수정")
    @ApiResponse(responseCode = "204", description = "수정 성공")
    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentRequest req,
            @RequestParam Long userId
    ) {
        commentService.updateComment(commentId, req.getContent(), userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "댓글 삭제", description = "본인 댓글 삭제")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long userId
    ) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "댓글 목록 조회", description = "상품(게시글)별 댓글/대댓글 페이징 조회")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getComments(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<CommentResponse> comments = commentService.getComments(productId, page, size);
        return ResponseEntity.ok(comments);
    }
}
