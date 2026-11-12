package com.xxxx.ordertracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "shipments", uniqueConstraints = {
        @UniqueConstraint(name = "uq_shipments_tracking", columnNames = {"carrier_id", "tracking_number"})
})
public class Shipment extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrier_id", nullable = false)
    private Carrier carrier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_id")
    private Account shipper;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "total_weight_grams", nullable = false)
    private Integer totalWeightGrams;

    @Column(name = "estimated_delivery_at")
    private LocalDateTime estimatedDeliveryAt;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount = 0;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "PICKING";
}
