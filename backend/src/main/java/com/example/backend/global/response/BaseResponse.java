package com.example.backend.global.response;

import com.example.backend.global.response.responseStatus.BaseResponseStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BaseResponse<T> {
    // 성공 / 실패 여부 반환
    private Boolean isSuccess;
    // 메시지
    private String message;
    // 코드
    private int code;
    // 전달 하는 데이터
    private T data;

    @Builder
    public BaseResponse(Boolean isSuccess, String message, int code,T data) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.code = code;
        this.data = data;
    }


}
