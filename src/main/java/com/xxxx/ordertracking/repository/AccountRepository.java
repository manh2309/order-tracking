package com.xxxx.ordertracking.repository;

import com.xxxx.ordertracking.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    @Query("""
            select a from Account a where a.id = :id and a.deleted = 0
            and a.status = 1 and a.role.code = 'SHIPPER'
            """)
    Optional<Account> findActiveShipper(String id);

    @EntityGraph(attributePaths = "role")
    Optional<Account> findByUsername(String username);
}
