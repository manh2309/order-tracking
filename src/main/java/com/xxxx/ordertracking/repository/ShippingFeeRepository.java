package com.xxxx.ordertracking.repository;

import com.xxxx.ordertracking.entity.ShippingFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingFeeRepository extends JpaRepository<ShippingFee, String> {
    @Query("select f from ShippingFee f " +
            "where f.carrier.id = :carrierId " +
            "and f.destinationProvinceCode = :provinceCode " +
            "and f.minWeightGrams <= :weight " +
            "and f.maxWeightGrams > :weight " +
            "and f.deleted = 0")
    List<ShippingFee> findMatchingFees(
            @Param("carrierId") String carrierId,
            @Param("provinceCode") String provinceCode,
            @Param("weight") long weight
    );
}
