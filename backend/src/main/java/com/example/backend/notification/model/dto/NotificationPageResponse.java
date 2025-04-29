package com.example.backend.notification.model.dto;

import com.example.backend.notification.model.UserNotification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPageResponse {

    @Schema(description = "현재 페이지", example = "1")
    private int page;

    @Schema(description = "한 페이지에 보여지는 알림 수", example = "10")
    private int size;

    @Schema(description = "전체 알림 수", example = "120")
    private long totalElements;

    @Schema(description = "전체 페이지 수", example = "12")
    private int totalPages;

    @Schema(description = "다음 페이지 존재 여부", example = "true")
    private boolean hasNext;

    @Schema(description = "이전 페이지 존재 여부", example = "false")
    private boolean hasPrevious;

    @Schema(description = "알림 목록")
    private List<UserNotificationDto> notifications;

    public static NotificationPageResponse from(Page<UserNotification> notificationPage) {
        return NotificationPageResponse.builder()
                .page(notificationPage.getNumber() + 1) // 1-based
                .size(notificationPage.getSize())
                .totalElements(notificationPage.getTotalElements())
                .totalPages(notificationPage.getTotalPages())
                .hasNext(notificationPage.hasNext())
                .hasPrevious(notificationPage.hasPrevious())
                .notifications(notificationPage.getContent().stream()
                        .map(UserNotificationDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
