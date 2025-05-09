package com.example.backend.coupon.service;

import com.example.backend.coupon.repository.CouponRedisRepository;
import com.example.backend.user.model.User;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest(
        properties = {
                "spring.redis.host=${spring.redis.host}",
                "spring.redis.port=${spring.redis.port}",
                "spring.task.scheduling.enabled=false"
        }
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CouponServiceRedisIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(CouponServiceRedisIntegrationTest.class);

    // 1) Redis 컨테이너 정의 & 시작
    static GenericContainer<?> redis = new GenericContainer<>(
            DockerImageName.parse("redis:6.2.6"))
            .withExposedPorts(6379);

    static {
        redis.start();
    }

    // 2) 스프링 프로퍼티에 호스트/포트 주입
    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRedisRepository couponRedisRepository;

    private final Long couponId     = 1L;
    private final long initialStock = 3000L;

    @BeforeAll
    void setupData() {
        // Redis 초기화 및 재고 세팅
        couponRedisRepository.flushAll();
        couponRedisRepository.hIncrBy(
                "hash.coupon.stock." + couponId,
                "quantity",
                initialStock
        );
        log.info("테스트 시작 전 재고를 {} 로 세팅했습니다.", initialStock);
    }

    @AfterAll
    void cleanup() {
        couponRedisRepository.flushAll();
        log.info("테스트 종료, Redis 초기화 완료.");
        redis.stop();
    }

    @Test
    void 동시성_쿠폰_발급_통합_테스트() throws InterruptedException {
        int THREADS = 30000;
        CountDownLatch latch = new CountDownLatch(THREADS);
        ExecutorService exec = Executors.newFixedThreadPool(THREADS);

        for (int i = 0; i < THREADS; i++) {
            final long userId = i;
            exec.submit(() -> {
                try {
                    couponService.issueEventCoupon(
                            User.builder().userIdx(userId).build(),
                            couponId
                    );
                } catch (RuntimeException ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        // 최대 10초까지 대기
        latch.await(10, TimeUnit.SECONDS);

        Long remain = couponService.getRemainingQuantity(couponId);
        long uniqueUserCount = couponService.countIssuedUsers(couponId);

        // 로그로 결과 정리
        log.info("===== 통합 테스트 결과 =====");
        log.info("초기 재고       : {}", initialStock);
        log.info("남은 재고       : {}", remain);
        log.info("발급된 사용자 수: {}", uniqueUserCount);
        log.info("==========================");

        Assertions.assertEquals(0L, remain, "남은 재고가 0 이어야 합니다.");
        Assertions.assertEquals(initialStock, uniqueUserCount, "발급된 사용자 수는 초기 재고와 같아야 합니다.");
    }
}
