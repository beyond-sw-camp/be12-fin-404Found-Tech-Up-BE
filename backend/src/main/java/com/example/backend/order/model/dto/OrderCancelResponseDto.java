package com.example.backend.order.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 취소 응답 DTO")
public class OrderCancelResponseDto {
    @Schema(description = "취소된 주문의 고유 번호", example = "1")
    private Long orderIdx;
    private String orderStatus;

    public static OrderCancelResponseDto from(Long orderIdx, String orderStatus) {
        return OrderCancelResponseDto.builder()
                .orderIdx(orderIdx)
                .orderStatus(orderStatus)
                .build();
    }
}
