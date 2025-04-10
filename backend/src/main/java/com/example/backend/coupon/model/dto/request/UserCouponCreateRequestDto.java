package com.example.backend.coupon.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserCouponCreateRequestDto {
    private Long userIdx;
    private Long productIdx;
    private Integer discount;
    private String couponName;
    private String expiryDate;
}
