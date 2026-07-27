package com.amazonscale.wishlists.entity;

import com.amazonscale.product.entity.Product;
import com.amazonscale.wishlists.enums.WishlistPriority;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"wishlist", "product"})
@Entity
@Table(
        name = "wishlist_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_wishlist_product",
                        columnNames = {"wishlist_id", "product_id"}
                )
        },
        indexes = {
                @Index(name = "idx_wishlist_item_wishlist", columnList = "wishlist_id"),
                @Index(name = "idx_wishlist_item_product", columnList = "product_id")
        }
)
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "wishlist_id",
            nullable = false
    )
    private Wishlist wishlist;

    // Saved product.

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    private Product product;

    // Optional note added by the user.
    @Column(length = 500)
    private String note;

    // Priority inside the wishlist.
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private WishlistPriority priority = WishlistPriority.MEDIUM;

    @NotNull
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}