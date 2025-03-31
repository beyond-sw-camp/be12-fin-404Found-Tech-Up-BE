package com.example.backend.cart.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    // 제품과 다대일 맵핑
}
