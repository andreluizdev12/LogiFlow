package com.github.andreluizdev12.logiflow.shared.exceptions;

import org.springframework.http.HttpStatus;

public abstract class BusinessException extends RuntimeException {

    private final ErrorCode code;
    private final HttpStatus status;

    protected BusinessException(
            String message,
            ErrorCode code,
            HttpStatus status
    ) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public ErrorCode getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}