package com.example.backend.board.model.dto;

import com.example.backend.board.model.Board;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponseDto {
    @Schema(description = "작성된 게시글의 고유 아이디", example = "1")
    private Long idx;
    @Schema(description = "작성된 게시글의 제목", example = "공지사항 제목입니다.")
    private String boardTitle;
    @Schema(description = "작성된 게시글의 내용", example = "공지사항 내용입니다.")
    private String boardContent;
    @Schema(description = "작성된 게시글의 작성자", example = "작성자01")
    private String writer;
    @Schema(description = "게시글이 작성된 시간", example = "2025-03-05")
    private LocalDateTime boardCreated;
    @Schema(description = "게시글이 수정된 시간", example = "2025-03-05")
    private LocalDateTime boardModified;
    @Schema(description = "게시글의 좋아요 수", example = "10")
    private Integer boardLikes;
    @Schema(description = "게시글의 싫어요 수", example = "3")
    private Integer boardUnlikes;
    @Schema(description = "게시글 작성중 첨부한 파일을 저장하기 위한 preSignedUrl", example = "")
    private List<String> preSignedUrls;
    @Schema(description = "게시글이 s3에 저장된 파일의 이름", example = "")
    private List<String> imageKeys;

    public static BoardResponseDto from(Board board) {
        return BoardResponseDto.builder()
                .idx(board.getIdx())
                .boardTitle(board.getBoardTitle())
                .boardContent(board.getBoardContent())
//                .writer(board.getUser().getName())
                .boardCreated(board.getBoardCreated())
                .boardModified(board.getBoardModified())
                .preSignedUrls(null)
                .build();
    }

//    public static BoardResponseDto from(Board board, List<String> preSignedUrls) {
//        List<String> imageKeys = board.getImageList() != null
//                ? board.getImageList().stream()
//                .map(BoardImage::getUrl)
//                .collect(Collectors.toList())
//                : Collections.emptyList();
//
//        return BoardResponseDto.builder()
//                .title(board.getTitle())
//                .writer(board.getUser().getName())
//                .createdDate(board.getCreatedDate())
//                .preSignedUrls(preSignedUrls)
//                .imageKeys(imageKeys)
//                .build();
//    }
}
