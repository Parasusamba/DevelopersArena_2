package com.developersarena.ecommerce.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BusinessException{
    public UserAlreadyExistsException(String email) {
        super(
                "user already exists with email: " + email,
                "USER_ALREADY_EXISTS",
                HttpStatus.CONFLICT
        );
    }
}
