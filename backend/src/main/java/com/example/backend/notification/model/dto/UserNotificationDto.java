package com.example.backend.notification.model.dto;

import com.example.backend.notification.model.UserNotification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationDto {
    private Long id;
    private String notificationType;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime checkedAt;
    private boolean read;

    public static UserNotificationDto from(UserNotification entity) {
        return UserNotificationDto.builder()
                .id(entity.getId())
                .notificationType(entity.getNotificationType().name())
                .title(entity.getTitle())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .checkedAt(entity.getCheckedAt())
                .read(Boolean.TRUE.equals(entity.getIsRead()))
                .build();
    }
}
