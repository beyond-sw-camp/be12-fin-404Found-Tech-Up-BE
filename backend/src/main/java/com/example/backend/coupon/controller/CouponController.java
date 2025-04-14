package com.example.backend.coupon.controller;

import com.example.backend.coupon.model.dto.request.CategoryCouponCreateRequestDto;
import com.example.backend.coupon.model.dto.response.CouponListResponseDto;
import com.example.backend.coupon.model.dto.request.EventCouponCreateRequest;
import com.example.backend.coupon.model.dto.request.UserCouponCreateRequestDto;
import com.example.backend.coupon.service.CouponService;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseServiceImpl;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "쿠폰 기능", description = "쿠폰 관련 기능을 제공합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon")
public class CouponController {
    private final CouponService couponService;

    @Operation(summary = "쿠폰 목록 조회", description = "전체 발급된 쿠폰 목록을 페이지 번호에 따라 조회합니다.")
    @GetMapping
    public ResponseEntity<BaseResponse<CouponListResponseDto>> getCouponList() {
        CouponListResponseDto result = couponService.getCouponPage(0);
        return ResponseEntity.ok(new BaseResponseServiceImpl().getSuccessResponse(result, CommonResponseStatus.SUCCESS));
    }

    @Operation(summary = "쿠폰 목록 조회", description = "전체 발급된 쿠폰 목록을 페이지 번호에 따라 조회합니다.")
    @GetMapping("/{offset}")
    public ResponseEntity<BaseResponse<CouponListResponseDto>> getCouponList(@PathVariable int offset) {
        CouponListResponseDto result = couponService.getCouponPage(offset);
        return ResponseEntity.ok(new BaseResponseServiceImpl().getSuccessResponse(result, CommonResponseStatus.SUCCESS));
    }

    @Operation(summary="사용자별 쿠폰 발급", description="개별 사용자마다 수동 쿠폰 발급")
    @PostMapping("/issue")
    public ResponseEntity<BaseResponse<String>> issueCoupon(@RequestBody UserCouponCreateRequestDto request) {
        Long couponIdx = couponService.CreateCouponForUser(request);
        log.info("issue coupon {}", couponIdx);
        return ResponseEntity.ok(new BaseResponseServiceImpl().getSuccessResponse(couponIdx.toString() + "번 쿠폰 발행 성공", CommonResponseStatus.SUCCESS));
    }

   /*
    @Operation(summary = "카테고리별 쿠폰 발급", description = "제품별 쿠폰 발급")
    @PostMapping("/issuecategory")
    public void issueByCategory(@RequestBody CategoryCouponCreateRequestDto category) {
        // TODO: 프론트 수정 후 여기를 구현
        log.info("issue category coupon {} with discount {}%", category.getCategory(), category.getDiscount());
    }
    */
    @Operation(summary = "전체 쿠폰 발급", description = "전체에게 쿠폰 발급.")
    @PostMapping("/issueall")
    public void issueCouponsToAll(@RequestBody CategoryCouponCreateRequestDto category) {
        // TODO: 프론트 수정 후 여기를 구현
    }

    @Operation(summary = "선착순 쿠폰 발급", description = "선착순 쿠폰 발급.")
    @PostMapping("/issuefirst")
    public void issueCouponsToFirstCome(
            @RequestBody EventCouponCreateRequest request
    ) {
        // TODO: 구현
    }

    // TODO: 등록한 쿠폰 수정 혹은 발행 취소

}
