package com.amazonscale.inventory.entity;


import com.amazonscale.product.entity.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;


import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "inventory",
        indexes = {
                @Index(name = "idx_inventory_product", columnList = "product_id")
        }
)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY ,optional = false)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @NotNull
    @Column(nullable = false)
    @PositiveOrZero
    private Integer quantity;

    @NotNull
    @Builder.Default
    @Column(nullable = false)
    @PositiveOrZero
    private Integer reservedQuantity = 0;


    @NotBlank
    @Size(max = 200)
    @Column(nullable = false,length = 200)
    private String warehouseLocation;

    @NotNull
    @Builder.Default
    @Column(nullable = false)
    @PositiveOrZero
    private Integer lowStockThreshold = 10;

    @Transient
    public Integer getAvailableQuantity() {
        return Math.max(0,
                (quantity == null ? 0 : quantity)
                        - (reservedQuantity == null ? 0 : reservedQuantity));
    }

    @NotNull
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @NotNull
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
