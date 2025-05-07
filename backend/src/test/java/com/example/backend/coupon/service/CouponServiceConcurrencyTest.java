package com.example.backend.coupon.service;

import com.example.backend.coupon.model.Coupon;
import com.example.backend.coupon.repository.CouponRepository;
import com.example.backend.coupon.repository.UserCouponRepository;
import com.example.backend.user.model.User;
import com.example.backend.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@SpringBootTest
@ActiveProfiles("test")
public class CouponServiceConcurrencyTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    private Long couponId;

    @BeforeEach
    void setup() {
        // 쿠폰 수동 ID 제거 -> Hibernate가 ID 및 Version 관리
        Coupon coupon = Coupon.builder()
                .couponQuantity(1)
                .build();
        couponRepository.save(coupon);
        couponId = coupon.getCouponIdx(); // 자동 생성된 ID 저장

        // 테스트 유저 10명 생성
        for (int i = 1; i <= 10; i++) {
            User user = User.builder()
                    .userEmail("user" + i + "_" + UUID.randomUUID() + "@test.com")
                    .build();
            userRepository.save(user);
        }
    }

    @Test
    void 동시에_여러명이_쿠폰을_발급하면_초과_발급이_되는가() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        List<User> users = userRepository.findAll();

        for (int i = 0; i < threadCount; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    couponService.issueEventCoupon(users.get(idx), couponId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        long issuedCount = userCouponRepository.countByCoupon_CouponIdx(couponId);
        System.out.println("발급된 쿠폰 수: " + issuedCount);

        Assertions.assertEquals(1L, issuedCount); // 1개를 초과하면 테스트 실패
    }
}
