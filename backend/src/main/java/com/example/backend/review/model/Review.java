package com.example.backend.review.model;

import com.example.backend.product.model.Product;
import com.example.backend.user.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "리뷰 정보")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "리뷰 고유 식별자", example = "1")
    private Long reviewIdx;

    @Schema(description = "리뷰 평점 (1~5)", example = "5")
    private int reviewRating;

    @Schema(description = "리뷰 내용", example = "정말 좋은 제품이에요!")
    private String reviewContent;

    @Schema(description = "리뷰 이미지 URL", example = "https://example.com/review-img.jpg")
    private String reviewImg;

    @Schema(description = "리뷰 작성 날짜", example = "2025-04-01 14:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime reviewDate;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    @Schema(description = "리뷰 작성자 유저")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_idx")
    @Schema(description = "리뷰가 작성된 제품")
    private Product product;
}
