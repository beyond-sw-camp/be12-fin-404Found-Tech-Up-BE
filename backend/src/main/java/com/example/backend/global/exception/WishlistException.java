package com.example.backend.global.exception;

import com.example.backend.global.response.responseStatus.WishlistResponseStatus;

public class WishlistException extends BaseException {
    public WishlistException(WishlistResponseStatus status) {
        super(status);
    }
}
