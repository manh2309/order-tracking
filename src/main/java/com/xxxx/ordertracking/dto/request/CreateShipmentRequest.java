package com.xxxx.ordertracking.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateShipmentRequest(@NotBlank String shipperId) {
}
