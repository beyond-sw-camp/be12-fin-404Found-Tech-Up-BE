package com.example.backend.global.response.responseStatus;


/*
enum(열거형)이란?
enum은 일정한 개수의 상수를 하나의 그룹으로 묶어서 관리하는 데이터 타입
가독성을 높이고, 실수로 잘못된 값을 사용하는 것을 방지하는 역할
자바에서는 enum을 클래스로 관리하며, 변수와 메서드를 가질 수 있음
*/
public enum CommonResponseStatus implements BaseResponseStatus {
    SUCCESS(true, 1000, "요청에 성공했습니다."),
    CREATED(true, 1001, "데이터가 성공적으로 생성되었습니다."),
    UPDATED(true, 1002, "데이터가 성공적으로 수정되었습니다."),
    DELETED(true, 1003, "데이터가 성공적으로 삭제되었습니다."),

    // 추가 에러 코드
    BAD_REQUEST(false, 9001, "잘못된 요청입니다."),
    MISSING_REQUEST_PARAMETER(false, 9002, "필수 요청 파라미터가 누락되었습니다."),
    INVALID_REQUEST_PARAMETER(false, 9003, "잘못된 요청 파라미터입니다."),
    UNSUPPORTED_HTTP_METHOD(false, 9004, "지원되지 않는 HTTP 메서드입니다."),
    DATABASE_ERROR(false, 9005, "데이터베이스 오류가 발생했습니다."),
    INTERNAL_SERVER_ERROR(false, 9006, "서버 내부 오류가 발생했습니다."),
    SERVICE_UNAVAILABLE(false, 9007, "현재 서비스가 불가능합니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    CommonResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean isSuccess() {
        return isSuccess;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}


