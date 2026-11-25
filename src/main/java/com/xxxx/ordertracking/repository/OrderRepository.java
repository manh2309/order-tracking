package com.xxxx.ordertracking.repository;

import com.xxxx.ordertracking.dto.response.OrderSummary;
import com.xxxx.ordertracking.entity.Order;
import com.xxxx.ordertracking.enums.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Optional<Order> findByAccount_IdAndCheckoutRequestId(String accountId, String checkoutRequestId);


    String SUMMARY = """
            select new com.xxxx.ordertracking.dto.response.OrderSummary(
                o.id, o.code, o.createdAt, o.status, o.subtotal, o.discountAmount,
                o.shippingFee, o.grandTotal, o.currency)
            """;
    String FILTER = " from Order o where o.deleted = 0 and o.account.id = :accountId "
            + "and (:status is null or o.status = :status) ";

    @Query(value = SUMMARY + FILTER + "order by o.createdAt desc, o.id desc",
           countQuery = "select count(o)" + FILTER)
    Page<OrderSummary> history(String accountId, OrderStatus status, Pageable pageable);

    @Query(SUMMARY + FILTER + "and o.id > :afterId order by o.id")
    List<OrderSummary> exportBatch(String accountId, OrderStatus status, String afterId, Pageable pageable);

    Optional<Order> findByIdAndAccount_IdAndDeleted(String id, String accountId, Byte deleted);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.id = :id and o.account.id = :accountId and o.deleted = 0")
    Optional<Order> lockCustomerOrder(String accountId, String id);

    @Modifying
    @Query(value = """
            UPDATE orders SET status = :confirmed, modified_date = :now
            WHERE id = :id AND status = :pending AND deleted = 0
            """, nativeQuery = true)
    int confirmOrder(@Param("id") String id, @Param("pending") byte pending,
                     @Param("confirmed") byte confirmed, @Param("now") LocalDateTime now);

    @Query(SUMMARY + " from Order o where o.id = :id and o.deleted = 0")
    Optional<OrderSummary> activeSummary(String id);
}
