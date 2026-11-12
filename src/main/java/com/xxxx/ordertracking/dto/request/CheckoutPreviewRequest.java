package com.xxxx.ordertracking.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CheckoutPreviewRequest(@NotBlank(message = "Vui lòng nhập mã địa chỉ")
                                     String addressId,
                                     @NotBlank(message = "Vui lòng nhập hãng vận chuyển")
                                     String carrierId) {}
