package com.example.backend.coupon.controller;

import com.example.backend.coupon.model.Coupon;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "쿠폰 기능", description = "쿠폰 관련 기능을 제공합니다.")
@RestController
@RequestMapping("/coupon")
public class CouponController {

    @Operation(summary = "쿠폰 목록 조회", description = "전체 발급된 쿠폰 목록을 조회합니다.")
    @GetMapping
    public void getCoupons() {
        // TODO: 구현
    }

    @Operation(summary = "전체 쿠폰 발급", description = "전체에게 쿠폰 발급.")
    @PostMapping("/issueall")
    public void issueCouponsToAll() {
        // TODO: 구현
    }

    @Operation(summary = "선착순 쿠폰 발급", description = "선착순 쿠폰 발급.")
    @PostMapping("/issuefirst")
    public void issueCouponsToFirstCome(
            @RequestBody int quantity
    ) {
        // TODO: 구현
    }
}
