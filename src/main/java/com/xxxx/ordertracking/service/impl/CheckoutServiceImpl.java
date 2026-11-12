package com.xxxx.ordertracking.service.impl;

import com.xxxx.ordertracking.common.exception.ApiException;
import com.xxxx.ordertracking.common.exception.ErrorCode;
import com.xxxx.ordertracking.dto.request.CheckoutPreviewRequest;
import com.xxxx.ordertracking.dto.response.CartStockIssue;
import com.xxxx.ordertracking.dto.response.CheckoutPreviewResponse;
import com.xxxx.ordertracking.entity.AccountAddress;
import com.xxxx.ordertracking.repository.AccountAddressRepository;
import com.xxxx.ordertracking.repository.CarrierRepository;
import com.xxxx.ordertracking.repository.CartItemRepository;
import com.xxxx.ordertracking.repository.ShippingFeeRepository;
import com.xxxx.ordertracking.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {
    private final AccountAddressRepository accountAddressRepository;
    private final CarrierRepository carrierRepository;
    private final CartItemRepository  cartItemRepository;
    private final ShippingFeeRepository shippingFeeRepository;
    @Override
    public CheckoutPreviewResponse findCheckoutPreviewById(String accountId, CheckoutPreviewRequest request) {
        AccountAddress address = accountAddressRepository
                .findActiveByIdAndAccountId(accountId, request.addressId())
                .orElseThrow(() -> new ApiException(ErrorCode.ADDRESS_NOT_FOUND));

        boolean carrierAvailable = carrierRepository.existsByIdAndStatusAndDeleted(
                request.carrierId(), 1L, 0L
        );

        if (!carrierAvailable) {
            throw new ApiException(ErrorCode.CARRIER_NOT_AVAILABLE);
        }

        String provinceCode = address.getProvinceCode();
        var items = cartItemRepository.findActiveItemsByAccountId(accountId);
        if (items.isEmpty()) {
            throw new ApiException(ErrorCode.CART_EMPTY);
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        long totalWeightGrams = 0;
        List<CartStockIssue> issues = new ArrayList<>();

        for (var item : items) {
            var variant = item.getProductVariant();
            var product = variant.getProduct();
            int quantity = item.getQuantity();
            int available = variant.getQuantityInStock();

            subtotal = subtotal.add(
                    variant.getPrice().multiply(BigDecimal.valueOf(quantity))
            );
            totalWeightGrams += (long) variant.getWeightGrams() * quantity;

            if (variant.getDeleted() != 0 || variant.getStatus() != 1
                    || product.getDeleted() != 0 || product.getStatus() != 1) {
                issues.add(new CartStockIssue(
                        item.getId(), quantity, available, "PRODUCT_NOT_AVAILABLE"
                ));
            } else if (quantity > available) {
                issues.add(new CartStockIssue(
                        item.getId(), quantity, available, "INSUFFICIENT_STOCK"
                ));
            }
        }

        var fees = shippingFeeRepository.findMatchingFees(
                request.carrierId(), provinceCode, totalWeightGrams
        );
        if (fees.size() != 1) {
            throw new ApiException(ErrorCode.SHIPPING_FEE_UNAVAILABLE);
        }

        BigDecimal shippingFee = fees.getFirst().getFee();
        BigDecimal discountAmount = subtotal.multiply(new BigDecimal("0.05"))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal grandTotal = subtotal.subtract(discountAmount).add(shippingFee);

        return new CheckoutPreviewResponse(
                subtotal, discountAmount, shippingFee, grandTotal,
                "VND", issues.isEmpty(), issues
        );
    }
}
