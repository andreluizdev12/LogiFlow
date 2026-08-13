package com.github.andreluizdev12.logiflow.shared.exceptions;

import org.springframework.http.HttpStatus;

public abstract class DomainException extends RuntimeException {

    private final ErrorCode code;

    protected DomainException(
            String message,
            ErrorCode code

    ) {
        super(message);
        this.code = code;

    }

    public ErrorCode getCode() {
        return code;
    }

}