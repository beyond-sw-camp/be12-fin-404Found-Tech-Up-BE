package com.example.backend.board.model.dto;

import com.example.backend.board.model.Board;
import com.example.backend.user.model.User;
import com.example.backend.util.HtmlSanitizer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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

    public Board toEntity(User loginUser) {
        return Board.builder()
                .boardTitle(boardTitle)
                .boardContent(HtmlSanitizer.sanitize(boardContent))  // 🔐 여기서 정제
                .boardCategory(boardCategory)
                .boardCreated(LocalDateTime.now())
                .user(loginUser)
                .boardLikes(0)
                .boardUnlikes(0)
                .boardComments(0)
                .build();
    }


}
