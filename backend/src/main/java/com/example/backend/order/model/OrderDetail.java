package com.example.backend.order.model;

import com.example.backend.coupon.model.UserCoupon;
import com.example.backend.product.model.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderDetailIdx;
    private int orderDetailQuantity;
    private int orderDetailPrice;
    private int orderDetailDiscount;

    // 유저쿠폰과 1대1 맵핑
    @OneToOne
    @JoinColumn(name = "user_coupon_idx")
    private UserCoupon userCoupon;
    // 주문과 다대일 맵핑
    @ManyToOne
    @JoinColumn(name = "order_idx")
    private Orders orders;
    // 제품과 다대일 맵핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_idx")
    private Product product;
}
