package com.xxxx.ordertracking.service;

import com.xxxx.ordertracking.common.exception.ApiException;
import com.xxxx.ordertracking.common.exception.ErrorCode;
import com.xxxx.ordertracking.dto.response.*;
import com.xxxx.ordertracking.entity.Order;
import com.xxxx.ordertracking.enums.OrderStatus;
import com.xxxx.ordertracking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class CustomerOrderService {
    private final OrderRepository orders;
    private final OrderItemRepository items;
    private final ProductVariantRepository variants;

    @Transactional(readOnly = true)
    public OrderDetailResponse detail(String accountId, String id) {
        Order order = orders.findByIdAndAccount_IdAndDeleted(id, accountId, (byte) 0)
                .orElseThrow(() -> new ApiException(ErrorCode.ORDER_NOT_FOUND));
        return new OrderDetailResponse(summary(order), order.getRecipientName(), order.getRecipientPhone(),
                order.getAddressLine(), order.getWardCode(), order.getProvinceCode(), order.getPaymentMethod(),
                order.getCancelReason(), items.orderLines(id));
    }

    @Transactional
    public OrderSummary cancel(String accountId, String id, String reason) {
        Order order = orders.lockCustomerOrder(accountId, id)
                .orElseThrow(() -> new ApiException(ErrorCode.ORDER_NOT_FOUND));
        if (order.getStatus() == OrderStatus.CANCELLED) return summary(order);
        if (order.getStatus() != OrderStatus.PENDING || !"COD".equals(order.getPaymentMethod())) {
            throw new ApiException(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
        }
        for (var item : items.findByOrder_IdAndDeletedOrderByProductVariant_IdAsc(id, (byte) 0)) {
            if (variants.restoreStock(item.getProductVariant().getId(), item.getQuantity()) != 1) {
                throw new ApiException(ErrorCode.STOCK_UPDATE_CONFLICT);
            }
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now(ZoneOffset.UTC));
        order.setCancelReason(reason.strip());
        return summary(order);
    }

    private static OrderSummary summary(Order o) {
        return new OrderSummary(o.getId(), o.getCode(), o.getCreatedAt(), o.getStatus(), o.getSubtotal(),
                o.getDiscountAmount(), o.getShippingFee(), o.getGrandTotal(), o.getCurrency());
    }
}
