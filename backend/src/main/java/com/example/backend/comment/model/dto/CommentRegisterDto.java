package com.example.backend.comment.model.dto;

import com.example.backend.board.model.Board;
import com.example.backend.comment.model.Comment;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentRegisterDto {
    @Schema(description = "댓글 내용", example = "댓글 내용을 작성해주세요.")
    private String commentContent;

    public Comment toEntity(User loginUser, Board board){
        return Comment.builder()
                .commentContent(commentContent)
                .commentCreated(LocalDateTime.now())
                .board(board)
                .user(loginUser)
                .build();
    }
}
