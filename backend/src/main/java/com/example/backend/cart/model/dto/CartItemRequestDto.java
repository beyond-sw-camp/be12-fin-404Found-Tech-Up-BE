package com.example.backend.cart.model.dto;

import com.example.backend.cart.model.CartItem;
import com.example.backend.product.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemRequestDto {
    @Schema(description = "장바구니에 추가된 수량", example = "2")
    private int cartItemQuantity;

    @Schema(description = "상품 정보")
    private Product product;

    public CartItem toEntity() {
        return CartItem.builder()
                .cartItemQuantity(cartItemQuantity)
                .product(product)
                .build();
    }
}
