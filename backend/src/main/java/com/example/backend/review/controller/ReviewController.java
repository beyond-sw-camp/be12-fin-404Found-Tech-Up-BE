package com.example.backend.review.controller;

import com.example.backend.global.exception.ReviewException;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseService;
import com.example.backend.global.response.responseStatus.ReviewResponseStatus;
import com.example.backend.review.model.dto.ReviewDeleteResponseDto;
import com.example.backend.review.model.dto.ReviewRequestDto;
import com.example.backend.review.model.dto.ReviewResponseDto;
import com.example.backend.review.service.ReviewService;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "리뷰 기능", description = "리뷰 기능 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final BaseResponseService baseResponseService;

    @Operation(summary="상품별 리뷰 조회", description="특정 상품에 달린 리뷰들을 날짜 최신순으로 가져옵니다.")
    @GetMapping("/product/{productIdx}")
    public BaseResponse<List<ReviewResponseDto>> listByProduct(
            @PathVariable Long productIdx
    ) {
        List<ReviewResponseDto> dtos = reviewService.getReviewsByProduct(productIdx);
        return baseResponseService.getSuccessResponse(dtos, ReviewResponseStatus.SUCCESS);
    }

    @Operation(
            summary = "리뷰 작성하기",
            description = """
                productIdx 를 전달받아, 로그인한 유저 정보를 확인합니다.
                유저가 해당 제품을 실제로 구매한 경우에만 리뷰를 작성할 수 있습니다.
            """
    )
    @PostMapping("/create/{productIdx}")
    public BaseResponse<ReviewResponseDto> create(
            @RequestBody ReviewRequestDto reviewRequestDto,
            @PathVariable Integer productIdx,
            @AuthenticationPrincipal User loginUser
    ) {
        if(loginUser == null) {
            throw new ReviewException(ReviewResponseStatus.REVIEW_USER_NOT_FOUND);
        }
        ReviewResponseDto response = reviewService.createReview(loginUser, productIdx, reviewRequestDto);
        return baseResponseService.getSuccessResponse(response, ReviewResponseStatus.SUCCESS);
    }

    @Operation(
            summary = "리뷰 삭제하기",
            description = """
                reviewIdx 를 전달받고, 로그인한 유저가 해당 리뷰 작성자인지 확인합니다.
                본인 작성 리뷰일 경우에만 삭제가 가능합니다.
            """
    )
    @DeleteMapping("/delete/{reviewIdx}")
    public BaseResponse<ReviewDeleteResponseDto> delete(
            @AuthenticationPrincipal User loginUser,
            @PathVariable Long reviewIdx
    ) {
        ReviewDeleteResponseDto response = reviewService.deleteReview(loginUser, reviewIdx);
        return baseResponseService.getSuccessResponse(response, ReviewResponseStatus.SUCCESS);
    }
}
