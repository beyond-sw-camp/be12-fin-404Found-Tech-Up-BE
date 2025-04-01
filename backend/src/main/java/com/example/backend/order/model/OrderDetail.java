package com.example.backend.order.model;

import com.example.backend.coupon.model.UserCoupon;
import com.example.backend.product.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderDetailIdx;
    private int orderDetailQuantity;
    private int orderDetailPrice;

    // 유저쿠폰과 1대1 맵핑
    @OneToOne(mappedBy = "orderDetail")
    private UserCoupon userCoupon;
    // 주문과 다대일 맵핑
    @ManyToOne
    @JoinColumn(name = "order_idx")
    private Order order;
    // 제품과 다대일 맵핑
    @ManyToOne
    @JoinColumn(name = "product_idx")
    private Product product;
}
