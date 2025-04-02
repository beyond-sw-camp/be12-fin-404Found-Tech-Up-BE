package com.example.backend.coupon.model;

import com.example.backend.product.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponIdx;
    private String couponName;
    private int couponDiscountRate;
    private Date couponValidDate;

    // 제품과 다대일 맵핑
    @ManyToOne
    @JoinColumn(name = "product_idx")
    private Product product;
    // 유저 쿠폰과 일대다 맵핑
    @OneToMany(mappedBy = "coupon")
    private List<UserCoupon> userCoupons;
}
