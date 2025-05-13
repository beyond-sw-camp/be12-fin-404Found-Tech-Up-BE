package com.example.backend.coupon.service;

import com.example.backend.coupon.repository.CouponRedisRepository;
import com.example.backend.user.model.User;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;     // ← 추가
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;            // ← 추가
import static org.mockito.Mockito.doNothing;                   // ← 추가

@SpringBootTest(properties = {
        "spring.task.scheduling.enabled=false"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CouponServiceLuaDebugTest {
    private static final Logger log = LoggerFactory.getLogger(CouponServiceLuaDebugTest.class);

    @Autowired private CouponService couponService;
    @Autowired private CouponRedisRepository couponRedisRepository;
    @Autowired private RedisTemplate<String, String> redisTemplate;

    // DB 호출을 모조리 스텁 처리
    @MockBean private CouponDBService couponDBService;

    private final Long couponId = 1L;
    private final long initialStock = 10L;

    @BeforeEach
    void setUp() {
        // Mockito 스텁: saveIssuedCouponToDB()는 아무 동작도 안 함
        doNothing().when(couponDBService).saveIssuedCouponToDB(anyLong(), anyLong());

        couponRedisRepository.flushAll();
        redisTemplate.opsForHash().put(stockKey(), "quantity", String.valueOf(initialStock));
        log.info("📡 Redis setup: stockKey={} exists={}, initialQty={}",
                stockKey(), redisTemplate.hasKey(stockKey()), getQty());
    }

    @AfterEach
    void tearDown() {
        couponRedisRepository.flushAll();
        log.info("🧹 Cleaned up Redis after test");
    }

    @Test
    void singleUserIssue() {
        User user = buildUser(1L);

        log.info("🧪 singleUserIssue start");
        boolean ok = couponService.issueEventCoupon(user, couponId);

        assertTrue(ok);
        assertEquals(initialStock - 1, getQty());
        assertTrue(redisTemplate.opsForSet().isMember(userSetKey(), user.getUserIdx().toString()));
    }

    @Test
    void duplicateUserThrows() {
        User user = buildUser(2L);
        couponService.issueEventCoupon(user, couponId);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> couponService.issueEventCoupon(user, couponId));
        assertTrue(ex.getMessage().contains("이미 발급된 사용자"));
    }

    @Test
    void smallConcurrencyTest() throws InterruptedException {
        final int THREADS = 15;
        ExecutorService exec = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(THREADS);
        AtomicInteger success = new AtomicInteger(), fail = new AtomicInteger();

        log.info("🧪 Running smallConcurrencyTest with {} threads", THREADS);
        for (int i = 0; i < THREADS; i++) {
            final long uid = 100 + i;
            exec.submit(() -> {
                try {
                    if (couponService.issueEventCoupon(buildUser(uid), couponId)) {
                        success.incrementAndGet();
                    }
                } catch (RuntimeException e) {
                    fail.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(10, TimeUnit.SECONDS);
        exec.shutdown();

        long remaining = getQty();
        long issuedCount = redisTemplate.opsForSet().size(userSetKey());

        // 10번만 발급되고 나머지는 재고 소진
        assertEquals(initialStock, remaining + issuedCount);
        assertEquals(success.get(), issuedCount);
    }

    // ——— 헬퍼 메서드 ———

    private String couponKey() {
        return "{coupon:" + couponId + "}";
    }
    private String stockKey() {
        return couponKey() + ":stock";
    }
    private String userSetKey() {
        return couponKey() + ":users";
    }
    private long getQty() {
        Object v = redisTemplate.opsForHash().get(stockKey(), "quantity");
        return v == null ? -1 : Long.parseLong(v.toString());
    }
    private User buildUser(long userId) {
        return User.builder()
                .userIdx(userId)
                .isAdmin(false)
                .build();
    }
}
