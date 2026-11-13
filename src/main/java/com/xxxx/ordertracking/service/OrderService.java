package com.xxxx.ordertracking.service;

import com.xxxx.ordertracking.dto.request.CreateOrderRequest;
import com.xxxx.ordertracking.dto.response.CreateOrderResponse;

public interface OrderService {
    CreateOrderResponse createOrder(CreateOrderRequest request, String accountId);
}
