package com.xxxx.ordertracking.repository;

import com.xxxx.ordertracking.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

    @Query("select c from Cart c where c.account.id =:accountId and c.deleted = 0")
    Optional<Cart> findAllByAccountId(@Param("accountId") String accountId);
}
