package com.xxxx.ordertracking.common.response;

import java.util.List;

public record ApiResponse<T>(
        boolean success, String code, String message, T data, List<FieldViolation> errors) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "OK", "Success", data, List.of());
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, "CREATED", "Created", data, List.of());
    }

    public static ApiResponse<Void> error(String code, String message, List<FieldViolation> errors) {
        return new ApiResponse<>(false, code, message, null, List.copyOf(errors));
    }

    public record FieldViolation(String field, String message) {}
}
