package com.amazonscale.cart.entity;

import com.amazonscale.product.entity.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(
        name = "cart_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cart_product",
                        columnNames = {"cart_id", "product_id"}
                )
        },
        indexes = {
                @Index(name = "idx_cart_id", columnList = "cart_id"),
                @Index(name = "idx_product_id", columnList = "product_id")
        }
)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Min(1)
    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false,precision = 10, scale = 2)
    private BigDecimal priceAtAddition;

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
