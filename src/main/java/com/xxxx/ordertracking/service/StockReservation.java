package com.xxxx.ordertracking.service;

import com.xxxx.ordertracking.entity.CartItem;
import com.xxxx.ordertracking.entity.ProductVariant;

import java.util.List;

public interface StockReservation {
    void prepare(List<CartItem> items);
    void reserve(ProductVariant variant, int quantity);
}
