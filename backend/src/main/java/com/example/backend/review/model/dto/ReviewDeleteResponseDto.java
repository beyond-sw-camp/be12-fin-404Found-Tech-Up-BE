package com.example.backend.review.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 삭제 응답 DTO")
public class ReviewDeleteResponseDto {
    @Schema(description = "삭제된 리뷰의 고유 번호", example = "1")
    private Long reviewIdx;

    public static ReviewDeleteResponseDto from(Long reviewIdx) {
        return ReviewDeleteResponseDto.builder()
                .reviewIdx(reviewIdx)
                .build();
    }
}
