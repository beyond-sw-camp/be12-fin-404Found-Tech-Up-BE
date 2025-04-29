package com.example.backend.coupon.controller;

import com.example.backend.coupon.model.dto.response.MyCouponInfoResponseDto;
import com.example.backend.coupon.service.CouponService;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseServiceImpl;
import com.example.backend.global.response.responseStatus.CouponResponseStatus;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "사용자 쿠폰 기능", description = "사용자에게 발급된 쿠폰 관련 기능을 제공합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/usercoupon")
public class UserCouponController {
    private final CouponService couponService;

    @Operation(summary = "사용자 쿠폰 목록 조회", description = "회원이 소유한 쿠폰 목록을 조회합니다.")
    @GetMapping("/{userIdx}")
    public void getUserCoupons(@PathVariable Long userIdx) {
        // TODO: 구현
    }

    @Operation(summary = "쿠폰 사용", description = "상품 구매 시 쿠폰을 사용합니다.")
    @PostMapping("/use/{couponIdx}")
    public void useCoupon(@PathVariable Long couponIdx) {
        // TODO: 구현
    }

    @Operation(summary = "쿠폰 사용 취소", description = "주문 취소 시 쿠폰을 복원합니다.")
    @PostMapping("/cancel/{couponIdx}")
    public void cancelCouponUsage(@PathVariable Long couponIdx) {
        // TODO: 구현
    }

    @Operation(summary = "내 쿠폰 조회", description = "내게 발급된 쿠폰 목록을 봅니다.")
    @GetMapping("/mycoupon")
    public BaseResponse<List<MyCouponInfoResponseDto>> getMyCoupon(@AuthenticationPrincipal User user) {
        List<MyCouponInfoResponseDto> myCoupons = couponService.getMyCouponList(user);
        return new BaseResponseServiceImpl().getSuccessResponse(myCoupons, CouponResponseStatus.SUCCESS);
    }

}
