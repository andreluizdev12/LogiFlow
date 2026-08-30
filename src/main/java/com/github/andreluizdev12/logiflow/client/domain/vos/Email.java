package com.github.andreluizdev12.logiflow.client.domain.vos;

import com.github.andreluizdev12.logiflow.client.exceptions.EmailInvalidException;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.regex.Pattern;

@Embeddable
public class Email {

    private static final int MAX_LENGTH = 150;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_-]+(?:\\.[A-Za-z0-9+_-]+)*@" +
            "(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+" +
            "[A-Za-z]{2,}$"
    );

    private String value;

    protected Email() {
        // JPA
    }

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String email) {
        if (email == null || email.isBlank()) {
            throw new EmailInvalidException("E-mail é obrigatório");
        }

        String normalized = email.trim().toLowerCase();

        if (normalized.length() > MAX_LENGTH) {
            throw new EmailInvalidException(
                    "E-mail deve possuir no máximo " + MAX_LENGTH + " caracteres"
            );
        }

        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new EmailInvalidException("E-mail inválido");
        }

        return new Email(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
