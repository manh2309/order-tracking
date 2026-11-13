package com.xxxx.ordertracking.enums;

public enum ShipmentStatus {
    PICKING(1),
    SHIPPING(2),
    DELIVERED(3),
    FAILED(4),
    REATTEMPT(5),
    RETURNING(6),
    RETURNED(7);

    private final byte code;

    ShipmentStatus(int code) {
        this.code = (byte) code;
    }

    public byte getCode() {
        return code;
    }

    public static ShipmentStatus fromCode(byte code) {
        for (ShipmentStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Mã trạng thái lô hàng không hợp lệ: " + code);
    }
}
