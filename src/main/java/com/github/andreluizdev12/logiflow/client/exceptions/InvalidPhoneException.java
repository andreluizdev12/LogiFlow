package com.github.andreluizdev12.logiflow.client.exceptions;

import com.github.andreluizdev12.logiflow.shared.exceptions.DomainException;
import com.github.andreluizdev12.logiflow.shared.exceptions.ErrorCode;

public class InvalidPhoneException extends DomainException {

    public InvalidPhoneException(String mensagem) {
        super(
                mensagem,
                ErrorCode.INVALID_PHONE
        );
    }
}
