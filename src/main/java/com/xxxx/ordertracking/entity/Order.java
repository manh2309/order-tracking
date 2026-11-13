package com.xxxx.ordertracking.entity;

import com.xxxx.ordertracking.converter.OrderStatusConverter;
import com.xxxx.ordertracking.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "orders", uniqueConstraints = {
        @UniqueConstraint(name = "uq_orders_checkout_request", columnNames = {"account_id", "checkout_request_id"})
})
public class Order extends BaseEntity {
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "checkout_request_id", nullable = false, length = 100)
    private String checkoutRequestId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier_id")
    private Carrier carrier;

    @Column(name = "checkout_request_fingerprint", length = 64)
    private String checkoutRequestFingerprint;

    @Convert(converter = OrderStatusConverter.class)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;

    @Column(name = "currency", nullable = false, length = 3, columnDefinition = "CHAR(3)")
    private String currency = "VND";

    @Column(name = "subtotal", nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "discount_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "shipping_fee", nullable = false, precision = 19, scale = 2)
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "grand_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal grandTotal;

    @Column(name = "recipient_name", nullable = false, length = 150)
    private String recipientName;

    @Column(name = "recipient_phone", nullable = false, length = 20)
    private String recipientPhone;

    @Column(name = "province_code", nullable = false, length = 30)
    private String provinceCode;

    @Column(name = "ward_code", length = 30)
    private String wardCode;

    @Column(name = "address_line", nullable = false, length = 500)
    private String addressLine;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "payment_expires_at")
    private LocalDateTime paymentExpiresAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;
}
