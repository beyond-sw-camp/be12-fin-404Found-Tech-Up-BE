package com.example.backend.notification.controller;

import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseServiceImpl;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import com.example.backend.global.response.BaseResponse;
import com.example.backend.global.response.BaseResponseService;
import com.example.backend.global.response.responseStatus.CommonResponseStatus;
import com.example.backend.notification.model.Notification;
import com.example.backend.notification.model.NotificationType;
import com.example.backend.notification.model.UserNotification;
import com.example.backend.notification.model.dto.NotiRequestDto;
import com.example.backend.notification.model.dto.NotiResponseDto;
import com.example.backend.notification.model.dto.NotificationPageResponse;
import com.example.backend.notification.model.dto.RealTimeNotificationDto;
import com.example.backend.notification.repository.NotificationRepository;
import com.example.backend.notification.service.NotificationService;
import com.example.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notification")
@Tag(name = "알림 기능", description = "사용자 알림 관련 API")
public class NotificationController {
    private final NotificationService notificationService;

    private final NotificationRepository notificationRepo;

    private final BaseResponseService baseResponseService;

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private SimpUserRegistry simpUserRegistry;

    @GetMapping("/sessions")
    public void printSessions() {
        System.out.println("🔍 현재 연결된 사용자 수: " + simpUserRegistry.getUserCount());

        simpUserRegistry.getUsers().forEach(user -> {
            System.out.println("🧩 유저: " + user.getName());
            user.getSessions().forEach(session -> {
                System.out.println("  ↪ 세션 ID: " + session.getId());
                session.getSubscriptions().forEach(sub -> {
                    System.out.println("    🔔 구독 경로: " + sub.getDestination());
                });
            });
        });
    }

    @PostMapping("/test-ws")
    public void testWs(@AuthenticationPrincipal(expression = "userIdx") Long userIdx) {
        System.out.println("[✅ WS 테스트] 메시지 전송 시도: userIdx = " + userIdx);

        RealTimeNotificationDto test = RealTimeNotificationDto.builder()
                .notificationType(NotificationType.ORDER_COMPLETE)
                .title("테스트 알림")
                .content("이건 테스트 메시지입니다.")
                .timestamp(LocalDateTime.now())
                .userIdx(userIdx)
                .build();

        messagingTemplate.convertAndSendToUser(
                userIdx.toString(),
                "/queue/notification",
                test
        );

        System.out.println("[✅ WS 테스트] 메시지 전송 완료 userIdx=" + userIdx);
    }




    @PostMapping("/test-noti/{id}")
    public void testTemplateSend(@PathVariable Long id) {
        Notification tpl = notificationRepo.findById(id)
                .orElseThrow(() -> new IllegalStateException("템플릿 없음"));
        notificationService.generateFromNotification(tpl);
    }

    @GetMapping("/broadcast")
    public void broadcastTestMessage() {
        RealTimeNotificationDto test = RealTimeNotificationDto.builder()
                .notificationType(NotificationType.ORDER_COMPLETE)
                .title("브로드캐스트 테스트")
                .content("모든 유저에게 알림을 보냅니다")
                .userIdx(0L)
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/test-broadcast", test);
        System.out.println("[✅ 브로드캐스트] /topic/test-broadcast 메시지 전송 완료");
    }


    @GetMapping
    public BaseResponse<?> getAll(@AuthenticationPrincipal User loginUser,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        NotificationPageResponse response = notificationService.getAllNotifications(loginUser.getUserIdx(), page, size);
        return baseResponseService.getSuccessResponse(response, CommonResponseStatus.SUCCESS);
    }

    @GetMapping("/read")
    public BaseResponse<?> getRead(@AuthenticationPrincipal User loginUser,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        NotificationPageResponse response = notificationService.getReadNotifications(loginUser.getUserIdx(), page, size);
        return baseResponseService.getSuccessResponse(response, CommonResponseStatus.SUCCESS);
    }

    @GetMapping("/unread")
    public BaseResponse<?> getUnread(@AuthenticationPrincipal User loginUser,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        NotificationPageResponse response = notificationService.getUnreadNotifications(loginUser.getUserIdx(), page, size);
        return baseResponseService.getSuccessResponse(response, CommonResponseStatus.SUCCESS);
    }

    @GetMapping("/unread/count")
    public BaseResponse<?> getUnreadCount(@AuthenticationPrincipal User loginUser) {
        long count = notificationService.countUnreadNotifications(loginUser.getUserIdx());
        return baseResponseService.getSuccessResponse(count, CommonResponseStatus.SUCCESS);
    }



    @Operation(
            summary = "알림 읽음 처리",
            description = "알림 ID를 기반으로 해당 알림을 읽음 처리합니다. 읽은 시간도 함께 기록됩니다."
    )
    @PatchMapping("/{id}/read")
    public BaseResponse<Object> markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return baseResponseService.getSuccessResponse(CommonResponseStatus.SUCCESS);
    }

    @Operation(
            summary = "알림 삭제",
            description = "알림 ID를 기반으로 해당 알림을 삭제합니다."
    )
    @DeleteMapping("/{id}")
    public BaseResponse<Object> deleteNotification(@PathVariable Long id, @AuthenticationPrincipal User loginUser) {
        notificationService.deleteById(id, loginUser.getUserIdx());
        return baseResponseService.getSuccessResponse(CommonResponseStatus.DELETED);
    }


    // ------------------------- 관리자 전용 기능 ----------------------------------
    @Operation( summary = "전체 알림 등록", description = "모든 사용자에게 가는 알림을 등록합니다" )
    @PostMapping("/all")
    @Transactional
    public BaseResponse<Object> registerAllUserNotification(@AuthenticationPrincipal User user, @RequestBody NotiRequestDto request) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        Notification notification = notificationService.generateNotificationEntity(request);
        notificationService.generateFromNotification(notification);
        return new BaseResponseServiceImpl().getSuccessResponse(CommonResponseStatus.SUCCESS);
    }

    @Operation( summary = "수동 알림 목록 보기", description= "모든 사용자에게 간 모든 알림을 하나씩 목록으로 반환합니다")
    @GetMapping("/all")
    public BaseResponse<List<NotiResponseDto>> getAllUserNotification(@AuthenticationPrincipal User user) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        List<NotiResponseDto> result = notificationService.getAllNotificationHistory();
        return new BaseResponseServiceImpl().getSuccessResponse(result ,CommonResponseStatus.SUCCESS);
    }

    @Operation(summary= "수동 알림/이벤트 알림 지우기", description="모든 사용자에게 간 알림을 제거합니다.")
    @DeleteMapping("/all")
    public BaseResponse<Object> deleteAllUserNotification(@AuthenticationPrincipal User user, @RequestParam Long idx) {
        if (user == null || !user.getIsAdmin()) {
            return new BaseResponseServiceImpl().getFailureResponse(CommonResponseStatus.BAD_REQUEST);
        }
        notificationService.deleteAllNotification(idx);
        return new BaseResponseServiceImpl().getSuccessResponse(CommonResponseStatus.SUCCESS);
    }
}
