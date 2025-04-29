package com.example.backend.global.response.responseStatus;

import lombok.Getter;

@Getter
public enum ProductResponseStatus implements BaseResponseStatus {
    // 3000번대 - Product 관련 에러
    PRODUCT_NOT_FOUND(false, 3001, "제품을 찾을 수 없습니다."),
    INVALID_PRODUCT_ID(false, 3003, "잘못된 사용자 ID입니다."),
    PRODUCT_SAVE_FAIL(false, 3016, "제품 정보를 저장하는데 실패했습니다"),

    PRODUCT_DELETE_FAIL( false, 3020, "제품을 삭제할 수 없습니다. 이미 누군가 구매했거나 쿠폰이 발급되었을 수 있습니다."),

    // 성공적인 응답을 위한 추가
    SUCCESS(true, 3000, "요청이 성공적으로 처리되었습니다.");



    private final boolean isSuccess;
    private final int code;
    private final String message;

    ProductResponseStatus(boolean isSuccess, int code, String message) {
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
