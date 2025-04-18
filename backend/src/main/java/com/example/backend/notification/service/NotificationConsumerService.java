package com.example.backend.notification.service;

import com.example.backend.notification.model.NotificationType;
import com.example.backend.notification.model.UserNotification;
import com.example.backend.notification.model.dto.RealTimeNotificationDto;
import com.example.backend.notification.repository.UserNotificationRepository;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumerService {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;

    // WebSocket 메시지를 전송할 엔드포인트 상수 정의
    private static final String WS_ENDPOINT_USER_NOTIFICATION = "/queue/notification";

    /**
     * 품절 임박 알림 수신 및 처리
     */
    @KafkaListener(
            topics = "low-stock-notifications",
            groupId = "notification-group",
            containerFactory = "realTimeKafkaListenerContainerFactory"
    )
    @Transactional
    public void consumeLowStockNotification(RealTimeNotificationDto notification) {
        log.info("Received low stock notification: {}", notification);

        // 1. 알림 저장
        saveNotification(notification);

        // 2. WebSocket을 통해 특정 사용자에게 알림 전송
        String userDestination = WS_ENDPOINT_USER_NOTIFICATION + "." + notification.getUserIdx();
        messagingTemplate.convertAndSendToUser(
                notification.getUserIdx().toString(),
                userDestination,
                notification
        );
    }

    /**
     * 재입고 알림 수신 및 처리
     */
    @KafkaListener(
            topics = "restock-notifications",
            groupId = "notification-group",
            containerFactory = "realTimeKafkaListenerContainerFactory"
    )
    @Transactional
    public void consumeRestockNotification(RealTimeNotificationDto notification) {
        log.info("Received restock notification: {}", notification);

        // 1. 알림 저장
        saveNotification(notification);

        // 2. WebSocket을 통해 특정 사용자에게 알림 전송
        String userDestination = WS_ENDPOINT_USER_NOTIFICATION + "." + notification.getUserIdx();
        messagingTemplate.convertAndSendToUser(
                notification.getUserIdx().toString(),
                userDestination,
                notification
        );
    }

    /**
     * 가격 인하 알림 수신 및 처리
     */
    @KafkaListener(
            topics = "discount-notifications",
            groupId = "notification-group",
            containerFactory = "realTimeKafkaListenerContainerFactory"
    )
    @Transactional
    public void consumePriceDropNotification(RealTimeNotificationDto notification) {
        log.info("Received price drop notification: {}", notification);
        System.out.println("가격 인하 수신 처리 " + notification);
        // 1. 알림 저장
        saveNotification(notification);

        // 2. WebSocket을 통해 특정 사용자에게 알림 전송
        String userDestination = WS_ENDPOINT_USER_NOTIFICATION + "." + notification.getUserIdx();
        messagingTemplate.convertAndSendToUser(
                notification.getUserIdx().toString(),
                userDestination,
                notification
        );
    }

    /**
     * 주문 완료 알림 수신 및 처리
     */
    @KafkaListener(
            topics = "order-complete-notifications",
            groupId = "notification-group",
            containerFactory = "realTimeKafkaListenerContainerFactory"
    )
    @Transactional
    public void consumeOrderCompleteNotification(RealTimeNotificationDto notification) {
        log.info("Received order complete notification: {}", notification);

        // 1. 알림 저장
        saveNotification(notification);

        // 2. WebSocket을 통해 특정 사용자에게 알림 전송
        String userDestination = WS_ENDPOINT_USER_NOTIFICATION + "." + notification.getUserIdx();
        messagingTemplate.convertAndSendToUser(
                notification.getUserIdx().toString(),
                userDestination,
                notification
        );
    }

    /**
     * 알림 내역 저장 (데이터베이스에 알림 기록 저장)
     * RealTimeNotificationDto를 UserNotification 엔티티로 변환하여 저장
     */
    private void saveNotification(RealTimeNotificationDto dto) {
        try {
            // 사용자 정보 조회
            User user = userRepository.findById(dto.getUserIdx())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserIdx()));

            // UserNotification 엔티티 생성 및 저장
            UserNotification userNotification = UserNotification.builder()
                    .notificationType(dto.getNotificationType())
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .createdAt(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now())
                    .isRead(false)
                    .user(user)
                    // 템플릿 기반 알림이 아니므로 template은 null로 설정
                    .template(null)
                    .build();

            userNotificationRepository.save(userNotification);
            log.info("Notification saved to database: {}", userNotification);
        } catch (Exception e) {
            log.error("Failed to save notification: {}", e.getMessage(), e);
            // 알림 저장 실패 시에도 WebSocket 메시지는 전송할 수 있도록 예외를 던지지 않음
        }
    }

}