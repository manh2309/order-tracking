package com.xxxx.ordertracking.service.impl;

import com.xxxx.ordertracking.common.exception.ApiException;
import com.xxxx.ordertracking.common.exception.ErrorCode;
import com.xxxx.ordertracking.entity.CartItem;
import com.xxxx.ordertracking.entity.ProductVariant;
import com.xxxx.ordertracking.repository.ProductVariantRepository;
import com.xxxx.ordertracking.service.StockReservation;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "app.inventory.reservation-mode",
        havingValue = "atomic",
        matchIfMissing = true
)
public class AtomicStockReservation implements StockReservation {
    private final ProductVariantRepository productVariantRepository;

    @Override
    public void prepare(List<CartItem> items) {

    }

    @Override
    public void reserve(ProductVariant variant, int quantity) {
        if (productVariantRepository.reserveStock(variant.getId(), quantity) != 1) {
            throw new ApiException(ErrorCode.INSUFFICIENT_STOCK);
        }
    }
}
