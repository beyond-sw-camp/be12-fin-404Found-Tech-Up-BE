package com.example.backend.notification.model;

import com.example.backend.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;



/**
 * 일반 알림 템플릿(스케줄 기반 또는 관리자 정의 알림)
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "notification")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;


    /** 템플릿 제목 (예: "[쿠폰] {{year}}-{{month}} 쿠폰 발급 완료") */
    @Column(nullable = false)
    private String title;

    /** 템플릿 내용 (예: "안녕하세요, {{year}}-{{month}} 쿠폰이 발급되었습니다.") */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /** 스케줄 표현식 (cron) */
    @Column(name = "cron_expression", nullable = false)
    private String cronExpression;

    /** 템플릿 생성/수정 시각 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}