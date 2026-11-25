package com.xxxx.ordertracking.dto.response;

import com.xxxx.ordertracking.enums.ShipmentStatus;

public record CreateShipmentResponse(String shipmentId, String orderId, ShipmentStatus shipmentStatus) {
}
