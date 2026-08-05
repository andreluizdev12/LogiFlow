package com.github.andreluizdev12.logiflow.shared.exceptions;

public record FieldValidationError(
        String field,
        String message
) {
}