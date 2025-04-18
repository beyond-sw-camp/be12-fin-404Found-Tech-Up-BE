package com.example.backend.global.exception;

import com.example.backend.global.response.responseStatus.CouponResponseStatus;

public class CouponException extends BaseException {
    public CouponException(CouponResponseStatus status) {
        super(status);
    }
}
