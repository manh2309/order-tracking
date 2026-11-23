package com.xxxx.ordertracking.dto.response;

import java.util.List;

public record OrderDetailResponse(OrderSummary order, String recipientName, String recipientPhone,
                                  String addressLine, String wardCode, String provinceCode,
                                  String paymentMethod, String cancelReason, List<OrderLineResponse> items) {}
