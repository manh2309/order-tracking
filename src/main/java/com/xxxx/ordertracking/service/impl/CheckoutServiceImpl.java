package com.xxxx.ordertracking.service.impl;

import com.xxxx.ordertracking.dto.request.CheckoutPreviewRequest;
import com.xxxx.ordertracking.dto.response.CheckoutPreviewResponse;
import com.xxxx.ordertracking.service.CheckoutService;
import com.xxxx.ordertracking.service.CheckoutPricingService;
import com.xxxx.ordertracking.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {
    private final CheckoutPricingService checkoutPricingService;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional(readOnly = true)
    public CheckoutPreviewResponse findCheckoutPreviewById(String accountId, CheckoutPreviewRequest request) {
        var items = cartItemRepository.findActiveItemsByAccountId(accountId);
        return checkoutPricingService.calculate(
                accountId, request.addressId(), request.carrierId(), items
        ).summary();
    }
}
