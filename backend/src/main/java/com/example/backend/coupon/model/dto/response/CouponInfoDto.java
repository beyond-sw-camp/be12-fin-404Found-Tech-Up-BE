package com.example.backend.coupon.model.dto.response;

import com.example.backend.coupon.model.Coupon;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Schema(description = "쿠폰 정보")
public class CouponInfoDto {
    @Schema(description = "쿠폰 고유 번호", example="1")
    private Long couponIdx;
    @Schema(description = "쿠폰 이름", example="10% 할인 쿠폰")
    private String couponName;
    @Schema(description = "쿠폰 할인율, 정수값만 가능함에 주의", example="10")
    private int couponDiscountRate;
    @Schema(description = "쿠폰 만료 기한")
    private ZonedDateTime couponValidDate;
    @Schema(description = "이벤트 쿠폰인 경우 발급 수량 개수, 일반 쿠폰은 -1으로 고정")
    private Integer couponStock;
    @Schema(description = "이 쿠폰을 쓸 수 있는 제품의 DB 테이블 Idx", example="210")
    private Long productIdx;
    @Schema(description = "이 쿠폰을 쓸 수 있는 제품의 이름", example="AMD RYZEN 5 5600X")
    private String productName;

    public static CouponInfoDto from(Coupon coupon) {
        return CouponInfoDto.builder()
                .couponIdx(coupon.getCouponIdx())
                .couponName(coupon.getCouponName())
                .couponDiscountRate(coupon.getCouponDiscountRate())
                .couponStock(coupon.getCouponQuantity())
                .couponValidDate(coupon.getCouponValidDate().toInstant().atZone(ZoneId.systemDefault()))
                .productIdx(coupon.getProduct().getProductIdx())
                .productName(coupon.getProduct().getName())
                .build();
    }
}
