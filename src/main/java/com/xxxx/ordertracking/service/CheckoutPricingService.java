package com.xxxx.ordertracking.service;

import com.xxxx.ordertracking.dto.response.CheckoutPreviewResponse;
import com.xxxx.ordertracking.entity.AccountAddress;
import com.xxxx.ordertracking.entity.CartItem;

import java.util.List;

public interface CheckoutPricingService {
    CheckoutQuote calculate(String accountId, String addressId, String carrierId);

    record CheckoutQuote(AccountAddress address, List<CartItem> items, CheckoutPreviewResponse summary) {}
}
