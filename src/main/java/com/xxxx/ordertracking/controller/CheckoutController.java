package com.xxxx.ordertracking.controller;

import com.xxxx.ordertracking.common.response.ApiResponse;
import com.xxxx.ordertracking.config.AccountPrincipal;
import com.xxxx.ordertracking.dto.request.CheckoutPreviewRequest;
import com.xxxx.ordertracking.dto.response.CheckoutPreviewResponse;
import com.xxxx.ordertracking.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping("/preview")
    public ApiResponse<CheckoutPreviewResponse> preview(
            @Validated @RequestBody CheckoutPreviewRequest request,
            @AuthenticationPrincipal AccountPrincipal principal
    ) {
        return ApiResponse.ok(checkoutService.findCheckoutPreviewById(
                principal.getAccountId(), request
        ));
    }
}
