package com.example.backend.coupon.service;

import com.example.backend.coupon.model.Coupon;
import com.example.backend.coupon.model.UserCoupon;
import com.example.backend.coupon.repository.CouponRedisRepository;
import com.example.backend.coupon.repository.CouponRepository;
import com.example.backend.coupon.repository.UserCouponRepository;
import com.example.backend.user.model.User;
import com.example.backend.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CouponServiceRedisTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRedisRepository couponRedisRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @BeforeEach
    void setUp() {
        couponRedisRepository.flushAll(); // 테스트 격리
    }

    @AfterEach
    void cleanup() {
        userRepository.deleteAll(); // 혹은 truncate
    }


    @Test
    void 동시성_테스트_쿠폰_정상_발급_수량_초과_없음() throws InterruptedException {
        // given
        int totalQuantity = 5;
        int 요청수 = 20;

        Coupon coupon = couponRepository.save(Coupon.of("동시성쿠폰", totalQuantity));

        ExecutorService executor = Executors.newFixedThreadPool(요청수);
        CountDownLatch latch = new CountDownLatch(요청수);

        for (int i = 0; i < 요청수; i++) {
            executor.submit(() -> {
                try {
                    // --- User 생성부 수정 ---
                    User user = User.of("user" + UUID.randomUUID() + "@test.com");
                    user.setIsAdmin(false);                            // isAdmin 기본값 설정
                    user = userRepository.save(user);
                    // --------------------------

                    Boolean result = couponService.issueEventCoupon(user, coupon.getCouponIdx());
                    System.out.println(Thread.currentThread().getName() + " ▶ 발급 성공: " + result);
                } catch (Exception e) {
                    System.err.println(Thread.currentThread().getName() + " ▶ 예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);

        // then
        String key = "set.receive.couponId." + coupon.getCouponIdx();
        Long redisCount = couponRedisRepository.sCard(key);
        List<UserCoupon> dbIssued = userCouponRepository.findAllByCoupon_CouponIdx(coupon.getCouponIdx());

        assertEquals(totalQuantity, redisCount);
        assertEquals(totalQuantity, dbIssued.size());

        executor.shutdown();
    }

    @Test
    void 쿠폰_정상_발급() throws JsonProcessingException, InterruptedException {
        // given
        User user = userRepository.save(User.of("test@test.com"));
        Coupon coupon = couponRepository.save(Coupon.of("이벤트", 10));

        // when
        Boolean result = couponService.issueEventCoupon(user, coupon.getCouponIdx());

        // then
        assertTrue(result);
        assertTrue(couponRedisRepository.sIsMember("set.receive.couponId." + coupon.getCouponIdx(), user.getUserIdx().toString()));
    }

    @Test
    void 쿠폰_중복_발급_예외() throws JsonProcessingException {
        // given
        User user = userRepository.save(User.of("test2@test.com"));
        Coupon coupon = couponRepository.save(Coupon.of("중복", 10));
        couponRedisRepository.sAdd("set.receive.couponId." + coupon.getCouponIdx(), user.getUserIdx().toString());

        // when & then
        assertThrows(RuntimeException.class, () ->
                couponService.issueEventCoupon(user, coupon.getCouponIdx())
        );
    }

    @Test
    void 쿠폰_수량초과_예외() throws JsonProcessingException, InterruptedException {
        // given
        Coupon coupon = couponRepository.save(Coupon.of("수량1", 1));

        User user1 = userRepository.save(User.of("a@test.com"));
        User user2 = userRepository.save(User.of("b@test.com"));

        couponService.issueEventCoupon(user1, coupon.getCouponIdx());

        // when & then
        assertThrows(RuntimeException.class, () ->
                couponService.issueEventCoupon(user2, coupon.getCouponIdx())
        );
    }
}

