package com.xxxx.ordertracking.dto.response;

public record CartStockIssue(String itemId, Integer requestedQuantity, Integer availableQuantity, String reason) {
}
