package com.amazonscale.wishlists.entity;

import com.amazonscale.product.entity.Product;
import com.amazonscale.wishlists.enums.WishlistPriority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class WishlistItemTest {

    @Test
    @DisplayName("Should build WishlistItem entity using Builder and verify default priority and getters/setters")
    void shouldBuildWishlistItemAndVerifyGettersSetters() {
        // Arrange
        Wishlist wishlist = Wishlist.builder().id(1L).build();
        Product product = Product.builder().id(10L).build();

        // Act
        WishlistItem item = WishlistItem.builder()
                .id(100L)
                .wishlist(wishlist)
                .product(product)
                .note("Gift idea")
                .build();

        // Assert
        assertThat(item.getId()).isEqualTo(100L);
        assertThat(item.getWishlist()).isEqualTo(wishlist);
        assertThat(item.getProduct()).isEqualTo(product);
        assertThat(item.getNote()).isEqualTo("Gift idea");
        assertThat(item.getPriority()).isEqualTo(WishlistPriority.MEDIUM); // Default
    }

    @Test
    @DisplayName("Should populate createdAt and updatedAt on @PrePersist (prePersist) and @PreUpdate (preUpdate)")
    void shouldPopulateTimestampsOnPrePersistAndPreUpdate() {
        // Arrange
        WishlistItem item = new WishlistItem();

        // Act - Simulating PrePersist
        item.prePersist();

        // Assert
        assertThat(item.getCreatedAt()).isNotNull();
        assertThat(item.getUpdatedAt()).isNotNull();

        LocalDateTime initialUpdatedAt = item.getUpdatedAt();

        // Act - Simulating PreUpdate
        item.preUpdate();

        // Assert
        assertThat(item.getUpdatedAt()).isNotNull();
        assertThat(item.getUpdatedAt()).isAfterOrEqualTo(initialUpdatedAt);
    }
}
