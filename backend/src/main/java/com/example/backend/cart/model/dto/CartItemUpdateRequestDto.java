package com.example.backend.cart.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "장바구니 항목 수량 변경 요청 DTO")
public class CartItemUpdateRequestDto {
    @Schema(description = "변경할 수량. 양수이면 추가, 음수이면 차감", example = "1")
    private int deltaQuantity;
}
