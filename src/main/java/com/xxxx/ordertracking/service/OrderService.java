package com.xxxx.ordertracking.service;

import com.xxxx.ordertracking.dto.request.CreateOrderRequest;
import com.xxxx.ordertracking.dto.response.CreateOrderResponse;
import com.xxxx.ordertracking.dto.response.OrderSummary;
import com.xxxx.ordertracking.dto.response.OrderDetailResponse;

public interface OrderService {
    CreateOrderResponse createOrder(CreateOrderRequest request, String accountId);
    OrderSummary confirm(String orderId);
    OrderDetailResponse detail(String accountId, String id);
    OrderSummary cancel(String accountId, String id, String reason);
}
