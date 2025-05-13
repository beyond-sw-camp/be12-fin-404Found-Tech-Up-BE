package com.example.backend.coupon.service;

import com.example.backend.coupon.model.Coupon;
import com.example.backend.coupon.model.UserCoupon;
import com.example.backend.coupon.repository.CouponRepository;
import com.example.backend.coupon.repository.UserCouponRepository;
import com.example.backend.user.model.User;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.task.scheduling.enabled=false",
        "spring.scheduling.enabled=false"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CouponLuaScriptIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(CouponLuaScriptIntegrationTest.class);

    @Autowired private StringRedisTemplate redisTemplate;
    @Autowired private RedisConnectionFactory redisConnectionFactory;
    @Autowired private CouponService couponService;

    // DB 를 건드리지 않기 위해 가짜 리포지토리 등록
    @MockBean private CouponRepository couponRepository;
    @MockBean private UserCouponRepository userCouponRepository;

    private final Long couponId     = 1L;
    private final long initialStock = 10_000L;

    // Redis 키 (lua 스크립트에서 쓰는 것과 1:1 동일)
    private String userSetKey;
    private String stockHashKey;
    private String issuedListKey;

    @BeforeAll
    void loadScriptAndSetupData() {
        // 1) 키 셋팅
        userSetKey    = "set.receive.couponId.{" + couponId + "}";
        stockHashKey  = "hash.coupon.stock.{"   + couponId + "}";
        issuedListKey = "list.received.user.{"   + couponId + "}";

        // 2) Redis 클러스터 초기화
        RedisClusterConnection cluster = redisConnectionFactory.getClusterConnection();
        cluster.flushDb();
        log.info("🧹 Redis cluster flushed");

        // 3) 재고 세팅
        redisTemplate.opsForHash()
                .put(stockHashKey, "quantity", String.valueOf(initialStock));
        log.info("🎯 초기 재고를 {}개로 세팅했습니다.", initialStock);

        // 4) CouponService 내부에서 쓰는 Repository 모킹
        Coupon dummyCoupon = new Coupon();
        dummyCoupon.setCouponIdx(couponId);
        Mockito.when(couponRepository.findById(couponId))
                .thenReturn(Optional.of(dummyCoupon));

        Mockito.when(userCouponRepository.save(Mockito.any(UserCoupon.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    @AfterAll
    void cleanup() {
        redisConnectionFactory.getClusterConnection().flushDb();
        log.info("🧹 테스트 종료 - Redis cluster flushed");
    }

    @Test
    void 동시성_쿠폰_발급_통합_테스트_via_service() throws InterruptedException {
        final int THREADS        = 100000;
        final int MAX_CONCURRENCY = 300;

        ExecutorService exec = Executors.newFixedThreadPool(MAX_CONCURRENCY);
        CountDownLatch latch = new CountDownLatch(THREADS);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount    = new AtomicInteger();

        // Redis 상태 초기화
        redisTemplate.delete(userSetKey);
        redisTemplate.delete(issuedListKey);

        long start = System.currentTimeMillis();

        for (int i = 0; i < THREADS; i++) {
            final long userIdx = i;
            exec.submit(() -> {
                try {
                    // User 엔티티는 id만 세팅해 줍니다.
                    User user = User.builder()
                            .userIdx(userIdx)
                            .build();

                    boolean issued = couponService.issueEventCoupon(user, couponId);
                    if (issued) successCount.incrementAndGet();
                    else        failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS), "타임아웃 발생!");

        long duration = System.currentTimeMillis() - start;
        long remain   = Long.parseLong(
                (String) redisTemplate.opsForHash().get(stockHashKey, "quantity")
        );
        long issuedUsers = redisTemplate.opsForSet().size(userSetKey);

        log.info("=== 테스트 결과 ===");
        log.info("총 소요 시간     : {}ms", duration);
        log.info("남은 재고        : {}", remain);
        log.info("발급된 사용자 수 : {}", issuedUsers);
        log.info("성공 횟수       : {}", successCount.get());
        log.info("실패 횟수       : {}", failCount.get());

        // 검증
        assertEquals(0L, remain, "재고는 0이 되어야 합니다.");
        assertEquals(initialStock, issuedUsers, "발급된 사용자 수는 초기 재고와 같아야 합니다.");
    }
}
