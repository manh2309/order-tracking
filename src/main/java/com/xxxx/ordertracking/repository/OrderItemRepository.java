package com.xxxx.ordertracking.repository;

import com.xxxx.ordertracking.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {}
