package com.example.backend.global.response.responseStatus;

import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Getter;

@Getter
public enum UserResponseStatus implements BaseResponseStatus {
    // 2000번대 - User 관련 에러
    // 성공적인 응답을 위한 추가
    SUCCESS(true, 2000, "요청이 성공적으로 처리되었습니다."),
    // 2001 - 2010 사용자 로그인 관련 에러
    NICKNAME_NOT_FOUND(false, 2001, "별명 중복 확인이 필요합니다."),
    INVALID_PASSWORD(false, 2002, "비밀번호가 일치하지 않습니다."),

    UNDEFINED_USER(false, 2003, "로그인된 사용자가 없습니다."),
//    UNAUTHORIZED_USER(false, 2004, "인증되지 않은 사용자입니다."),
//    FORBIDDEN_USER(false, 2005, "접근 권한이 없습니다."),
//    INVALID_LOGIN_CREDENTIALS(false, 2006, "아이디 또는 비밀번호가 올바르지 않습니다."),
//    ACCOUNT_LOCKED(false, 2007, "계정이 잠겼습니다."),
//    ACCOUNT_EXPIRED(false, 2008, "계정이 만료되었습니다."),
//    PASSWORD_EXPIRED(false, 2009, "비밀번호가 만료되었습니다."),
    INVALID_PASSWORD_FAIL(false, 2010, "잘못된 비밀번호입니다."),

    // 2011 - 2020 사용자 이메일 인증 관련 에러
    INVALID_EMAIL_FORMAT(false, 2011, "유효하지 않은 이메일 형식입니다."),
    EMAIL_ALREADY_IN_USE(false, 2012, "해당 이메일은 이미 사용 중입니다."),
    EMAIL_SEND_FAIL(false, 2013, "이메일 전송에 실패했습니다."),
    EMAIL_VERIFY_NOTFOUND(false, 2014, "이메일 인증 코드가 존재하지 않습니다."),
    EMAIL_VERIFY_EXPIRED(false, 2015, "이메일 인증 코드가 만료 되었습니다."),
    EMAIL_VERIFY_FAIL(false, 2016, "이메일 인증 코드가 일치하지 않습니다."),

    // 2021 - 2030 사용자 마이페이지 관련 에러
    USER_UPDATE_FAIL(false, 2021, "사용자 정보가 변경되지 않았습니다."),
    USER_DELETE_FAIL(false, 2022, "사용자 탈퇴가 실패하였습니다."),
    USER_PRODUCT_FAIL(false, 2023, "사용자의 제품을 찾을 수 없습니다."),
    USER_PRODUCT_IN_USE(false, 2024, "이미 등록된 사용자의 제품입니다."),

    // 2100 - 기타 에러
    TOO_MANY_FAILED_ATTEMPTS(false, 2101, "로그인 시도 횟수가 초과되었습니다."),
    PHONE_NUMBER_ALREADY_IN_USE(false, 2102, "해당 전화번호는 이미 사용 중입니다."),
    UNIDENTIFIED_ROLE(false, 2103, "유효하지 않은 직책입니다"),
    USER_SAVE_FAIL(false, 2104, "유저 정보를 저장하는데 실패했습니다"),
  
    // 2200 - 병합 충돌날 경우 임시로 여기 보관함
    INVALID_USER_ID(false, 2203, "잘못된 사용자 ID입니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    UserResponseStatus(boolean isSuccess, int code, String message) {
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
