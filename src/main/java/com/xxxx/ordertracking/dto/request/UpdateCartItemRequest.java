package com.xxxx.ordertracking.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateCartItemRequest(@NotNull(message = "Vui lòng nhập số lượng")
                                    @Positive(message = "Số lượng phải lớn hơn 0")
                                    Integer quantity) {
}
