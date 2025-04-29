package com.example.backend.coupon.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description = "카테고리별 쿠폰 발급 요청, 사용 여부는 확실하지 않음")
public class CategoryCouponCreateRequestDto { // TODO
    private String category;
    private Integer discount;
    private Integer expiryDate;
    @Schema(description = "쿠폰 이름", example="10% 할인 쿠폰")
    private String couponName;
}
