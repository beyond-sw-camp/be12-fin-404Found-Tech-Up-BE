package com.example.backend.review.model.dto;

import com.example.backend.product.model.Product;
import com.example.backend.review.model.Review;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "리뷰 등록/수정 요청 DTO")
public class ReviewRequestDto {
    @Schema(description = "리뷰 내용", example = "정말 만족스러운 제품입니다.")
    private String reviewContent;
    @Schema(description = "리뷰 평점 (1~5)", example = "5")
    private int reviewRating;
    @Schema(description = "리뷰 이미지 URL", example = "https://example.com/review-img.jpg")
    private String reviewImg;

    // 로그인한 유저 정보와, 어떤 제품에 작성되는 리뷰인지 받아와 엔티티로 반환
    public Review toEntity(User loginUser, Product product) {
        return Review.builder()
                .reviewContent(reviewContent)
                .reviewRating(reviewRating)
                .reviewImg(reviewImg)
                .reviewDate(LocalDateTime.now())
                .user(loginUser)
                .product(product)
                .build();
    }

}
