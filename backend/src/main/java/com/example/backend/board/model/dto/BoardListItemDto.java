package com.example.backend.board.model.dto;

import com.example.backend.board.model.Board;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class BoardListItemDto {
    @Schema(description = "게시글 ID")
    private Long idx;

    @Schema(description = "게시글 제목")
    private String boardTitle;

    @Schema(description = "작성자")
    private String writer;

    @Schema(description = "작성일")
    private String boardCreated;

    @Schema(description = "좋아요 수")
    private Integer boardLikes;

    @Schema(description = "싫어요 수")
    private Integer boardUnlikes;

    @Schema(description = "댓글 수")
    private Integer boardComments;

    @Schema(description = "게시글 카테고리")
    private String boardCategory;

    public static BoardListItemDto from(Board board) {
        return BoardListItemDto.builder()
                .idx(board.getIdx())
                .boardTitle(board.getBoardTitle())
                .writer(board.getUser() != null ? board.getUser().getUserNickname() : "탈퇴한 사용자")
                .boardCreated(board.getBoardCreated() != null ? board.getBoardCreated().toString() : null)
                .boardLikes(board.getBoardLikes())
                .boardUnlikes(board.getBoardUnlikes())
                .boardComments(board.getBoardComments())
                .boardCategory(board.getBoardCategory())
                .build();
    }
}

