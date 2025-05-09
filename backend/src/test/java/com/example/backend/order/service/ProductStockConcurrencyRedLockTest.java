package com.example.backend.order.service;

import com.example.backend.product.model.Product;
import com.example.backend.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class ProductStockConcurrencyRedLockTest {
    private static final Logger log = LoggerFactory.getLogger(ProductStockConcurrencySuccessTest.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RedissonClient redissonClient;

    private Long productId;
    private final int initialStock = 100;

    @BeforeEach
    void setup() {
        Product product = Product.builder()
                .name("concurrency-success-" + UUID.randomUUID())
                .price(1000.0)
                .discount(0)
                .brand("TestBrand")
                .stock(initialStock)
                .description("동시성 성공 테스트용 상품")
                .category("Test")
                .build();
        productRepository.save(product);
        productId = product.getProductIdx();
    }

    @Test
    void 분산락_적용시_재고가_정상_차감되는가() throws InterruptedException {
        int threadCount = 10;
        int purchaseQuantity = 5;
        int expectedFinalStock = initialStock - threadCount * purchaseQuantity;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                boolean locked = false;
                RLock lock = redissonClient.getLock("lock:product:" + productId);
                try {
                    // 락 대기 최대 2초, 획득 시 자동 만료 5초
                    locked = lock.tryLock(2, 5, TimeUnit.SECONDS);
                    if (!locked) {
                        throw new RuntimeException("락 획득 실패");
                    }

                    Product p = productRepository.findById(productId)
                            .orElseThrow(() -> new RuntimeException("상품이 없습니다"));
                    p.setStock(p.getStock() - purchaseQuantity);
                    productRepository.save(p);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("락 획득 중 인터럽트", e);
                } finally {
                    if (locked) {
                        lock.unlock();
                    }
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Product updated = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 없습니다"));
        int actualFinalStock = updated.getStock();

        log.info("기대 최종 재고={}, 실제 최종 재고={}", expectedFinalStock, actualFinalStock);

        // Redis 분산락이 적용되면 항상 기대값과 일치해야 한다
        assertEquals(expectedFinalStock, actualFinalStock,
                "Redis 분산락 적용 후에는 재고가 정확히 차감되어야 합니다");

        log.info("분산락 테스트 성공: 재고가 정확히 차감되었습니다.");
    }
}
