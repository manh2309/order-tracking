package com.xxxx.ordertracking.common.exception;

import com.xxxx.ordertracking.common.response.ApiResponse;
import com.xxxx.ordertracking.common.response.ApiResponse.FieldViolation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(ApiResponse.error(ex.getCode(), ex.getMessage(), List.of()));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        ApiResponse<Void> error;
        if (ex instanceof MethodArgumentNotValidException validation) {
            List<FieldViolation> violations = validation.getBindingResult().getAllErrors().stream()
                    .map(item -> new FieldViolation(
                            item instanceof org.springframework.validation.FieldError field
                                    ? field.getField() : item.getObjectName(),
                            Objects.toString(item.getDefaultMessage(), "Invalid value")))
                    .toList();
            error = ApiResponse.error(ErrorCode.VALIDATION_FAILED.name(), ErrorCode.VALIDATION_FAILED.getMessage(), violations);
        } else if (ex instanceof HttpMessageNotReadableException) {
            error = ApiResponse.error(ErrorCode.INVALID_REQUEST_BODY.name(), ErrorCode.INVALID_REQUEST_BODY.getMessage(), List.of());
        } else {
            HttpStatus knownStatus = HttpStatus.resolve(status.value());
            String code = knownStatus == null ? "HTTP_" + status.value() : knownStatus.name();
            String message = status.is5xxServerError() ? "Internal server error"
                    : knownStatus == null ? "Request failed" : knownStatus.getReasonPhrase();
            error = ApiResponse.error(code, message, List.of());
        }
        return super.handleExceptionInternal(ex, error, headers, status, request);
    }
}
