package com.example.backend.board.model.dto;

import com.example.backend.board.model.Board;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardRegisterRequestDto {
    @Schema(description = "게시글의 제목", example = "게시판 제목입니다.")
    private String boardTitle;

    @Schema(description = "게시글의 내용", example = "게시판 내용입니다.")
    private String boardContent;

    @Schema(description = "게시글의 카테고리", example = "추천/후기/QnA")
    private String boardCategory;

    @Schema(description = "게시글 첨부파일 URL 목록", example = "[\"https://s3.amazonaws.com/your-bucket/file1.jpg\", \"https://s3.amazonaws.com/your-bucket/file2.pdf\"]")
    private List<String> files = new ArrayList<>();  // 프리사인드 URL 방식으로 S3 업로드 후 최종 URL들을 클라이언트에서 전달받음

    // 로그인한 유저 정보를 받아 Board 엔티티로 변환 (파일 정보는 별도 등록 처리)
    public Board toEntity(User loginUser) {
        return Board.builder()
                .boardTitle(boardTitle)
                .boardContent(boardContent)
                .boardCategory(boardCategory)
                .boardCreated(LocalDateTime.now())
                .boardModified(null)
                .user(loginUser)
                .boardLikes(0)    // 초기값 설정 (예: 0)
                .boardUnlikes(0)  // 초기값 설정
                .boardComments(0) // 초기값 설정
                .build();
    }
}
