package com.xxxx.ordertracking.controller;

import com.xxxx.ordertracking.common.response.ApiResponse;
import com.xxxx.ordertracking.config.AccountPrincipal;
import com.xxxx.ordertracking.dto.request.CreateShipmentEventRequest;
import com.xxxx.ordertracking.dto.response.CreateShipmentEventResponse;
import com.xxxx.ordertracking.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {
    private final ShipmentService shipmentService;

    @PostMapping("/{shipmentId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateShipmentEventResponse> createShipmentEvent(
            @PathVariable String shipmentId,
            @AuthenticationPrincipal AccountPrincipal principal,
            @Valid @RequestBody CreateShipmentEventRequest request) {
        return ApiResponse.created(shipmentService.createShipmentEvent(shipmentId, principal.getAccountId(), request));
    }
}
