package com.example.backend.coupon.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Schema(description="쿠폰 목록과 페이지네이션 정보를 담은 데이터 전송 객체")
public class CouponListResponseDto {
    @Schema(description="쿠폰 목록")
    List<CouponInfoDto> couponList;
    @Schema(description="페이지 번호, 0부터 시작", example="3")
    Integer offset;
    @Schema(description="한 페이지에 보여줄 쿠폰 개수", example="30")
    Long limit;
    @Schema(description="총 항목 개수",example="60")
    Integer total;
}
