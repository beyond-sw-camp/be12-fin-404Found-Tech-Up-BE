package com.example.backend.review.service;

import com.example.backend.global.exception.ProductException;
import com.example.backend.global.exception.ReviewException;
import com.example.backend.global.response.responseStatus.ProductResponseStatus;
import com.example.backend.global.response.responseStatus.ReviewResponseStatus;
import com.example.backend.notification.model.NotificationType;
import com.example.backend.notification.model.dto.RealTimeNotificationDto;
import com.example.backend.review.model.Review;
import com.example.backend.review.model.dto.ReviewDeleteResponseDto;
import com.example.backend.review.model.dto.ReviewRequestDto;
import com.example.backend.review.model.dto.ReviewResponseDto;
import com.example.backend.review.repository.ReviewRepository;
import com.example.backend.product.model.Product;
import com.example.backend.product.repository.ProductRepository;
import com.example.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final KafkaTemplate<String, String> stringKafkaTemplate;

    @Transactional(readOnly=true)
    public List<ReviewResponseDto> getReviewsByProduct(Long productIdx) {
        Product p = productRepository.findById(productIdx)
                .orElseThrow(() -> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));

        List<Review> reviews = reviewRepository.findAllByProductOrderByReviewDateDesc(p);
        return reviews.stream()
                .map(ReviewResponseDto::from)
                .collect(Collectors.toList());
    }

    // 리뷰 작성
    public ReviewResponseDto createReview(User loginUser, Integer productIdx, ReviewRequestDto dto) {
        // 구매한 사람만 리뷰 작성 가능하다면 구매 검증 로직 추가 필요
        Product product = productRepository.findById(productIdx.longValue())
                .orElseThrow(() -> new ProductException(ProductResponseStatus.PRODUCT_NOT_FOUND));
        Review review = dto.toEntity(loginUser, product);
        review = reviewRepository.save(review);
        // Kafka 이벤트 발행
        stringKafkaTemplate.send("review-created-notifications", loginUser.getUserIdx().toString(), "new review register");
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
