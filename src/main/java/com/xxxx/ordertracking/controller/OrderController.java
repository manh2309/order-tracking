package com.xxxx.ordertracking.controller;

import com.xxxx.ordertracking.common.response.ApiResponse;
import com.xxxx.ordertracking.config.AccountPrincipal;
import com.xxxx.ordertracking.dto.request.CancelRequest;
import com.xxxx.ordertracking.dto.request.CreateOrderRequest;
import com.xxxx.ordertracking.dto.response.CreateOrderResponse;
import com.xxxx.ordertracking.dto.response.OrderDetailResponse;
import com.xxxx.ordertracking.dto.response.OrderSummary;
import com.xxxx.ordertracking.enums.OrderStatus;
import com.xxxx.ordertracking.repository.OrderRepository;
import com.xxxx.ordertracking.service.OrderExportService;
import com.xxxx.ordertracking.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderRepository orders;
    private final OrderExportService exporter;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateOrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal AccountPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(orderService.createOrder(request, principal.getAccountId())));
    }

    @GetMapping
    public ApiResponse<Page<OrderSummary>> history(@AuthenticationPrincipal AccountPrincipal user,
            @RequestParam(required = false) OrderStatus status, Pageable pageable) {
        return ApiResponse.ok(orders.history(user.getAccountId(), status,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDetailResponse> detail(@AuthenticationPrincipal AccountPrincipal user,
                                                    @PathVariable String id) {
        return ApiResponse.ok(orderService.detail(user.getAccountId(), id));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<OrderSummary> cancel(@AuthenticationPrincipal AccountPrincipal user, @PathVariable String id,
                                            @Valid @RequestBody CancelRequest request) {
        return ApiResponse.ok(orderService.cancel(user.getAccountId(), id, request.reason()));
    }

    @GetMapping("/export")
    public void export(@AuthenticationPrincipal AccountPrincipal user, @RequestParam(required = false) OrderStatus status,
                       HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"orders.xlsx\"");
        exporter.write(user.getAccountId(), status, response.getOutputStream());
    }

    @PostMapping("/{id}/confirm")
    public ApiResponse<OrderSummary> confirm(@PathVariable String id) {
        return ApiResponse.ok(orderService.confirm(id));
    }
}
