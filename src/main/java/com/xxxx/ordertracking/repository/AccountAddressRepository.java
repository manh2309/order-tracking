package com.xxxx.ordertracking.repository;

import com.xxxx.ordertracking.entity.AccountAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountAddressRepository extends JpaRepository<AccountAddress, String> {
    @Query("select ac from AccountAddress ac where ac.account.id =:accountId and ac.id =:addressId and ac.deleted = 0")
    Optional<AccountAddress> findActiveByIdAndAccountId(@Param("accountId") String accountId, @Param("addressId") String addressId);
}
