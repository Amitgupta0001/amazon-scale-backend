package com.amazonscale.product.entity;

import jakarta.persistence.*;  // contains Entity,Table,Id,GeneratedValue and Column basically everything related to JPA
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "products")
public class Product {

    @Id  // this is the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false , length = 200)  // not null value inside the database meaning no product should exist without name
    private String name; // it creates a VARCHAR(200) instead of TEXT

    @Column(columnDefinition = "TEXT")
    private String description; // here hibernate creates a TEXT

    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal price; // BigDecimal stores values precisely and scale = 2 means two digit after decimal

    @Column(nullable = false)
    private Integer stock; // Available quantity

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate(){
        updatedAt = LocalDateTime.now();
    }

}
