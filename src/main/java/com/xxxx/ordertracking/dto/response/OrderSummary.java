package com.xxxx.ordertracking.dto.response;

import com.xxxx.ordertracking.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummary(String id, String code, LocalDateTime createdAt, OrderStatus status,
                           BigDecimal subtotal, BigDecimal discountAmount, BigDecimal shippingFee,
                           BigDecimal grandTotal, String currency) {}
