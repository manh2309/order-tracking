package com.xxxx.ordertracking.repository;

import com.xxxx.ordertracking.entity.ShipmentEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentEventRepository extends JpaRepository<ShipmentEvent, String> {
}
