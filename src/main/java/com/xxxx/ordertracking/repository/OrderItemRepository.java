package com.xxxx.ordertracking.repository;

import com.xxxx.ordertracking.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import com.xxxx.ordertracking.dto.response.OrderLineResponse;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    @Query("""
            select new com.xxxx.ordertracking.dto.response.OrderLineResponse(i.skuSnapshot,
                i.productNameSnapshot, i.variantDescriptionSnapshot, i.quantity, i.unitPrice, i.lineTotal)
            from OrderItem i where i.order.id = :id and i.deleted = 0 order by i.skuSnapshot
            """)
    List<OrderLineResponse> orderLines(String id);

    List<OrderItem> findByOrder_IdAndDeletedOrderByProductVariant_IdAsc(String id, Byte deleted);
}
