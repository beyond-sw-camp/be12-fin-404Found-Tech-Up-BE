package com.example.backend.review.controller;

import com.example.backend.review.model.Review;
import com.example.backend.review.model.dto.ReviewRequestDto;
import com.example.backend.review.model.dto.ReviewResponseDto;
import com.example.backend.review.service.ReviewService;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "리뷰 기능", description = "리뷰 기능 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(
            summary = "리뷰 작성하기",
            description = """
                productIdx를 전달받아, 로그인한 유저 정보를 확인합니다.  
                유저가 해당 제품을 실제로 구매한 경우에만 리뷰를 작성할 수 있습니다.
            """
    )
    @PostMapping("/create/{productIdx}")
    public ResponseEntity<ReviewResponseDto> create(@RequestBody ReviewRequestDto reviewRequestDto, @PathVariable Integer productIdx) {
        return null;
    }

    @Operation(
            summary = "리뷰 수정하기",
            description = """
                reviewIdx를 전달받고, 로그인한 유저가 해당 리뷰 작성자인지 확인합니다.  
                작성자 본인일 경우에만 리뷰 수정이 가능합니다.
            """
    )
    @PatchMapping("/update/{reviewIdx}")
    public void update(@AuthenticationPrincipal User loginUser, @PathVariable Long reviewIdx) {

    }

    @Operation(
            summary = "리뷰 삭제하기",
            description = """
                reviewIdx를 전달받고, 로그인한 유저가 해당 리뷰 작성자인지 확인합니다.  
                본인 작성 리뷰일 경우에만 삭제가 가능합니다.
            """
    )
    @DeleteMapping("/delete/{reviewIdx}")
    public void delete(@AuthenticationPrincipal User loginUser, @PathVariable Long reviewIdx) {

    }
}
