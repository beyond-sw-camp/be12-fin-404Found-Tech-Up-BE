package com.example.backend.coupon.repository;

import com.example.backend.coupon.model.UserCoupon;
import com.example.backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    List<UserCoupon> findAllByUser(User user);

    long countByCoupon_CouponIdx(Long couponCouponIdx);

    long countByCoupon(Coupon eventCoupon);
}
