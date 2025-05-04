package com.example.backend.comment.model.dto;

import com.example.backend.comment.model.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponseDto {
    private Long commentIdx;
    private Long userIdx;
    private String writer;
    private String content;
    private LocalDateTime createdAt;

    public static CommentResponseDto from(Comment comment) {
        return CommentResponseDto.builder()
                .commentIdx(comment.getIdx())
                .userIdx(comment.getUser() != null ? comment.getUser().getUserIdx() : null)
                .writer(comment.getUser() != null ? comment.getUser().getUserNickname() : "탈퇴한 사용자")
                .content(comment.getCommentContent())
                .createdAt(comment.getCommentCreated())
                .build();
    }
}
