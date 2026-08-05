package com.github.andreluizdev12.logiflow.client.domain.client.converter;

import com.github.andreluizdev12.logiflow.client.domain.client.vos.Document;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DocumentConverter
        implements AttributeConverter<Document, String> {

    @Override
    public String convertToDatabaseColumn(Document document) {
        return document == null ? null : document.value();
    }

    @Override
    public Document convertToEntityAttribute(String value) {
        return value == null ? null : Document.of(value);
    }
}