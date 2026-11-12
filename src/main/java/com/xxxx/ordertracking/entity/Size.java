package com.xxxx.ordertracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sizes")
public class Size extends BaseEntity {
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "system_code", length = 50)
    private String systemCode;
}
