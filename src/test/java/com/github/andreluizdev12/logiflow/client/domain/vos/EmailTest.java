package com.github.andreluizdev12.logiflow.client.domain.vos;

import com.github.andreluizdev12.logiflow.client.exceptions.EmailInvalidException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "joao.silva@email.com",
            "usuario+logistica@empresa.com.br",
            "nome_sobrenome@dominio.io",
            "usuario-123@sub.dominio.com"
    })
    void of_shouldCreateEmailWhenFormatIsValid(String rawEmail) {
        var email = Email.of(rawEmail);

        assertEquals(rawEmail, email.value());
    }

    @Test
    void of_shouldNormalizeEmailRemovingOuterSpacesAndConvertingToLowercase() {
        var email = Email.of("  JOAO.SILVA@EMAIL.COM  ");

        assertEquals("joao.silva@email.com", email.value());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void of_shouldRejectNullBlankOrEmptyEmail(String rawEmail) {
        assertThrows(EmailInvalidException.class, () -> Email.of(rawEmail));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "joao",
            "joao@",
            "@email.com",
            "joao@email",
            "joao email@email.com",
            "joao@email.c",
            "joao@@email.com",
            "joao@email,com"
    })
    void of_shouldRejectEmailWithInvalidFormat(String rawEmail) {
        assertThrows(EmailInvalidException.class, () -> Email.of(rawEmail));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            ".joao@email.com",
            "joao.@email.com",
            "joao..silva@email.com",
            "joao@email..com",
            "joao@-email.com",
            "joao@email-.com",
            "joao@email_com.com"
    })
    void of_shouldRejectStructurallyInvalidEmail(String rawEmail) {
        assertThrows(EmailInvalidException.class, () -> Email.of(rawEmail));
    }

    @Test
    void of_shouldAcceptEmailWithExactlyMaximumLength() {
        var rawEmail = "a".repeat(140) + "@email.com";

        var email = Email.of(rawEmail);

        assertEquals(150, email.value().length());
    }

    @Test
    void of_shouldRejectEmailLongerThanMaximumLength() {
        var rawEmail = "a".repeat(141) + "@email.com";

        assertThrows(EmailInvalidException.class, () -> Email.of(rawEmail));
    }

    @Test
    void equals_shouldIgnoreCaseAndOuterSpacesAfterNormalization() {
        var normalized = Email.of("joao.silva@email.com");
        var raw = Email.of("  JOAO.SILVA@EMAIL.COM  ");

        assertAll(
                () -> assertEquals(normalized, raw),
                () -> assertEquals(normalized.hashCode(), raw.hashCode())
        );
    }

    @Test
    void equals_shouldConsiderDifferentEmailsNotEqual() {
        var first = Email.of("joao@email.com");
        var second = Email.of("maria@email.com");

        assertNotEquals(first, second);
    }

    @Test
    void equals_shouldReturnFalseForNullAndDifferentObjectType() {
        var email = Email.of("joao@email.com");

        assertAll(
                () -> assertNotEquals(null, email),
                () -> assertNotEquals(email, "joao@email.com")
        );
    }

    @Test
    void toString_shouldReturnNormalizedValue() {
        var email = Email.of("  JOAO@EMAIL.COM  ");

        assertEquals("joao@email.com", email.toString());
    }
}
