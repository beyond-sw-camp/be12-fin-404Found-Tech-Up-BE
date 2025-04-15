package com.example.backend.board.model.dto;

import com.example.backend.board.model.Board;
import com.example.backend.board.model.BoardFiles;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponseDto {

    @Schema(description = "게시글 ID", example = "45")
    private Long idx;

    @Schema(description = "게시글 제목", example = "Spring Boot 프로젝트 게시글")
    private String boardTitle;

    @Schema(description = "게시글 본문 내용 (HTML 포함)", example = "<p>본문 내용입니다</p>")
    private String boardContent;

    @Schema(description = "작성자 이름", example = "홍길동")
    private String writer;

    @Schema(description = "작성자 ID", example = "5") // ✅ 추가
    private Long userIdx;

    @Schema(description = "게시글 생성 일시", example = "2025-04-14T15:18:31")
    private LocalDateTime boardCreated;

    @Schema(description = "게시글 수정 일시", example = "2025-04-14T16:10:00")
    private LocalDateTime boardModified;

    @Schema(description = "게시글 카테고리", example = "자유, Q&A")
    private String boardCategory;

    @Schema(description = "게시글 좋아요 수", example = "5")
    private Integer boardLikes;

    @Schema(description = "게시글 싫어요 수", example = "1")
    private Integer boardUnlikes;

    @Schema(description = "게시글 댓글 수", example = "3")
    private Integer boardComments;

    @Schema(description = "첨부파일 리스트")
    private List<BoardFileDto> fileList;

    public static BoardResponseDto from(Board board) {
        return BoardResponseDto.builder()
                .idx(board.getIdx())
                .boardTitle(board.getBoardTitle())
                .boardContent(board.getBoardContent())
                .writer(board.getUser() != null ? board.getUser().getUsername() : "탈퇴한 사용자")
                .userIdx(board.getUser() != null ? board.getUser().getUserIdx() : null) // ✅ 추가
                .boardCreated(board.getBoardCreated())
                .boardModified(board.getBoardModified())
                .boardCategory(board.getBoardCategory())
                .boardLikes(board.getBoardLikes())
                .boardUnlikes(board.getBoardUnlikes())
                .boardComments(board.getBoardComments())
                .fileList(
                        board.getImageList().stream()
                                .map(file -> BoardFileDto.builder()
                                        .filesName(file.getFilesName())
                                        .filesType(file.getFilesType())
                                        .filesUrl(file.getFilesUrl())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }
}
