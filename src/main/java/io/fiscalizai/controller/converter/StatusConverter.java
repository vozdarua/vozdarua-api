package io.fiscalizai.controller.converter;

import io.fiscalizai.model.entity.Status;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<Status, String> {
    @Override
    public String convertToDatabaseColumn(Status attribute) {
        return attribute == null ? null : attribute.getStatus();
    }

    @Override
    public Status convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Status.fromStatusName(dbData);
    }
}
