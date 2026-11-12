package com.xxxx.ordertracking.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Dữ liệu gửi lên không hợp lệ"),
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, "Nội dung yêu cầu bị thiếu hoặc không hợp lệ"),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản"),
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Tên đăng nhập đã được sử dụng"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email đã được sử dụng"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"),
    PRODUCT_VARIANT_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy biến thể sản phẩm"),
    PRODUCT_NOT_AVAILABLE(HttpStatus.CONFLICT, "Sản phẩm hiện không được bán"),
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng"),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy mặt hàng trong giỏ"),
    CART_EMPTY(HttpStatus.CONFLICT, "Giỏ hàng trống, không thể đặt hàng"),
    INSUFFICIENT_STOCK(HttpStatus.CONFLICT, "Số lượng yêu cầu vượt quá tồn kho"),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"),
    INVALID_ORDER_STATUS_TRANSITION(HttpStatus.CONFLICT, "Không thể chuyển đơn hàng sang trạng thái được yêu cầu"),
    CHECKOUT_REQUEST_CONFLICT(HttpStatus.CONFLICT, "Mã yêu cầu đặt hàng đã được dùng cho nội dung khác"),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy giao dịch thanh toán"),
    SHIPMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy vận đơn"),
    INVALID_SHIPMENT_STATUS_TRANSITION(HttpStatus.CONFLICT, "Không thể chuyển vận đơn sang trạng thái được yêu cầu"),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ giao hàng"),
    SHIPPING_FEE_UNAVAILABLE(HttpStatus.CONFLICT, "Chưa xác định được phí vận chuyển cho giỏ hàng này"),
    CARRIER_NOT_AVAILABLE(HttpStatus.CONFLICT, "Đơn vị vận chuyển hiện không khả dụng");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
