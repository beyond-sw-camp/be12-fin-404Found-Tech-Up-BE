package com.example.backend.global.exception;

import com.example.backend.global.response.responseStatus.BoardResponseStatus;

public class ProductException extends BaseException {
    public ProductException(BoardResponseStatus status) {
        super(status);
    }
}
