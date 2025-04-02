package com.example.backend.coupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "쿠폰 기능", description = "쿠폰 관련 기능을 제공합니다.")
@RestController
@RequestMapping("/coupon")
public class CouponController {

    @Operation(summary = "쿠폰 목록 조회", description = "회원이 소유한 쿠폰 목록을 조회합니다.")
    @GetMapping
    public void getUserCoupons() {
        // TODO: 구현
    }

    @Operation(summary = "쿠폰 사용", description = "상품 구매 시 쿠폰을 사용합니다.")
    @PostMapping("/use")
    public void useCoupon() {
        // TODO: 구현
    }

    @Operation(summary = "쿠폰 사용 취소", description = "주문 취소 시 쿠폰을 복원합니다.")
    @PostMapping("/cancel")
    public void cancelCouponUsage() {
        // TODO: 구현
    }
}
