package com.xxxx.ordertracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "shipping_fees")
public class ShippingFee extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrier_id", nullable = false)
    private Carrier carrier;

    @Column(name = "destination_province_code", nullable = false, length = 30)
    private String destinationProvinceCode;

    @Column(name = "min_weight_grams", nullable = false)
    private Integer minWeightGrams;

    @Column(name = "max_weight_grams", nullable = false)
    private Integer maxWeightGrams;

    @Column(name = "fee", nullable = false, precision = 19, scale = 2)
    private BigDecimal fee;

    @Column(name = "estimated_days", nullable = false)
    private Integer estimatedDays;
}
