package com.example.backend.coupon.model;

import com.example.backend.coupon.model.dto.response.MyCouponInfoResponseDto;
import com.example.backend.order.model.OrderDetail;
import com.example.backend.user.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCouponIdx;
    private Boolean couponUsed;

    // 쿠폰과 다대일 맵핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_idx")
    private Coupon coupon;
    // 유저와 다대일 맵핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx")
    private User user;
    // 오더디테일과 일대일 맵핑
    @OneToOne(mappedBy = "userCoupon")
    private OrderDetail orderDetail;

    public MyCouponInfoResponseDto toDto() {
        return MyCouponInfoResponseDto.builder().couponIdx(userCouponIdx).couponUsed(couponUsed).couponInfo(getCoupon().toDto()).build();
    }
}
