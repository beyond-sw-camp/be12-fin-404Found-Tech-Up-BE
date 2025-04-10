package com.example.backend.review.model.dto;

import com.example.backend.review.model.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "리뷰 응답 DTO")
public class ReviewResponseDto {
    @Schema(description = "리뷰 내용", example = "정말 만족스러운 제품입니다.")
    private String reviewContent;
    @Schema(description = "리뷰 평점 (1~5)", example = "5")
    private int reviewRating;
    @Schema(description = "리뷰 이미지 URL", example = "https://example.com/review-img.jpg")
    private String reviewImg;

    // 리뷰 작성후 결과 반환
    public static ReviewResponseDto from(Review review) {
        return ReviewResponseDto.builder()
                .reviewContent(review.getReviewContent())
                .reviewRating(review.getReviewRating())
                .reviewImg(review.getReviewImg())
                .build();
    }
}
