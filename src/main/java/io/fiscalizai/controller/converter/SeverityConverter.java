package io.fiscalizai.controller.converter;

import io.fiscalizai.model.entity.Severity;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SeverityConverter implements AttributeConverter<Severity, String> {
    @Override
    public String convertToDatabaseColumn(Severity attribute) {
        return attribute == null ? null : attribute.getSeverity();
    }

    @Override
    public Severity convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Severity.fromSeverityName(dbData);
    }
}
