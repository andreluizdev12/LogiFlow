package com.github.andreluizdev12.logiflow.client.exceptions;

import com.github.andreluizdev12.logiflow.shared.exceptions.DomainException;
import com.github.andreluizdev12.logiflow.shared.exceptions.ErrorCode;

public class EmailInvalidException extends DomainException {

    public EmailInvalidException(String mensagem) {
        super(
                mensagem,
                ErrorCode.INVALID_EMAIL
        );
    }
}
