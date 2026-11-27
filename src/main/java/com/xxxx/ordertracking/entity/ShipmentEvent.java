package com.xxxx.ordertracking.entity;

import com.xxxx.ordertracking.converter.ShipmentStatusConverter;
import com.xxxx.ordertracking.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipment_events")
@Getter
@Setter
public class ShipmentEvent extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Convert(converter = ShipmentStatusConverter.class)
    @Column(name = "from_status", nullable = false)
    private ShipmentStatus fromStatus;

    @Convert(converter = ShipmentStatusConverter.class)
    @Column(name = "to_status", nullable = false)
    private ShipmentStatus toStatus;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;
}
