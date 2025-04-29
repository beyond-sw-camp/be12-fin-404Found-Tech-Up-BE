package com.example.backend.coupon.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description = "쿠폰 생성 요청")
public class UserCouponCreateRequestDto {
    @Schema(description = "사용자 고유 번호", example="21")
    private Long userIdx;
    @Schema(description = "사용 가능한 제품의 고유 번호", example="121")
    private Long productIdx;
    @Schema(description = "할인율, 정수임에 주의", example="10")
    private Integer discount;
    @Schema(description = "쿠폰 이름", example="10% 할인 쿠폰")
    private String couponName;
    @Schema(description = "쿠폰 만료일, YYYY-MM-DD", example="2025-04-15")
    private String expiryDate;
}
