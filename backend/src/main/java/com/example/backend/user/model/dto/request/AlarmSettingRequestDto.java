package com.example.backend.user.model.dto.request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmSettingRequestDto {
    /**
     * 변경할 알림 설정 값
     */
    private Boolean alarmEnabled;
}
