package com.example.backend.review.service;

import com.example.backend.global.exception.ProductException;
import com.example.backend.global.exception.ReviewException;
import com.example.backend.global.response.responseStatus.ProductResponseStatus;
import com.example.backend.global.response.responseStatus.ReviewResponseStatus;
import com.example.backend.review.model.Review;
import com.example.backend.review.model.dto.ReviewDeleteResponseDto;
import com.example.backend.review.model.dto.ReviewRequestDto;
import com.example.backend.review.model.dto.ReviewResponseDto;
import com.example.backend.review.repository.ReviewRepository;
import com.example.backend.product.model.Product;
import com.example.backend.product.repository.ProductRepository;
import com.example.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly=true)
    public List<ReviewResponseDto> getReviewsByProduct(Long productIdx) {
        Product p = productRepository.findById(productIdx)
                .orElseThrow(() -> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));

        return reviewRepository.findAll().stream()
                .filter(r -> r.getProduct().getProductIdx().equals(productIdx))
                .sorted(Comparator.comparing(Review::getReviewDate).reversed())
                .map(ReviewResponseDto::from)
                .collect(Collectors.toList());
    }

    // 리뷰 작성
    public ReviewResponseDto createReview(User loginUser, Integer productIdx, ReviewRequestDto dto) {
        // 구매 검증 로직 추가 필요. 구매한 사람만 리뷰 작성 가능.
        Product product = productRepository.findById(productIdx.longValue())
                .orElseThrow(() -> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));
        Review review = dto.toEntity(loginUser, product);
        review = reviewRepository.save(review);
        return ReviewResponseDto.from(review);
    }

    // 리뷰 수정 (작성자인지 확인)
    public ReviewResponseDto updateReview(User loginUser, Long reviewIdx, ReviewRequestDto dto) {
        Review review = reviewRepository.findById(reviewIdx)
                .orElseThrow(() -> new ReviewException(ReviewResponseStatus.REVIEW_NOT_FOUND));
        if (!review.getUser().getUserIdx().equals(loginUser.getUserIdx())) {
            throw new ReviewException(ReviewResponseStatus.REVIEW_USER_MISMATCH);
        }
        // 리뷰 내용을 업데이트합니다.
        review.setReviewContent(dto.getReviewContent());
        review.setReviewRating(dto.getReviewRating());
        review.setReviewImg(dto.getReviewImg());
        review = reviewRepository.save(review);
        return ReviewResponseDto.from(review);
    }

    // 리뷰 삭제 (작성자인지 확인)
    public ReviewDeleteResponseDto deleteReview(User loginUser, Long reviewIdx) {
        Review review = reviewRepository.findById(reviewIdx)
                .orElseThrow(() -> new ReviewException(ReviewResponseStatus.REVIEW_NOT_FOUND));
        if (!review.getUser().getUserIdx().equals(loginUser.getUserIdx())) {
            throw new ReviewException(ReviewResponseStatus.REVIEW_USER_MISMATCH);
        }
        reviewRepository.delete(review);
        return ReviewDeleteResponseDto.from(reviewIdx);
    }
}
