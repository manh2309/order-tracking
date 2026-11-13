package com.xxxx.ordertracking.repository;

import com.xxxx.ordertracking.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Optional<Order> findByAccount_IdAndCheckoutRequestId(
            String accountId,
            String checkoutRequestId
    );
}
