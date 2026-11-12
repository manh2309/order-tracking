package com.xxxx.ordertracking.dto.response;

import java.math.BigDecimal;

public record CartItemResponse(String itemId,
                               String variantId,
                               String productName,
                               String sku,
                               String imageUrl,
                               int quantity,
                               BigDecimal unitPrice,
                               BigDecimal lineTotal,
                               int availableQuantity) {
}
