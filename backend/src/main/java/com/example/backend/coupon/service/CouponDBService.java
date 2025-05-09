package com.example.backend.coupon.service;

import com.example.backend.coupon.model.Coupon;
import com.example.backend.coupon.model.UserCoupon;
import com.example.backend.coupon.repository.CouponRepository;
import com.example.backend.coupon.repository.UserCouponRepository;
import com.example.backend.global.exception.CouponException;
import com.example.backend.global.exception.UserException;
import com.example.backend.global.response.responseStatus.CouponResponseStatus;
import com.example.backend.global.response.responseStatus.UserResponseStatus;
import com.example.backend.user.model.User;
import com.example.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponDBService {

    private final CouponRepository couponRepository;

    private final UserCouponRepository userCouponRepository;

    private final UserRepository userRepository;

    @Transactional
    public void saveIssuedCouponToDB(Long userId, Long couponId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserResponseStatus.INVALID_USER_ID));

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponException(CouponResponseStatus.COUPON_NOT_FOUND));

        if (coupon.getCouponQuantity() == 0) {
            throw new RuntimeException("쿠폰이 모두 소진되었습니다."); // 방어적 처리
        }

        // 수량 차감 및 저장
        coupon.setCouponQuantity(coupon.getCouponQuantity() - 1);
        couponRepository.save(coupon);

        // 사용자 쿠폰 저장
        UserCoupon userCoupon = UserCoupon.builder()
                .user(user)
                .coupon(coupon)
                .couponUsed(false)
                .build();

        userCouponRepository.save(userCoupon);
    }

}
