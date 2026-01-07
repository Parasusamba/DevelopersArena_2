package com.developersarena.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

public class NotFoundException extends BusinessException {
    public NotFoundException(String message, String errorCode) {
        super(
                message,
                errorCode,
                HttpStatus.NOT_FOUND
        );
    }
}
