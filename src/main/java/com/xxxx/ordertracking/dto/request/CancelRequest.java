package com.xxxx.ordertracking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelRequest(@NotBlank @Size(max = 500) String reason) {}
