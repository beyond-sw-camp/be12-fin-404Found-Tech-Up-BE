package com.example.backend.coupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자 쿠폰 기능", description = "사용자에게 발급된 쿠폰 관련 기능을 제공합니다.")
@RestController
@RequestMapping("/usercoupon")
public class UserCouponController {

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
}
