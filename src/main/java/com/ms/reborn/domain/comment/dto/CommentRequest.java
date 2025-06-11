package com.ms.reborn.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequest {
    private Long productId;      // 댓글 대상(상품 등) ID
    private Long parentId;       // 대댓글 부모 ID(null 가능)

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;
}
