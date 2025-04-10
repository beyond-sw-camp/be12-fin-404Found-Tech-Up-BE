package com.example.backend.coupon.model.dto.response;

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
}
