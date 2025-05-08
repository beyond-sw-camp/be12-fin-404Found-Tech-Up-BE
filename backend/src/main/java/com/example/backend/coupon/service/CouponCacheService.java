package com.example.backend.coupon.service;

import com.example.backend.coupon.model.Coupon;
import com.example.backend.coupon.model.CouponRedisEntity;
import com.example.backend.coupon.repository.CouponRepository;
import com.example.backend.global.exception.CouponException;
import com.example.backend.global.response.responseStatus.CouponResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponCacheService {
    private final CouponRepository couponRepository;

    @Cacheable(cacheNames = "coupon")
    public CouponRedisEntity findCoupon(long couponIdx) {
        Coupon coupon = couponRepository.findById(couponIdx).orElseThrow(() -> new CouponException(CouponResponseStatus.COUPON_NOT_FOUND));
        return CouponRedisEntity.builder()
                .couponIdx(coupon.getCouponIdx())
                .couponQuantity(coupon.getCouponQuantity())
                .build();
    }
}
