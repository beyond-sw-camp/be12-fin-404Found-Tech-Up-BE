package com.example.backend.global.response.responseStatus;

import lombok.Getter;

@Getter
public enum OrderResponseStatus implements BaseResponseStatus {
    // 4000번대 - Order 관련 응답 상태
    ORDER_NOT_FOUND(false, 4001, "해당 주문을 찾을 수 없습니다."),
    ORDER_SAVE_FAIL(false, 4002, "주문 정보를 저장하는데 실패했습니다."),
    PAYMENT_AMOUNT_MISMATCH(false, 4003, "결제 금액이 주문 총액과 일치하지 않습니다."),
    ORDER_CANCEL_FAIL(false, 4004, "주문 취소에 실패했습니다."),
    REFUND_REQUEST_FAIL(false, 4005, "환불 요청에 실패했습니다."),

    // 성공적인 응답 상태
    SUCCESS(true, 4000, "요청이 성공적으로 처리되었습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    OrderResponseStatus(boolean isSuccess, int code, String message) {
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
