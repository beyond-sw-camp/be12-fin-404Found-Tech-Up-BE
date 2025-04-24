package com.example.backend.notification.model.dto;

import com.example.backend.notification.model.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RealTimeNotificationDto {
    private NotificationType notificationType;
    private String title;
    private String content;
    private Long userIdx;          // 특정 사용자에게만 보낼 경우 사용
    private Long productIdx;     // 관련 상품 ID (있을 경우)
    private Long orderIdx;      // 주문 완료시
    private Integer stockCount;   // 재고 수량 (품절 임박 알림 등에 사용)
    private Integer discount;       // 할인율 할인 이벤트시 알림 발행
    private LocalDateTime timestamp;
}