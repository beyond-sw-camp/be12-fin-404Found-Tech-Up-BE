package com.example.backend.global.exception;

import com.example.backend.global.response.responseStatus.UserResponseStatus;

public class UserException extends BaseException {
    public UserException(UserResponseStatus status) {
        super(status);
    }
}
