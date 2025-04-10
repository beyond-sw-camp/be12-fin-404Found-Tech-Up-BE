package com.example.backend.coupon.model.dto.response;

import com.example.backend.coupon.model.Coupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CouponInfoDto {
    private Long couponIdx;
    private String couponName;
    private int couponDiscountRate;
    private Date couponValidDate;
    private Long productIdx;

    public static CouponInfoDto from(Coupon coupon) {
        return CouponInfoDto.builder()
                .couponIdx(coupon.getCouponIdx())
                .couponName(coupon.getCouponName())
                .couponDiscountRate(coupon.getCouponDiscountRate())
                .couponValidDate(coupon.getCouponValidDate())
                .productIdx(coupon.getProduct().getProductIdx())
                .build();
    }
}
