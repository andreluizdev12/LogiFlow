package com.github.andreluizdev12.logiflow.client.exceptions;

import com.github.andreluizdev12.logiflow.shared.exceptions.BusinessException;
import com.github.andreluizdev12.logiflow.shared.exceptions.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ClientNotFoundException extends BusinessException {

    public ClientNotFoundException(UUID id) {
        super(
                "No client exists with this ID: " + id,
                ErrorCode.CLIENT_NOT_FOUND,
                HttpStatus.NOT_FOUND
        );
    }
}