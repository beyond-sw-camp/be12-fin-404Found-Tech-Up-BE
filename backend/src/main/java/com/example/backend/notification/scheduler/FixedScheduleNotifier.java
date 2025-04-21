// com/example/backend/notification/scheduler/FixedScheduleNotifier.java
package com.example.backend.notification.scheduler;

import com.example.backend.notification.model.Notification;
import com.example.backend.notification.repository.NotificationRepository;
import com.example.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FixedScheduleNotifier {
    private final NotificationRepository notificationRepo;
    private final NotificationService notiSvc;

    /** 매월 1일 00:00: 쿠폰 발급 알림 (템플릿 ID=1) */
    @Scheduled(cron = "0 0 0 1 * *")
    public void monthlyCoupon() {
        Notification tpl = notificationRepo.findById(1L)
                .orElseThrow(() -> new IllegalStateException("템플릿[1] 없음"));
        notiSvc.generateFromNotification(tpl);
    }

    /** 매일 09:00: 장바구니 방치 리마인더 (템플릿 ID=2) */
    @Scheduled(cron = "0 0 9 * * *")
    public void cartReminder() {
        Notification tpl = notificationRepo.findById(2L)
                .orElseThrow(() -> new IllegalStateException("템플릿[2] 없음"));
        notiSvc.generateFromNotification(tpl);
    }

    /** 매일 10:00: 위시리스트 요약 알림 (템플릿 ID=3) */
    @Scheduled(cron = "0 0 10 * * *")
    public void wishlistSummary() {
        Notification tpl = notificationRepo.findById(3L)
                .orElseThrow(() -> new IllegalStateException("템플릿[3] 없음"));
        notiSvc.generateFromNotification(tpl);
    }

    /** 매주 월요일 08:00: 이번 주 인기 상품 알림 (템플릿 ID=4) */
    @Scheduled(cron = "0 0 8 * * MON")
    public void weeklyTopProducts() {
        Notification tpl = notificationRepo.findById(4L)
                .orElseThrow(() -> new IllegalStateException("템플릿[4] 없음"));
        notiSvc.generateFromNotification(tpl);
    }
}
