package com.example.backend.board.model.dto;

import com.example.backend.board.model.Board;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPageResponse {
    @Schema(description = "현재 페이지", example = "1")
    private int page;

    @Schema(description = "한 페이지에 보여지는 게시글 수", example = "10")
    private int size;

    @Schema(description = "전체 게시글 수", example = "259")
    private long totalElements;

    @Schema(description = "전체 페이지 수", example = "26")
    private int totalPages;

    @Schema(description = "다음 페이지가 있는지 여부", example = "true")
    private boolean hasNext;

    @Schema(description = "이전 페이지가 있는지 여부", example = "false")
    private boolean hasPrevious;

    @Schema(description = "정렬 기준", example = "createdDate")
    private String sort;

    @Schema(description = "정렬 방향", example = "desc")
    private String direction;

    @Schema(description = "게시글 목록")
    private List<BoardListItemDto> boardList;

    public static BoardPageResponse from(Page<Board> boardPage, String sort, String direction) {
        return BoardPageResponse.builder()
                .page(boardPage.getNumber())
                .size(boardPage.getSize())
                .totalElements(boardPage.getTotalElements())
                .totalPages(boardPage.getTotalPages())
                .hasNext(boardPage.hasNext())
                .hasPrevious(boardPage.hasPrevious())
                .sort(sort)
                .direction(direction)
                .boardList(boardPage.stream().map(BoardListItemDto::from).collect(Collectors.toList()))
                .build();
    }
}
