package com.xxxx.ordertracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "account_addresses")
public class AccountAddress extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "recipient_name", nullable = false, length = 150)
    private String recipientName;

    @Column(name = "recipient_phone", nullable = false, length = 20)
    private String recipientPhone;

    @Column(name = "province_code", nullable = false, length = 30)
    private String provinceCode;

    @Column(name = "ward_code", length = 30)
    private String wardCode;

    @Column(name = "address_line", nullable = false, length = 500)
    private String addressLine;
}
