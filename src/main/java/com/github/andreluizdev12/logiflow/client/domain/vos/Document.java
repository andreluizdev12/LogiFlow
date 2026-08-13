package com.github.andreluizdev12.logiflow.client.domain.vos;


import java.util.Objects;

public final class Document {

    private final String value;
    private final DocumentType type;

    private Document(String value, DocumentType type) {
        this.value = value;
        this.type = type;
    }

    public static Document of(String document) {
        if (document == null || document.isBlank()) {
            throw new Document("The document is mandatory.");
        }

        String normalized = normalize(document);

        if (!normalized.matches("\\d+")) {
            throw new IllegalArgumentException(
                "O documento deve conter apenas números"
            );
        }

        if (normalized.length() == 11) {
            if (!isValidCpf(normalized)) {
                throw new IllegalArgumentException("CPF inválido");
            }

            return new Document(normalized, DocumentType.CPF);
        }

        if (normalized.length() == 14) {
            if (!isValidCnpj(normalized)) {
                throw new IllegalArgumentException("CNPJ inválido");
            }

            return new Document(normalized, DocumentType.CNPJ);
        }

        throw new IllegalArgumentException(
            "O documento deve possuir 11 dígitos para CPF ou 14 para CNPJ"
        );
    }

    private static String normalize(String document) {
        return document
            .trim()
            .replaceAll("[.\\-/\\s]", "");
    }

    private static boolean isValidCpf(String cpf) {
        if (hasAllEqualDigits(cpf)) {
            return false;
        }

        int firstDigit = calculateCpfDigit(cpf.substring(0, 9), 10);

        if (firstDigit != Character.getNumericValue(cpf.charAt(9))) {
            return false;
        }

        int secondDigit = calculateCpfDigit(cpf.substring(0, 10), 11);

        return secondDigit == Character.getNumericValue(cpf.charAt(10));
    }

    private static int calculateCpfDigit(String base, int initialWeight) {
        int sum = 0;

        for (int i = 0; i < base.length(); i++) {
            int digit = Character.getNumericValue(base.charAt(i));
            sum += digit * (initialWeight - i);
        }

        int result = 11 - (sum % 11);
        return result >= 10 ? 0 : result;
    }

    private static boolean isValidCnpj(String cnpj) {
        if (hasAllEqualDigits(cnpj)) {
            return false;
        }

        int firstDigit = calculateCnpjDigit(cnpj.substring(0, 12));

        if (firstDigit != Character.getNumericValue(cnpj.charAt(12))) {
            return false;
        }

        int secondDigit = calculateCnpjDigit(cnpj.substring(0, 13));

        return secondDigit == Character.getNumericValue(cnpj.charAt(13));
    }

    private static int calculateCnpjDigit(String base) {
        int[] weights = base.length() == 12
            ? new int[]{5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2}
            : new int[]{6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int sum = 0;

        for (int i = 0; i < base.length(); i++) {
            int digit = Character.getNumericValue(base.charAt(i));
            sum += digit * weights[i];
        }

        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private static boolean hasAllEqualDigits(String value) {
        return value.chars().allMatch(character -> character == value.charAt(0));
    }

    public String value() {
        return value;
    }

    public DocumentType type() {
        return type;
    }

    public boolean isCpf() {
        return type == DocumentType.CPF;
    }

    public boolean isCnpj() {
        return type == DocumentType.CNPJ;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Document document)) {
            return false;
        }

        return value.equals(document.value);
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