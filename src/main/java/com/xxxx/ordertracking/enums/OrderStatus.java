package com.xxxx.ordertracking.enums;

public enum OrderStatus {
    PENDING(1),
    AWAITING_PAYMENT(2),
    CONFIRMED(3),
    COMPLETED(4),
    CANCELLED(5);

    private final byte code;

    OrderStatus(int code) {
        this.code = (byte) code;
    }

    public byte getCode() {
        return code;
    }

    public static OrderStatus fromCode(byte code) {
        for (OrderStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Mã trạng thái đơn hàng không hợp lệ: " + code);
    }
}
