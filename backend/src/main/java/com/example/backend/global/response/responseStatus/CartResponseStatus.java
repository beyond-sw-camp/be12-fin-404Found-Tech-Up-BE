package com.example.backend.global.response.responseStatus;

import lombok.Getter;

@Getter
public enum CartResponseStatus implements BaseResponseStatus {
    // 5000번대 - Cart 관련
    CART_NOT_FOUND(false, 5001, "장바구니를 찾을 수 없습니다."),
    CART_ITEM_ADD_FAIL(false, 5002, "상품 추가에 실패했습니다."),
    CART_ITEM_UPDATE_FAIL(false, 5003, "수량 변경할 항목을 찾을 수 없습니다."),
    CART_ITEM_DELETE_FAIL(false, 5004, "장바구니 항목 삭제에 실패했습니다."),
    USER_NOT_LOGGED(false, 5005, "사용자가 로그인하지 않았습니다."),
    CART_IS_EMPTY(false, 5010, "장바구니가 비어있습니다."),

    SUCCESS(true, 5000, "요청이 성공적으로 처리되었습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    CartResponseStatus(boolean isSuccess, int code, String message) {
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
