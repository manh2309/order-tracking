package com.xxxx.ordertracking.repository;

import com.xxxx.ordertracking.entity.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {
    @Query("select ci from CartItem ci JOIN Cart c on c.id = ci.cart.id " +
            "and c.id =:cartId ")
    Page<CartItem> findByCartId(@Param("cartId") String cartId, Pageable pageable);

    @Query("select ci From CartItem ci join ci.cart c " +
            "join fetch ci.productVariant v " +
            "join fetch v.product " +
            "where ci.id = :itemId " +
            "and c.account.id = :accountId " +
            "and ci.deleted = 0 " +
            "and c.deleted = 0")
    Optional<CartItem> findActiveItem(@Param("itemId") String itemId,
                                      @Param("accountId") String accountId);

    @Query("select ci from CartItem ci " +
            "join ci.cart c " +
            "join fetch ci.productVariant v " +
            "join fetch v.product " +
            "where c.account.id = :accountId " +
            "and c.deleted = 0 " +
            "and ci.deleted = 0")
    List<CartItem> findActiveItemsByAccountId(@Param("accountId") String accountId);
}
