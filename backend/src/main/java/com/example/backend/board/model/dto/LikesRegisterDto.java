package com.example.backend.board.model.dto;

import com.example.backend.board.model.Board;
import com.example.backend.board.model.Likes;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikesRegisterDto {
    @Schema(description = "좋아요/싫어요 타입", example = "좋아요: True, 싫어요: False")
    private Boolean likesType;

    public Likes toEntity(User loginUser, Board board){
        return Likes.builder()
                .likesType(likesType)
                .board(board)
                .user(loginUser)
                .build();
    }
}
