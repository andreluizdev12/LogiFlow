package com.github.andreluizdev12.logiflow.client.exceptions;

import com.github.andreluizdev12.logiflow.shared.exceptions.BusinessException;
import com.github.andreluizdev12.logiflow.shared.exceptions.ErrorCode;
import org.springframework.http.HttpStatus;

public class ClientAlreadyExistsException extends BusinessException {

    public ClientAlreadyExistsException(String document) {
        super(
                "Já existe um cliente com o documento informado",
                ErrorCode.CLIENT_ALREADY_EXISTS,
                HttpStatus.CONFLICT
        );
    }
}