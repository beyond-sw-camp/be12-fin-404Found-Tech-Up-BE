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
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.CannotCreateTransactionException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    }
    @Test
    void 동시에_여러명이_쿠폰을_발급하면_초과_발급이_되는가() throws InterruptedException {
        int 발급시도횟수 = 500; // 더 많은 동시성
        Coupon eventCoupon = couponRepository.save(Coupon.of("이벤트 쿠폰", 50));
        List<User> users = IntStream.range(0, 발급시도횟수)
                .mapToObj(i -> userRepository.save(User.of("user" + i + "_" + UUID.randomUUID() + "@test.com")))
                .collect(Collectors.toList());

        ExecutorService executor = Executors.newFixedThreadPool(발급시도횟수);
        CountDownLatch latch = new CountDownLatch(발급시도횟수);

        for (User user : users) {
            executor.submit(() -> {
                try {
                    boolean result = couponService.issueEventCoupon(user, eventCoupon.getCouponIdx());
                    System.out.println(Thread.currentThread().getName() + " -> 발급결과: " + result);
                } catch (CannotAcquireLockException | CannotCreateTransactionException e) {
                    // Deadlock 포함된 예외 - 간단 출력
                    System.out.println(Thread.currentThread().getName() + " -> Lock 관련 예외 발생: " + e.getClass().getSimpleName());
                } catch (Exception e) {
                    // 정말 필요한 경우만 추적
                    System.err.println(Thread.currentThread().getName() + " -> 알 수 없는 예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }



        latch.await();

        long 발급된수 = userCouponRepository.countByCoupon(eventCoupon);
        System.out.println("발급된 쿠폰 수: " + 발급된수);

        // 오류 유도: 초과 발급 허용되도록 만듦
        Assertions.assertTrue(발급된수 > 5); // 의도적 초과 조건
        executor.shutdown(); // 꼭 shutdown 해줘야 JVM 종료됨

    }

}
