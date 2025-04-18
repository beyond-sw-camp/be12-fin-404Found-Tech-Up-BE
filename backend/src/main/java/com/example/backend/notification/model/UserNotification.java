package com.example.backend.notification.model;

import com.example.backend.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;




/**
 * 사용자별 발행된 알림 인스턴스
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class UserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 알림 유형 */
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    /** 실제 알림 제목 */
    @Column(nullable = false)
    private String title;

    /** 실제 알림 내용 */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /** 알림 생성 시각 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 읽음 처리 시각 (null: 미확인) */
    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    /** 읽음 여부 (false: unread, true: read) */
    private Boolean isRead = false;

    /** 알림을 받은 사용자 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_idx", nullable = false)
    private User user;

    /** 참조된 일반 알림 템플릿 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id")
    private Notification template;

    public void markAsRead() {
        if (!this.isRead) {
            this.isRead = true;
            this.checkedAt = LocalDateTime.now();
        }
    }
}
