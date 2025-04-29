package com.example.backend.coupon.model.dto.response;

import com.example.backend.product.model.dto.ProductResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MyCouponInfoResponseDto {
    private Long couponIdx;
    private Boolean couponUsed;
    private CouponInfoDto couponInfo;
}
