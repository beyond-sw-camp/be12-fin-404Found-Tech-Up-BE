package com.example.backend.notification.model.dto;

import com.example.backend.notification.model.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotiRequestDto {
    @Schema(description = "알림 제목", example = "제품 업그레이드 추천")
    private String notiTitle;
    @Schema(description = "알림 내용", example = "현재 사용중인 SSD의 업그레이드 버전 제품이 할인중입니다.")
    private String notiContent;

}

