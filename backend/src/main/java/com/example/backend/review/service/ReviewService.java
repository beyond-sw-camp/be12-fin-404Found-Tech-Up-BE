package com.example.backend.review.service;

import com.example.backend.review.model.Review;
import com.example.backend.review.model.dto.ReviewRequestDto;
import com.example.backend.review.model.dto.ReviewResponseDto;
import com.example.backend.review.repository.ReviewRepository;
import com.example.backend.product.model.Product;
import com.example.backend.product.repository.ProductRepository;
import com.example.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    // 리뷰 작성
    public ReviewResponseDto createReview(User loginUser, Integer productIdx, ReviewRequestDto dto) {
        // 구매 검증 로직 추가 필요. 구매한 사람만 리뷰 작성 가능.
        Product product = productRepository.findById(productIdx.longValue())
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다."));
        Review review = dto.toEntity(loginUser, product);
        review = reviewRepository.save(review);
        return ReviewResponseDto.from(review);
    }

    // 리뷰 수정 (작성자인지 확인)
    public ReviewResponseDto updateReview(User loginUser, Long reviewIdx, ReviewRequestDto dto) {
        Review review = reviewRepository.findById(reviewIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));
        if (!review.getUser().getUserIdx().equals(loginUser.getUserIdx())) {
            throw new IllegalArgumentException("작성자만 리뷰 수정이 가능합니다.");
        }
        // 리뷰 내용을 업데이트합니다.
        review.setReviewContent(dto.getReviewContent());
        review.setReviewRating(dto.getReviewRating());
        review.setReviewImg(dto.getReviewImg());
        review = reviewRepository.save(review);
        return ReviewResponseDto.from(review);
    }

    // 리뷰 삭제 (작성자인지 확인)
    public void deleteReview(User loginUser, Long reviewIdx) {
        Review review = reviewRepository.findById(reviewIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));
        if (!review.getUser().getUserIdx().equals(loginUser.getUserIdx())) {
            throw new IllegalArgumentException("작성자만 리뷰 삭제가 가능합니다.");
        }
        reviewRepository.delete(review);
    }
}
