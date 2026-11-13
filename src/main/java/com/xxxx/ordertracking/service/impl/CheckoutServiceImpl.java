package com.xxxx.ordertracking.service.impl;

import com.xxxx.ordertracking.dto.request.CheckoutPreviewRequest;
import com.xxxx.ordertracking.dto.response.CheckoutPreviewResponse;
import com.xxxx.ordertracking.service.CheckoutService;
import com.xxxx.ordertracking.service.CheckoutPricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {
    private final CheckoutPricingService checkoutPricingService;

    @Override
    @Transactional(readOnly = true)
    public CheckoutPreviewResponse findCheckoutPreviewById(String accountId, CheckoutPreviewRequest request) {
        return checkoutPricingService.calculate(accountId, request.addressId(), request.carrierId()).summary();
    }
}
