package com.example.backend.notification.service;

import com.example.backend.notification.model.NotificationType;
import com.example.backend.notification.model.dto.RealTimeNotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationProducerService {

    @Qualifier("realTimeKafkaTemplate")  // 명시적으로 Bean 지정
    private final KafkaTemplate<String, RealTimeNotificationDto> kafkaTemplate;

    private static final String TOPIC_LOW_STOCK_NOTIFICATION = "low-stock-notifications";
    private static final String TOPIC_RESTOCK_NOTIFICATION = "restock-notifications";
    private static final String TOPIC_DISCOUNT_NOTIFICATION = "discount-notifications";
    private static final String TOPIC_ORDER_COMPLETE_NOTIFICATION = "order-complete-notifications";

    /**
     * 품절 임박 알림 발송
     */
    public void sendLowStockNotification(Long productIdx, String productName, int stockCount, Long userIdx) {
        RealTimeNotificationDto notification = RealTimeNotificationDto.builder()
                .notificationType(NotificationType.LOW_STOCK)
                .title("품절 임박 알림")
                .content(String.format("'%s' 상품의 재고가 %d개 남았습니다. 서둘러 구매하세요!", productName, stockCount))
                .productIdx(productIdx)
                .stockCount(stockCount)
                .userIdx(userIdx)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(TOPIC_LOW_STOCK_NOTIFICATION, notification);
    }

    /**
     * 재입고 알림 발송
     */
    public void sendRestockNotification(Long productIdx, String productName, Long userIdx) {
        System.out.println("재입고 알림 생성");
        RealTimeNotificationDto notification = RealTimeNotificationDto.builder()
                .notificationType(NotificationType.RESTOCK)
                .title("재입고 알림")
                .content(String.format("관심 상품 '%s'가 재입고 되었습니다.", productName))
                .productIdx(productIdx)
                .userIdx(userIdx)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(TOPIC_RESTOCK_NOTIFICATION, notification);
    }

    /**
     * 가격 인하 알림 발송
     */
    public void sendPriceDropNotification(Long productIdx, String productName, int discount, Long userIdx) {
        RealTimeNotificationDto notification = RealTimeNotificationDto.builder()
                .notificationType(NotificationType.PRICE_DROP)
                .title("가격 인하 알림")
                .content(String.format("관심 상품 '%s'가 현재 %d%% 할인 중입니다.", productName, discount))
                .productIdx(productIdx)
                .discount(discount)
                .userIdx(userIdx)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(TOPIC_DISCOUNT_NOTIFICATION, notification);
    }



    /**
     * 주문 완료 알림 발송
     */
    public void sendOrderCompleteNotification(Long orderIdx, String productName, Long userIdx) {
        RealTimeNotificationDto notification = RealTimeNotificationDto.builder()
                .notificationType(NotificationType.ORDER_COMPLETE)
                .title("주문 완료 알림")
                .content(String.format("'%s' 상품의 주문이 완료되었습니다. 감사합니다!", productName))
                .orderIdx(orderIdx)
                .userIdx(userIdx)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(TOPIC_ORDER_COMPLETE_NOTIFICATION, notification);
    }
}
