package com.example.backend.board.model.dto;

import com.example.backend.board.model.Board;
import com.example.backend.board.model.BoardFiles;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardRegisterResponseDto {

    @Schema(description = "게시글 고유 식별자", example = "1")
    private Long idx;

    @Schema(description = "게시글 제목", example = "공지사항 제목입니다.")
    private String boardTitle;

    @Schema(description = "게시글 내용", example = "공지사항 내용입니다.")
    private String boardContent;

    @Schema(description = "게시글 카테고리", example = "자유")
    private String boardCategory;

    @Schema(description = "게시글 작성 시간", example = "2025-03-05T10:15:30")
    private LocalDateTime boardCreated;

    @Schema(description = "게시글 작성자", example = "작성자01")
    private String writer;

    @Schema(description = "게시글 첨부파일의 preSignedUrl 목록", example = "[\"https://s3.amazonaws.com/bucket/file1.jpg\", \"https://s3.amazonaws.com/bucket/file2.pdf\"]")
    private List<String> preSignedUrls;

    //@Schema(description = "S3에 저장된 첨부파일의 키 목록", example = "[\"board_files/abc.jpg\", \"board_files/def.pdf\"]")
    //private List<String> imageKeys;

    /**
     * 첨부파일 관련 정보 없이 게시글 기본 정보만 반환
     */
    public static BoardRegisterResponseDto from(Board board) {
        return BoardRegisterResponseDto.builder()
                .idx(board.getIdx())
                .boardTitle(board.getBoardTitle())
                .boardContent(board.getBoardContent())
                .boardCategory(board.getBoardCategory())
                .boardCreated(board.getBoardCreated())
                .writer(board.getUser().getUserNickname())
                .preSignedUrls(null)
      //          .imageKeys(Collections.emptyList())
                .build();
    }

    /**
     * preSignedUrl 목록과 이미지 키(첨부파일 URL들)를 함께 포함하여 반환
     */
    public static BoardRegisterResponseDto from(Board board, List<String> preSignedUrls) {
        /*
        List<String> imageKeys = board.getImageList() != null
                ? board.getImageList().stream()
                .map(BoardFiles::getFilesUrl)
                .collect(Collectors.toList())
                : Collections.emptyList();
         */
        return BoardRegisterResponseDto.builder()
                .idx(board.getIdx())
                .boardTitle(board.getBoardTitle())
                .boardContent(board.getBoardContent())
                .boardCategory(board.getBoardCategory())
                .boardCreated(board.getBoardCreated())
                //.writer(board.getUser().getUserNickname())
                .preSignedUrls(preSignedUrls)
                //.imageKeys(imageKeys)
                .build();
    }
}