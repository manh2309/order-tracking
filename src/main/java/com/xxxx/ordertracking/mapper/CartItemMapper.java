package com.xxxx.ordertracking.mapper;

import com.xxxx.ordertracking.dto.response.CartItemResponse;
import com.xxxx.ordertracking.entity.CartItem;
import com.xxxx.ordertracking.entity.ProductVariant;

import java.math.BigDecimal;

public final class CartItemMapper {
    private CartItemMapper() {}

    public static CartItemResponse toResponse(CartItem item) {
        ProductVariant variant = item.getProductVariant();
        int quantity = item.getQuantity();
        BigDecimal unitPrice = variant.getPrice();

        return new CartItemResponse(
                item.getId(),
                variant.getId(),
                variant.getProduct().getName(),
                variant.getSku(),
                variant.getImageUrl(),
                quantity,
                unitPrice,
                unitPrice.multiply(BigDecimal.valueOf(quantity)),
                variant.getQuantityInStock()
        );
    }
}
