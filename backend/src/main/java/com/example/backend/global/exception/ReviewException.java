package com.example.backend.global.exception;

import com.example.backend.global.response.responseStatus.ReviewResponseStatus;

public class ReviewException extends BaseException {
    public ReviewException(ReviewResponseStatus status) {
        super(status);
    }
}
