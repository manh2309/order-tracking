package com.xxxx.ordertracking.controller;

import com.xxxx.ordertracking.common.response.ApiResponse;
import com.xxxx.ordertracking.config.AccountPrincipal;
import com.xxxx.ordertracking.dto.request.CreateOrderRequest;
import com.xxxx.ordertracking.dto.response.CreateOrderResponse;
import com.xxxx.ordertracking.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateOrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal AccountPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(orderService.createOrder(request, principal.getAccountId())));
    }
}
