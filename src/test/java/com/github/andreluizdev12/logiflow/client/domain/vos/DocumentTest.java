package com.github.andreluizdev12.logiflow.client.domain.vos;

import com.github.andreluizdev12.logiflow.client.exceptions.DocumentInvalidException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "095.326.265-06",
            "529.982.247-25",
            "168.995.350-09"
    })
    void of_shouldCreateCpfWhenDocumentIsValid(String rawCpf) {
        var document = Document.of(rawCpf);

        assertAll(
                () -> assertEquals(DocumentType.CPF, document.type()),
                () -> assertTrue(document.isCpf()),
                () -> assertFalse(document.isCnpj()),
                () -> assertEquals(11, document.value().length()),
                () -> assertTrue(document.value().matches("\\d{11}"))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "31.559.589/0001-96",
            "11.444.777/0001-61",
            "04.252.011/0001-10"
    })
    void of_shouldCreateCnpjWhenDocumentIsValid(String rawCnpj) {
        var document = Document.of(rawCnpj);

        assertAll(
                () -> assertEquals(DocumentType.CNPJ, document.type()),
                () -> assertFalse(document.isCpf()),
                () -> assertTrue(document.isCnpj()),
                () -> assertEquals(14, document.value().length()),
                () -> assertTrue(document.value().matches("\\d{14}"))
        );
    }

    @Test
    void of_shouldNormalizeCpfRemovingFormattingAndSpaces() {
        var document = Document.of("  095.326.265-06  ");

        assertEquals("09532626506", document.value());
    }

    @Test
    void of_shouldNormalizeCnpjRemovingFormattingAndSpaces() {
        var document = Document.of("  31.559.589 / 0001-96  ");

        assertEquals("31559589000196", document.value());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void of_shouldRejectNullBlankOrEmptyDocument(String rawDocument) {
        assertThrows(DocumentInvalidException.class, () -> Document.of(rawDocument));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "095.326.265-A6",
            "31.559.589/0001-9A",
            "095_326_265_06",
            "CPF09532626506"
    })
    void of_shouldRejectDocumentWithUnsupportedCharacters(String rawDocument) {
        assertThrows(DocumentInvalidException.class, () -> Document.of(rawDocument));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1234567890",
            "123456789012",
            "1234567890123",
            "123456789012345"
    })
    void of_shouldRejectDocumentWithInvalidLength(String rawDocument) {
        assertThrows(DocumentInvalidException.class, () -> Document.of(rawDocument));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "000.000.000-00",
            "111.111.111-11",
            "222.222.222-22",
            "333.333.333-33",
            "444.444.444-44",
            "555.555.555-55",
            "666.666.666-66",
            "777.777.777-77",
            "888.888.888-88",
            "999.999.999-99"
    })
    void of_shouldRejectCpfWithAllEqualDigits(String rawCpf) {
        assertThrows(DocumentInvalidException.class, () -> Document.of(rawCpf));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "095.326.265-05",
            "095.326.265-16",
            "529.982.247-24",
            "168.995.350-08"
    })
    void of_shouldRejectCpfWithInvalidCheckDigits(String rawCpf) {
        assertThrows(DocumentInvalidException.class, () -> Document.of(rawCpf));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00.000.000/0000-00",
            "11.111.111/1111-11",
            "22.222.222/2222-22",
            "99.999.999/9999-99"
    })
    void of_shouldRejectCnpjWithAllEqualDigits(String rawCnpj) {
        assertThrows(DocumentInvalidException.class, () -> Document.of(rawCnpj));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "31.559.589/0001-95",
            "31.559.589/0001-06",
            "11.444.777/0001-60",
            "04.252.011/0001-11"
    })
    void of_shouldRejectCnpjWithInvalidCheckDigits(String rawCnpj) {
        assertThrows(DocumentInvalidException.class, () -> Document.of(rawCnpj));
    }

    @Test
    void equals_shouldConsiderFormattedAndUnformattedDocumentEqual() {
        var formatted = Document.of("095.326.265-06");
        var unformatted = Document.of("09532626506");

        assertAll(
                () -> assertEquals(formatted, unformatted),
                () -> assertEquals(formatted.hashCode(), unformatted.hashCode())
        );
    }

    @Test
    void equals_shouldConsiderDifferentDocumentsNotEqual() {
        var first = Document.of("095.326.265-06");
        var second = Document.of("529.982.247-25");

        assertNotEquals(first, second);
    }

    @Test
    void equals_shouldReturnFalseForNullAndDifferentObjectType() {
        var document = Document.of("095.326.265-06");

        assertAll(
                () -> assertNotEquals(null, document),
                () -> assertNotEquals(document, "09532626506")
        );
    }

    @Test
    void toString_shouldReturnNormalizedValue() {
        var document = Document.of("095.326.265-06");

        assertEquals("09532626506", document.toString());
    }
}
