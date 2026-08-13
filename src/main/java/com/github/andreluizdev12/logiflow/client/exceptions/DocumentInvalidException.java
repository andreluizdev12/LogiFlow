package com.github.andreluizdev12.logiflow.client.exceptions;

import com.github.andreluizdev12.logiflow.shared.exceptions.BusinessException;
import com.github.andreluizdev12.logiflow.shared.exceptions.DomainException;
import com.github.andreluizdev12.logiflow.shared.exceptions.ErrorCode;
import org.springframework.http.HttpStatus;

public class DocumentInvalidException extends DomainException {

    public DocumentInvalidException(String mensagem) {
        super(
                mensagem,
                ErrorCode.INVALID_DOCUMENT
        );
    }
}