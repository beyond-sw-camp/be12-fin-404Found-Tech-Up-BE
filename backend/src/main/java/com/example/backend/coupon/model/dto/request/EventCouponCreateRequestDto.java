package com.example.backend.coupon.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description = "선착순 쿠폰 이벤트를 만듭니다")
public class EventCouponCreateRequestDto {
    @Schema(description = "사용 가능한 제품의 고유 번호", example="12")
    private Long productIdx;
    @Schema(description = "할인율, 양의 정수임", example="10")
    private Integer discount;
    @Schema(description = "쿠폰 이름", example="10% 할인 이벤트")
    private String couponName;
    @Schema(description = "쿠폰의 만료일, XXXX-XX-XX 형태", example="2028-12-31")
    private String expiryDate;
    @Schema(description = "이벤트로 발급할 쿠폰 수량", example="100")
    private Integer quantity;
}
