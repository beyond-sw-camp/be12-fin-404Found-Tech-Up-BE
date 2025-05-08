package com.example.backend.order.service;

import com.example.backend.product.model.Product;
import com.example.backend.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@ActiveProfiles("test")
public class ProductStockConcurrencyFailureTest {

    @Autowired
    private ProductRepository productRepository;

    private Long productId;
    private final int initialStock = 100;

    @BeforeEach
    void setup() {
        // 엔티티 참조: Product.stock 필드 :contentReference[oaicite:2]{index=2}:contentReference[oaicite:3]{index=3}
        Product product = Product.builder()
                .name("concurrency-failure-" + UUID.randomUUID())
                .price(1000.0)
                .discount(0)
                .brand("TestBrand")
                .stock(initialStock)
                .description("동시성 실패 테스트용 상품")
                .category("Test")
                .build();
        productRepository.save(product);
        productId = product.getProductIdx();
    }

    @Test
    void 동시성_제어_없을_때_재고_차감_누락이_발생하는가() throws InterruptedException {
        int threadCount = 10;
        int purchaseQuantity = 5;
        int expectedFinalStock = initialStock - threadCount * purchaseQuantity;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 락 없이 동시에 재고 읽고 차감 저장
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    Product p = productRepository.findById(productId)
                            .orElseThrow(() -> new RuntimeException("상품이 없습니다"));
                    // 재고 차감
                    p.setStock(p.getStock() - purchaseQuantity);
                    productRepository.save(p);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Product updated = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 없습니다"));
        int actualFinalStock = updated.getStock();

        System.out.println("기대 최종 재고: " + expectedFinalStock);
        System.out.println("실제 최종 재고: " + actualFinalStock);

        // 동시성 제어가 없으면 차감 누락이 발생하므로, 기대값과 불일치해야 함
        assertNotEquals(expectedFinalStock, actualFinalStock,
                "동시성 제어 없이 재고가 올바르게 차감되지 않아야 합니다");
    }
}
