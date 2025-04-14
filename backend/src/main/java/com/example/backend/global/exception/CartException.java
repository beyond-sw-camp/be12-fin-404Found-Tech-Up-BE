package com.example.backend.global.exception;

import com.example.backend.global.response.responseStatus.CartResponseStatus;

public class CartException extends BaseException {
    public CartException(CartResponseStatus status) {
        super(status);
    }
}
