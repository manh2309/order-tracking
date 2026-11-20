package com.xxxx.ordertracking.service.impl;

import com.xxxx.ordertracking.common.exception.ApiException;
import com.xxxx.ordertracking.common.exception.ErrorCode;
import com.xxxx.ordertracking.entity.CartItem;
import com.xxxx.ordertracking.entity.ProductVariant;
import com.xxxx.ordertracking.service.StockReservation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@ConditionalOnProperty(
        name = "app.inventory.reservation-mode",
        havingValue = "pessimistic"
)
public class PessimisticStockReservation implements StockReservation {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void prepare(List<CartItem> items) {
        List<ProductVariant> variants = items.stream()
                .map(CartItem::getProductVariant)
                .sorted(Comparator.comparing(ProductVariant::getId))
                .toList();

        for (ProductVariant variant : variants) {
            entityManager.refresh(variant, LockModeType.PESSIMISTIC_WRITE);
        }
    }

    @Override
    public void reserve(ProductVariant variant, int quantity) {
        if(variant.getDeleted() != 0 || variant.getStatus() != 1) {
            throw new ApiException(ErrorCode.PRODUCT_NOT_AVAILABLE);
        }

        int available = variant.getQuantityInStock();
        if(available < quantity) {
            throw new ApiException(ErrorCode.INSUFFICIENT_STOCK);
        }

        variant.setQuantityInStock(available - quantity);
    }
}
