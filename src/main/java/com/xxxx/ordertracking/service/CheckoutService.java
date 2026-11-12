package com.xxxx.ordertracking.service;

import com.xxxx.ordertracking.dto.request.CheckoutPreviewRequest;
import com.xxxx.ordertracking.dto.response.CheckoutPreviewResponse;

public interface CheckoutService {
    CheckoutPreviewResponse findCheckoutPreviewById(String accountId, CheckoutPreviewRequest request);
}
