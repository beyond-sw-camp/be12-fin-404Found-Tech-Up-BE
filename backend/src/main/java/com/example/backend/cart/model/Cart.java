package com.example.backend.cart.model;

import com.example.backend.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartIdx;

    // user와 1대1 맵핑
    @OneToOne
    @JoinColumn(name="user_idx")
    private User user;

    // 카트 아이템과 다대일 맵핑
    @OneToMany(mappedBy = "cart")
    private List<CartItem> cartItems;
}
