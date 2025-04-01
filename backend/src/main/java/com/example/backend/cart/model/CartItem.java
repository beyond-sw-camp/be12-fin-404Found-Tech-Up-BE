package com.example.backend.cart.model;

import com.example.backend.product.model.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {
    @Id
    private Long cartItemIdx;
    private int cartItemQuantity;

    // 카트와 다대일 맵핑
    @ManyToOne
    @JoinColumn(name = "cart_idx")
    private Cart cart;

    // 제품과 다대일 맵핑
    @ManyToOne
    @JoinColumn(name = "product_idx")
    private Product product;
}
