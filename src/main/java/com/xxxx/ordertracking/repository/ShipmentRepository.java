package com.xxxx.ordertracking.repository;

import com.xxxx.ordertracking.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface ShipmentRepository extends JpaRepository<Shipment, String> {
    boolean existsByOrderId(String orderId);

    boolean existsByIdAndShipper_IdAndDeleted(String id, String shipperId, Byte deleted);

    @Modifying
    @Query(value = """
            UPDATE shipments
            SET status = :shipping, shipped_at = :now, modified_date = :now
            WHERE id = :shipmentId AND shipper_id = :accountId
              AND deleted = 0 AND status = :picking
            """, nativeQuery = true)
    int startShipping(String shipmentId, String accountId, byte picking, byte shipping, LocalDateTime now);
}
