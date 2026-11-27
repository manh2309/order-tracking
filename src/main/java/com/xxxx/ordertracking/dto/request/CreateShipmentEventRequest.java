package com.xxxx.ordertracking.dto.request;

import com.xxxx.ordertracking.enums.ShipmentStatus;
import jakarta.validation.constraints.NotNull;

public record CreateShipmentEventRequest(@NotNull ShipmentStatus shipmentStatus) {
}
