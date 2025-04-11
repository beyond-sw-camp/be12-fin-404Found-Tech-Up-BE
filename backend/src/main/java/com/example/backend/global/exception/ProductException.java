package com.example.backend.global.exception;

import com.example.backend.global.response.responseStatus.ProductResponseStatus;

public class ProductException extends BaseException {
    public ProductException(ProductResponseStatus status) {
        super(status);
    }
}
