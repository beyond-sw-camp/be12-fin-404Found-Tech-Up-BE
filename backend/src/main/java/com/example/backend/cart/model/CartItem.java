package com.example.backend.cart.model;

import com.example.backend.product.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "장바구니 항목 엔티티")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "카트 아이템 고유 식별자", example = "1")
    private Long cartItemIdx;

    @Schema(description = "장바구니에 추가된 상품 수량", example = "2")
    private int cartItemQuantity;

    // 카트와 다대일 맵핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_idx")
    @Schema(description = "해당 장바구니 항목이 속한 카트")
    private Cart cart;

    // 제품과 다대일 맵핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_idx")
    @Schema(description = "장바구니 항목에 포함된 제품 정보")
    private Product product;
}
