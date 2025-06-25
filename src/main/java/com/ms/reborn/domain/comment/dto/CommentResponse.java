package com.ms.reborn.domain.comment.dto;

import com.ms.reborn.domain.comment.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long id;
    private Long writerId;
    private String writerNickname;
    private String content;
    private LocalDateTime createdAt;
    private Long parentId;
    private List<CommentResponse> replies;

    public static CommentResponse from(Comment c) {
        return CommentResponse.builder()
                .id(c.getId())
                .writerId(c.getWriter().getId())
                .writerNickname(c.getWriter().getNickname())
                .content(c.getContent())
                .createdAt(c.getCreatedAt())
                .parentId(c.getParent() != null ? c.getParent().getId() : null)
                .replies(c.getReplies() != null ?
                        c.getReplies().stream().map(CommentResponse::from).collect(Collectors.toList())
                        : null)
                .build();
    }
}
