package com.example.backend.notification.controller;

import com.example.backend.notification.model.Notification;
import com.example.backend.notification.model.UserNotification;
import com.example.backend.notification.model.dto.NotiRequestDto;
import com.example.backend.notification.model.dto.NotiResponseDto;
import com.example.backend.notification.repository.NotificationRepository;
import com.example.backend.notification.service.NotificationService;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notification")
@Tag(name = "알림 기능", description = "사용자 알림 관련 API")
public class NotificationController {
    private final NotificationService notificationService;

    private final NotificationRepository notificationRepo;

    @PostMapping("/test-noti/{id}")
    public void testTemplateSend(@PathVariable Long id) {
        Notification tpl = notificationRepo.findById(id)
                .orElseThrow(() -> new IllegalStateException("템플릿 없음"));
        notificationService.generateFromNotification(tpl);
    }


    @Operation(summary = "전체 알림 조회", description = "사용자의 모든 알림을 최신순으로 조회합니다.")
    @GetMapping
    public List<UserNotification> getAll(@AuthenticationPrincipal(expression = "userIdx") Long userIdx) {
        return notificationService.getAllNotifications(userIdx);
    }

    @Operation(summary = "읽지 않은 알림 조회", description = "사용자의 읽지 않은 알림을 최신순으로 조회합니다.")
    @GetMapping("/unread")
    public List<UserNotification> getUnread(@AuthenticationPrincipal(expression = "userIdx") Long userIdx) {
        return notificationService.getUnreadNotifications(userIdx);
    }

    @Operation(summary = "읽은 알림 조회", description = "사용자의 읽은 알림을 최신순으로 조회합니다.")
    @GetMapping("/read")
    public List<UserNotification> getRead(@AuthenticationPrincipal(expression = "userIdx") Long userIdx) {
        return notificationService.getReadNotifications(userIdx);
    }

    @Operation(
            summary = "알림 읽음 처리",
            description = "알림 ID를 기반으로 해당 알림을 읽음 처리합니다. 읽은 시간도 함께 기록됩니다."
    )
    @PatchMapping("/{id}/read")
    public void markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }

}
