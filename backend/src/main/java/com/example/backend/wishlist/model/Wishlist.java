package com.example.backend.wishlist.model;

import com.example.backend.product.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishlistIdx;

    // 제품과 다대일 맵핑
    @ManyToOne
    @JoinColumn(name = "product_idx")
    private Product product;

    // 유저와 다대일 맵핑
}
