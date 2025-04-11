package com.example.backend.global.exception;

import com.example.backend.global.response.responseStatus.OrderResponseStatus;

public class OrderException extends BaseException {
    public OrderException(OrderResponseStatus status) {
        super(status);
    }
}
