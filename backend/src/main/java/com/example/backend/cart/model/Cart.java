package com.example.backend.cart.model;

import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "장바구니 엔티티")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "카트 고유 식별자", example = "1")
    private Long cartIdx;

    // 사용자와 1:1 매핑
    @OneToOne
    @JoinColumn(name="user_idx")
    @Schema(description = "카트를 소유한 사용자 정보", required = true)
    private User user;

    // 카트 아이템과 다대일 매핑
    @Schema(description = "카트에 포함된 장바구니 항목 목록")
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

}
