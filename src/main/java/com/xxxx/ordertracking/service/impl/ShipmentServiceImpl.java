package com.xxxx.ordertracking.service.impl;

import com.xxxx.ordertracking.common.exception.ApiException;
import com.xxxx.ordertracking.common.exception.ErrorCode;
import com.xxxx.ordertracking.dto.request.CreateShipmentEventRequest;
import com.xxxx.ordertracking.dto.response.CreateShipmentEventResponse;
import com.xxxx.ordertracking.entity.ShipmentEvent;
import com.xxxx.ordertracking.enums.ShipmentStatus;
import com.xxxx.ordertracking.repository.ShipmentEventRepository;
import com.xxxx.ordertracking.repository.ShipmentRepository;
import com.xxxx.ordertracking.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final ShipmentEventRepository eventRepository;

    @Override
    @Transactional
    public CreateShipmentEventResponse createShipmentEvent(String shipmentId, String accountId,
                                                           CreateShipmentEventRequest request) {
        if (request.shipmentStatus() != ShipmentStatus.SHIPPING) {
            throw new ApiException(ErrorCode.INVALID_SHIPMENT_STATUS_TRANSITION);
        }

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MICROS);
        int updated = shipmentRepository.startShipping(shipmentId, accountId,
                ShipmentStatus.PICKING.getCode(), ShipmentStatus.SHIPPING.getCode(), now);
        if (updated == 0) {
            if (!shipmentRepository.existsByIdAndShipper_IdAndDeleted(shipmentId, accountId, (byte) 0)) {
                throw new ApiException(ErrorCode.SHIPMENT_NOT_FOUND);
            }
            throw new ApiException(ErrorCode.INVALID_SHIPMENT_STATUS_TRANSITION);
        }

        ShipmentEvent event = new ShipmentEvent();
        event.setShipment(shipmentRepository.getReferenceById(shipmentId));
        event.setCreatedBy(accountId);
        event.setFromStatus(ShipmentStatus.PICKING);
        event.setToStatus(ShipmentStatus.SHIPPING);
        event.setOccurredAt(now);
        eventRepository.save(event);
        return new CreateShipmentEventResponse(event.getId(), shipmentId, event.getFromStatus(),
                event.getToStatus(), event.getOccurredAt());
    }
}
