package com.example.backend.global.response.responseStatus;

import lombok.Getter;

@Getter
public enum CouponResponseStatus implements BaseResponseStatus {
    // 11000번대 - Wishlist 관련
    COUPON_NOT_FOUND(false, 8001, "쿠폰을 찾을 수 없습니다."),
    CANNOT_DELETE_COUPON(false, 8002, "쿠폰을 삭제할 수 없습니다. 누군가가 이 쿠폰을 사용했습니다."),
    DUPLICATED_EVENT_COUPON(false, 8003, "이미 발급한 쿠폰입니다."),
    OUT_OF_COUPON_STOCK(false, 8004, "쿠폰 재고가 소진되었습니다."),

    SUCCESS(true, 8000, "요청이 성공적으로 처리되었습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    CouponResponseStatus(boolean isSuccess, int code, String message) {
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
