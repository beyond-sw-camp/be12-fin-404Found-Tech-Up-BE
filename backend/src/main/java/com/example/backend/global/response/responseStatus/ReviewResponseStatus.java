package com.example.backend.global.response.responseStatus;

import lombok.Getter;

@Getter
public enum ReviewResponseStatus implements BaseResponseStatus {
    // 10000번대 - Review 관련
    REVIEW_NOT_FOUND(false, 10001, "리뷰를 찾을 수 없습니다."),
    REVIEW_CREATION_FAIL(false, 10002, "리뷰 작성에 실패했습니다."),
    REVIEW_UPDATE_FAIL(false, 10003, "리뷰 수정에 실패했습니다."),
    REVIEW_DELETE_FAIL(false, 10004, "리뷰 삭제에 실패했습니다."),
    REVIEW_USER_MISMATCH(false, 10005, "리뷰 작성자가 아닙니다."),
    REVIEW_USER_NOT_FOUND(false, 10006, "접근 권한이 없습니다. 로그인해주세요."),

    SUCCESS(true, 10000, "요청이 성공적으로 처리되었습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    ReviewResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean isSuccess() { return isSuccess; }
    @Override
    public int getCode() { return code; }
    @Override
    public String getMessage() { return message; }
}
