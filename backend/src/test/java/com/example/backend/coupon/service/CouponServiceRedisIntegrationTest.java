package com.example.backend.coupon.service;

import com.example.backend.coupon.repository.CouponRedisRepository;
import com.example.backend.user.model.User;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootTest(properties = {
        "spring.redis.cluster.nodes=localhost:17000,localhost:17001,localhost:17002,localhost:17003,localhost:17004,localhost:17005",
        "spring.task.scheduling.enabled=false"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CouponServiceRedisIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(CouponServiceRedisIntegrationTest.class);

    @Autowired private CouponService couponService;
    @Autowired private CouponRedisRepository couponRedisRepository;

    private final Long couponId     = 1L;
    private final long initialStock = 500L;

    @BeforeAll
    void setupData() {
        couponRedisRepository.flushAll();
        couponRedisRepository.hIncrBy("hash.coupon.stock." + couponId, "quantity", initialStock);
        log.info("▶ 테스트 시작 전: 재고를 {} 로 세팅했습니다.", initialStock);
    }

    @AfterAll
    void cleanup() {
        couponRedisRepository.flushAll();
        log.info("▶ 테스트 종료: Redis 클러스터 전체 초기화 완료.");
    }

    @Test
    void 동시성_쿠폰_발급_통합_테스트() throws InterruptedException {
        int THREADS = 1000;
        CountDownLatch latch = new CountDownLatch(THREADS);
        ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

        // 카운터 준비
        AtomicLong successCount       = new AtomicLong();
        AtomicLong duplicateCount     = new AtomicLong();
        AtomicLong soldOutCount       = new AtomicLong();
        AtomicLong lockFailCount      = new AtomicLong();
        AtomicLong otherErrorCount    = new AtomicLong();

        long startTime = System.nanoTime();
        log.info("▶ 테스트 시작: {} 스레드로 발급 시도", THREADS);

        for (int i = 0; i < THREADS; i++) {
            final long userId = i;
            exec.submit(() -> {
                try {
                    boolean issued = couponService.issueEventCoupon(
                            User.builder().userIdx(userId).build(),
                            couponId
                    );
                    if (issued) {
                        successCount.incrementAndGet();
                    }
                } catch (RuntimeException e) {
                    String msg = e.getMessage();
                    if (msg.contains("락 획득 실패")) {
                        lockFailCount.incrementAndGet();
                //        log.debug("[락 실패] userId={} {}", userId, msg);
                    } else if (msg.contains("이미 발급")) {
                        duplicateCount.incrementAndGet();
                        //log.debug("[중복 요청] userId={} {}", userId, msg);
                    } else if (msg.contains("소진")) {
                        soldOutCount.incrementAndGet();
                        //log.debug("[재고 부족] userId={} {}", userId, msg);
                    } else {
                        otherErrorCount.incrementAndGet();
                    //    log.warn("[기타 오류] userId={} {}, {}", userId, msg, e.toString());
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // 완료 대기
        latch.await();
        exec.shutdown();
        exec.awaitTermination(1, TimeUnit.MINUTES);

        long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        Long remain          = couponService.getRemainingQuantity(couponId);
        long uniqueUserCount = couponService.countIssuedUsers(couponId);

        // 최종 로그
        log.info("===== 통합 테스트 결과 =====");
        log.info("초기 재고         : {}", initialStock);
        log.info("남은 재고         : {}", remain);
        log.info("발급된 사용자 수  : {}", uniqueUserCount);
        log.info("정상 발급 성공    : {}", successCount.get());
        log.info("중복 요청          : {}", duplicateCount.get());
        log.info("재고 부족 실패    : {}", soldOutCount.get());
        log.info("락 획득 실패      : {}", lockFailCount.get());
        log.info("기타 오류         : {}", otherErrorCount.get());
        log.info("총 소요 시간(ms)  : {}", durationMs);
        log.info("=============================");

        Assertions.assertEquals(0L, remain, "남은 재고가 0 이어야 합니다.");
        Assertions.assertEquals(initialStock, uniqueUserCount, "발급된 사용자 수는 초기 재고와 같아야 합니다.");
    }
}
