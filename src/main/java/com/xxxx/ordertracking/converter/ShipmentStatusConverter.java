package com.xxxx.ordertracking.converter;

import com.xxxx.ordertracking.enums.ShipmentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ShipmentStatusConverter implements AttributeConverter<ShipmentStatus, Byte> {
    @Override
    public Byte convertToDatabaseColumn(ShipmentStatus status) {
        return status == null ? null : status.getCode();
    }

    @Override
    public ShipmentStatus convertToEntityAttribute(Byte code) {
        return code == null ? null : ShipmentStatus.fromCode(code);
    }
}
