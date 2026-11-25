package com.xxxx.ordertracking.repository;

import com.xxxx.ordertracking.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, String> {
    boolean existsByOrderId(String orderId);
}
