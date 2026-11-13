package com.xxxx.ordertracking.dto.response;

import java.math.BigDecimal;

public record CreateOrderResponse(String orderId, String code, String status, BigDecimal grandTotal, String currency) {
}
