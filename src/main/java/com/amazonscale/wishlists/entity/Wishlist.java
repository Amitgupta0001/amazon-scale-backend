package com.amazonscale.wishlists.entity;

import com.amazonscale.user.entity.User;
import com.amazonscale.wishlists.enums.WishlistType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "items"})
@Entity
@Table(
        name = "wishlists",
        indexes = {
                @Index(name = "idx_wishlist_user", columnList = "user_id"),
                @Index(name = "idx_wishlist_type", columnList = "type")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_wishlist_name",
                        columnNames = {"user_id", "name"}
                )
        }
)
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Owner of the wishlist
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Wishlist name
     * Examples:
     * Electronics
     * Birthday Gifts
     * Gaming Setup
     */
    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    // DEFAULT or CUSTOM

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WishlistType type;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDefault = false;


    @Builder.Default
    @OneToMany(
            mappedBy = "wishlist",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<WishlistItem> items = new ArrayList<>();

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