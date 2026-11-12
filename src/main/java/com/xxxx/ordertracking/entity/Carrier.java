package com.xxxx.ordertracking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "carriers")
public class Carrier extends BaseEntity {
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "status", nullable = false)
    private Byte status = 1;
}
