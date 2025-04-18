package com.example.backend.notification.service;

import com.example.backend.notification.model.Notification;
import com.example.backend.notification.model.NotificationType;
import com.example.backend.notification.model.UserNotification;
import com.example.backend.notification.model.dto.NotiRequestDto;
import com.example.backend.notification.model.dto.NotiResponseDto;
import com.example.backend.notification.repository.NotificationRepository;
import com.example.backend.notification.repository.UserNotificationRepository;
import com.example.backend.user.model.User;
import com.example.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NotificationService {
    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;


    @Transactional
    public void generateFromNotification(Notification tpl) {
        // 1) 변수 치환 예제 (year, month)
        LocalDate nowDate = LocalDate.now();
        String title = tpl.getTitle()
                .replace("{{year}}", String.valueOf(nowDate.getYear()))
                .replace("{{month}}", String.format("%02d", nowDate.getMonthValue()));
        String content = tpl.getContent()
                .replace("{{year}}", String.valueOf(nowDate.getYear()))
                .replace("{{month}}", String.format("%02d", nowDate.getMonthValue()));

        LocalDateTime ts = LocalDateTime.now();

        // 2) 모든 사용자 조회
        List<User> users = userRepository.findByAlarmEnabledTrue();


        // 3) 배치 생성
        List<UserNotification> batch = users.stream().map(u ->
                UserNotification.builder()
                        .notificationType(tpl.getNotificationType())
                        .title(title)
                        .content(content)
                        .createdAt(ts)
                        .isRead(false)
                        .user(u)                   // 엔티티 필드명이 입니다
                        .template(tpl)
                        .build()
        ).toList();

        // 4) 저장
        userNotificationRepository.saveAll(batch);
    }

    public List<UserNotification> getAllNotifications(Long userIdx) {
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return userNotificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<UserNotification> getUnreadNotifications(Long userIdx) {
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return userNotificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }

    public List<UserNotification> getReadNotifications(Long userIdx) {
        User user = userRepository.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return userNotificationRepository.findByUserAndIsReadTrueOrderByCreatedAtDesc(user);
    }


    public void markAsRead(Long id) {
        // 서비스 또는 컨트롤러
        userNotificationRepository.findById(id).ifPresent(n -> {
            n.markAsRead();
            userNotificationRepository.save(n);
        });

    }

    // ------------------------- 관리자 ---------------------------------

    // 전체 알림 이력 보기
    public List<NotiResponseDto> getAllNotificationHistory() {
        List<Notification> notiList = notificationRepository.findAllByOrderByIdxDesc();
        return notiList.stream().map(noti -> NotiResponseDto.builder().idx(noti.getIdx()).notiTitle(noti.getTitle()).notiContent(noti.getContent()).notiCreated(noti.getCreatedAt()).build()).toList();
    }

    public Notification generateNotificationForAllUser(NotiRequestDto notiRequestDto) {
        Notification newNotification = Notification.builder().title(notiRequestDto.getNotiTitle()).content(notiRequestDto.getNotiContent()).createdAt(LocalDateTime.now()).cronExpression("").notificationType(NotificationType.GLOBAL).build();
        return notificationRepository.save(newNotification);
    }
    // 전체 알림 삭제
    @Transactional
    public void deleteAllNotification(Long idx) {
        if (idx == null || idx <= 4) { // 스케줄링된 이벤트 제거 방지
            throw new IllegalArgumentException("이 이벤트의 삭제는 유효한 동작이 아닙니다.");
        }
        Notification notification = notificationRepository.findById(idx).orElseThrow(()-> new IllegalArgumentException("찾을 수 없는 이벤트 번호입니다"));
        List<UserNotification> issuedNotifications = userNotificationRepository.findByTemplate(notification);
        if (!issuedNotifications.isEmpty()) {
            userNotificationRepository.deleteAll(issuedNotifications);
        }
        notificationRepository.delete(notification);
    }
}
