package com.example.backend.global.response.responseStatus;

import lombok.Getter;

@Getter
public enum WishlistResponseStatus implements BaseResponseStatus {
    // 11000번대 - Wishlist 관련
    WISHLIST_NOT_FOUND(false, 11001, "위시리스트를 찾을 수 없습니다."),
    WISHLIST_TOGGLE_FAIL(false, 11002, "위시리스트 갱신에 실패했습니다."),
    USER_NOT_LOGGED(false, 11003, "사용자가 로그인하지 않았습니다."),

    SUCCESS(true, 11000, "요청이 성공적으로 처리되었습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    WishlistResponseStatus(boolean isSuccess, int code, String message) {
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
