package com.developersarena.ecommerce.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public abstract class BusinessException extends RuntimeException{
    @Getter
    private final String errorCode;
    @Getter
    private final HttpStatus status;

    protected BusinessException(
            String message,
            String errorCode,
            HttpStatus status

    ) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}
