package com.example.backend.board.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikesRegisterDto {
    private Boolean likesType; // true = 좋아요, false = 싫어요
}
