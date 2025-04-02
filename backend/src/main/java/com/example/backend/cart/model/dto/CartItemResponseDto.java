package com.example.backend.cart.model.dto;

import com.example.backend.cart.model.CartItem;
import com.example.backend.common.dto.ProductSummaryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장바구니 항목 응답 DTO")
public class CartItemResponseDto {

    @Schema(description = "장바구니 항목 고유 번호", example = "1")
    private Long cartItemIdx;

    @Schema(description = "장바구니에 추가된 수량", example = "2")
    private int cartItemQuantity;

    @Schema(description = "상품 요약 정보")
    private ProductSummaryDto product;

    public static CartItemResponseDto from(CartItem cartItem) {
        return CartItemResponseDto.builder()
                .cartItemIdx(cartItem.getCartItemIdx())
                .cartItemQuantity(cartItem.getCartItemQuantity())
                .product(ProductSummaryDto.from(cartItem.getProduct()))
                .build();
    }

}
