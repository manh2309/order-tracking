package com.xxxx.ordertracking.controller;

import com.xxxx.ordertracking.common.response.ApiResponse;
import com.xxxx.ordertracking.config.AccountPrincipal;
import com.xxxx.ordertracking.dto.response.*;
import com.xxxx.ordertracking.enums.OrderStatus;
import com.xxxx.ordertracking.repository.OrderRepository;
import com.xxxx.ordertracking.service.CustomerOrderService;
import com.xxxx.ordertracking.service.OrderExportService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class CustomerOrderController {
    private final OrderRepository orders;
    private final CustomerOrderService customerOrders;
    private final OrderExportService exporter;

    @GetMapping
    public ApiResponse<Page<OrderSummary>> history(@AuthenticationPrincipal AccountPrincipal user,
            @RequestParam(required = false) OrderStatus status, Pageable pageable) {
        return ApiResponse.ok(orders.history(user.getAccountId(), status,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDetailResponse> detail(@AuthenticationPrincipal AccountPrincipal user,
                                                    @PathVariable String id) {
        return ApiResponse.ok(customerOrders.detail(user.getAccountId(), id));
    }

    public record CancelRequest(@NotBlank @Size(max = 500) String reason) {}

    @PostMapping("/{id}/cancel")
    public ApiResponse<OrderSummary> cancel(@AuthenticationPrincipal AccountPrincipal user, @PathVariable String id,
                                            @Valid @RequestBody CancelRequest request) {
        return ApiResponse.ok(customerOrders.cancel(user.getAccountId(), id, request.reason()));
    }

    @GetMapping("/export")
    public void export(@AuthenticationPrincipal AccountPrincipal user, @RequestParam(required = false) OrderStatus status,
                       HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"orders.xlsx\"");
        exporter.write(user.getAccountId(), status, response.getOutputStream());
    }
}
