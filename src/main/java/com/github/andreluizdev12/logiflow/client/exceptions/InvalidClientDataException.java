package com.github.andreluizdev12.logiflow.client.exceptions;

import com.github.andreluizdev12.logiflow.shared.exceptions.DomainException;
import com.github.andreluizdev12.logiflow.shared.exceptions.ErrorCode;

public class InvalidClientDataException extends DomainException {

    public InvalidClientDataException(String mensagem) {
        super(
                mensagem,
                ErrorCode.INVALID_CLIENT_DATA
        );
    }
}
