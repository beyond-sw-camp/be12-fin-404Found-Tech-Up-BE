package com.example.backend.notification.service;

import com.example.backend.notification.model.NotificationType;
import com.example.backend.notification.model.UserNotification;
import com.example.backend.notification.model.dto.RealTimeNotificationDto;
import com.example.backend.notification.repository.UserNotificationRepository;
import com.example.backend.user.repository.UserRepository;
import com.example.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumerService {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;

    private static final String WS_ENDPOINT_USER_NOTIFICATION = "/queue/notification";

    @KafkaListener(
            topics = "low-stock-notifications",
            groupId = "notification-group",
            containerFactory = "realTimeKafkaListenerContainerFactory"
    )
    @Transactional
    public void consumeLowStockNotification(RealTimeNotificationDto notification) {
        log.info("Received low stock notification: {}", notification);
        saveNotification(notification);
        sendWebSocketNotification(notification);
    }

    @KafkaListener(
            topics = "restock-notifications",
            groupId = "notification-group",
            containerFactory = "realTimeKafkaListenerContainerFactory"
    )
    @Transactional
    public void consumeRestockNotification(RealTimeNotificationDto notification) {
        log.info("Received restock notification: {}", notification);
        saveNotification(notification);
        sendWebSocketNotification(notification);
    }

    @KafkaListener(
            topics = "discount-notifications",
            groupId = "notification-group",
            containerFactory = "realTimeKafkaListenerContainerFactory"
    )
    @Transactional
    public void consumePriceDropNotification(RealTimeNotificationDto notification) {
        log.info("Received price drop notification: {}", notification);
        saveNotification(notification);
        sendWebSocketNotification(notification);
    }

    @KafkaListener(
            topics = "order-complete-notifications",
            groupId = "notification-group",
            containerFactory = "realTimeKafkaListenerContainerFactory"
    )
    @Transactional
    public void consumeOrderCompleteNotification(RealTimeNotificationDto notification) {
        log.info("Received order complete notification: {}", notification);
        saveNotification(notification);
        sendWebSocketNotification(notification);
    }

    private void saveNotification(RealTimeNotificationDto dto) {
        try {
            User user = userRepository.findById(dto.getUserIdx())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserIdx()));

            UserNotification userNotification = UserNotification.builder()
                    .notificationType(dto.getNotificationType())
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .createdAt(dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now())
                    .isRead(false)
                    .user(user)
                    .template(null)
                    .build();

            userNotificationRepository.save(userNotification);
            log.info("Notification saved to database: {}", userNotification);
        } catch (Exception e) {
            log.error("Failed to save notification: {}", e.getMessage(), e);
        }
    }

    private void sendWebSocketNotification(RealTimeNotificationDto notification) {
        messagingTemplate.convertAndSendToUser(
                notification.getUserIdx().toString(),
                "/queue/notification",
                notification
        );

        log.info("[✅ WS 전송 완료] userIdx={}, title={}", notification.getUserIdx(), notification.getTitle());
    }

}
