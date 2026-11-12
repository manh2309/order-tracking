package com.xxxx.ordertracking.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CheckoutPreviewResponse(
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal shippingFee,
        BigDecimal grandTotal,
        String currency,
        boolean canPlaceOrder,
        List<CartStockIssue> issues
) {}
