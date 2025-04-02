package com.example.backend.coupon.model;

import com.example.backend.order.model.OrderDetail;
import com.example.backend.user.model.User;
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
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCouponIdx;
    private Boolean couponUsed;

    // 쿠폰과 다대일 맵핑
    @ManyToOne
    @JoinColumn(name = "coupon_idx")
    private Coupon coupon;
    // 유저와 다대일 맵핑
    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;
    // 오더디테일과 일대일 맵핑
    @OneToOne(mappedBy = "userCoupon")
    private OrderDetail orderDetail;
}
