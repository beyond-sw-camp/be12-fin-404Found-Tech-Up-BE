package com.example.backend.wishlist.model;

import com.example.backend.product.model.Product;
import com.example.backend.user.model.User;
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

    // 유저와 다대일 맵핑
    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    // 제품과 다대일 맵핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_idx")
    private Product product;

}
