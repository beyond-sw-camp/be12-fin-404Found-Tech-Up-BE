package com.example.backend.coupon.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description = "선착순 쿠폰 이벤트 발행 요청입니다.")
public class EventCouponIssueRequestDto {
    private Long couponIdx;
    private Long userIdx;
}
