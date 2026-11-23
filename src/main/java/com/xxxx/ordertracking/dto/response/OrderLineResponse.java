package com.xxxx.ordertracking.dto.response;

import java.math.BigDecimal;

public record OrderLineResponse(String sku, String productName, String variantDescription,
                                int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {}
