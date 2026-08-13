package com.github.andreluizdev12.logiflow.client.exceptions;

import com.github.andreluizdev12.logiflow.shared.exceptions.DomainException;
import com.github.andreluizdev12.logiflow.shared.exceptions.ErrorCode;

public class InvalidDocumentForPersonTypeException extends DomainException {

    public InvalidDocumentForPersonTypeException(String mensagem) {
        super(
            mensagem,
            ErrorCode.INVALID_DOCUMENT_FOR_PERSON_TYPE

        );
    }
}