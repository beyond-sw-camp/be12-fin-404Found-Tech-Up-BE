package com.example.backend.user.model.dto.response;

import lombok.Getter;

@Getter
public class AlarmSettingResponseDto {
    /**
     * 현재 알림 설정 상태
     */
    private final Boolean alarmEnabled;

    public AlarmSettingResponseDto(Boolean alarmEnabled) {
        this.alarmEnabled = alarmEnabled;
    }
}