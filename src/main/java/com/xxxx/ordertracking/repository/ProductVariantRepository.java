package com.xxxx.ordertracking.repository;

import com.xxxx.ordertracking.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {
    @Modifying
    @Query(value = """
            UPDATE product_variants v
            JOIN products p ON p.id = v.product_id
            SET v.quantity_in_stock = v.quantity_in_stock - :quantity
            WHERE v.id = :id AND v.quantity_in_stock >= :quantity
              AND v.deleted = 0 AND v.status = 1
              AND p.deleted = 0 AND p.status = 1
            """, nativeQuery = true)
    int reserveStock(@Param("id") String id, @Param("quantity") int quantity);
}
