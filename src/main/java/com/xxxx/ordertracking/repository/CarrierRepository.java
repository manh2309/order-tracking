package com.xxxx.ordertracking.repository;

import com.xxxx.ordertracking.entity.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, String> {
    boolean existsByIdAndStatusAndDeleted(String id, Long status, Long deleted);
}
