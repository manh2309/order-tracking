package com.xxxx.ordertracking.repository;

import com.xxxx.ordertracking.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    @EntityGraph(attributePaths = "role")
    Optional<Account> findByUsername(String username);
}
