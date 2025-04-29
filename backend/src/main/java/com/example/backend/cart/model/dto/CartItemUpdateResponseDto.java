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
@Schema(description = "장바구니 항목 삭제 응답 DTO")
public class CartItemUpdateResponseDto {
    @Schema(description = "삭제된 항목의 번호", example = "1")
    private Long cartItemIdx;

    // 장바구니 항목 삭제용
    public static CartItemUpdateResponseDto from(Long cartItemIdx) {
        return CartItemUpdateResponseDto.builder().cartItemIdx(cartItemIdx).build();
    }

    // 장바구니 비우기용
    public static CartItemUpdateResponseDto from() {
        return CartItemUpdateResponseDto.builder().build();
    }
}
