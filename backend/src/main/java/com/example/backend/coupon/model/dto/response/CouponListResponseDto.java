package com.example.backend.coupon.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CouponListResponseDto {
    List<CouponInfoDto> couponList;
    Integer offset;
    Long limit;
    Integer total;
}
