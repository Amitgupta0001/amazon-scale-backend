package com.amazonscale.wishlists.entity;

import com.amazonscale.user.entity.User;
import com.amazonscale.user.enums.Role;
import com.amazonscale.wishlists.enums.WishlistType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class WishlistTest {

    @Test
    @DisplayName("Should build Wishlist entity using Builder and verify defaults and getters/setters")
    void shouldBuildWishlistAndVerifyGettersSetters() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .firstName("Wish")
                .lastName("User")
                .email("wishuser@example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        // Act
        Wishlist wishlist = Wishlist.builder()
                .id(10L)
                .user(user)
                .name("Books")
                .description("Books to read")
                .type(WishlistType.CUSTOM)
                .build();

        // Assert
        assertThat(wishlist.getId()).isEqualTo(10L);
        assertThat(wishlist.getUser()).isEqualTo(user);
        assertThat(wishlist.getName()).isEqualTo("Books");
        assertThat(wishlist.getDescription()).isEqualTo("Books to read");
        assertThat(wishlist.getType()).isEqualTo(WishlistType.CUSTOM);
        assertThat(wishlist.getIsDefault()).isFalse(); // Default
        assertThat(wishlist.getItems()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Should populate createdAt and updatedAt on @PrePersist (prePersist) and @PreUpdate (preUpdate)")
    void shouldPopulateTimestampsOnPrePersistAndPreUpdate() {
        // Arrange
        Wishlist wishlist = new Wishlist();

        // Act - Simulating PrePersist
        wishlist.prePersist();

        // Assert
        assertThat(wishlist.getCreatedAt()).isNotNull();
        assertThat(wishlist.getUpdatedAt()).isNotNull();

        LocalDateTime initialUpdatedAt = wishlist.getUpdatedAt();

        // Act - Simulating PreUpdate
        wishlist.preUpdate();

        // Assert
        assertThat(wishlist.getUpdatedAt()).isNotNull();
        assertThat(wishlist.getUpdatedAt()).isAfterOrEqualTo(initialUpdatedAt);
    }
}
