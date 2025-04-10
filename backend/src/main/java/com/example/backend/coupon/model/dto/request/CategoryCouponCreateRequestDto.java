package com.example.backend.coupon.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CategoryCouponCreateRequestDto {
    private String category;
    private Integer discount;
}
