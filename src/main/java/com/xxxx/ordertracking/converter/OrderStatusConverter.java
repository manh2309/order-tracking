package com.xxxx.ordertracking.converter;

import com.xxxx.ordertracking.enums.OrderStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class OrderStatusConverter implements AttributeConverter<OrderStatus, Byte> {
    @Override
    public Byte convertToDatabaseColumn(OrderStatus status) {
        return status == null ? null : status.getCode();
    }

    @Override
    public OrderStatus convertToEntityAttribute(Byte code) {
        return code == null ? null : OrderStatus.fromCode(code);
    }
}
