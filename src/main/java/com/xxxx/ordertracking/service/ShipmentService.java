package com.xxxx.ordertracking.service;

import com.xxxx.ordertracking.dto.request.CreateShipmentEventRequest;
import com.xxxx.ordertracking.dto.response.CreateShipmentEventResponse;

public interface ShipmentService {
    CreateShipmentEventResponse createShipmentEvent(String shipmentId, String accountId, CreateShipmentEventRequest request);
}
