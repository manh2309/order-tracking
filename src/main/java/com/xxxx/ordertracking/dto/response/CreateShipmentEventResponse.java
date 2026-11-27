package com.xxxx.ordertracking.dto.response;

import com.xxxx.ordertracking.enums.ShipmentStatus;

import java.time.LocalDateTime;

public record CreateShipmentEventResponse(String eventId, String shipmentId, ShipmentStatus fromStatus,
                                          ShipmentStatus toStatus, LocalDateTime occurredAt) {
}
