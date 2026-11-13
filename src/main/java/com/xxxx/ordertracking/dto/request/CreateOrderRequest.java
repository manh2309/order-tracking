package com.xxxx.ordertracking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(
        @NotBlank @Size(max = 36) String addressId,
        @NotBlank @Size(max = 36) String carrierId,
        @NotBlank @Pattern(regexp = "COD", message = "Hiện chỉ hỗ trợ thanh toán COD") String paymentMethod,
        @NotBlank @Size(max = 100) String checkoutRequestId
) {}
