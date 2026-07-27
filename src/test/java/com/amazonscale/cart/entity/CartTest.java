package com.amazonscale.cart.entity;

import com.amazonscale.user.entity.User;
import com.amazonscale.user.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CartTest {

    @Test
    @DisplayName("Should build Cart entity using Builder and verify getters/setters")
    void shouldBuildCartAndVerifyGettersSetters() {
        // Arrange
        User user = User.builder()
                .id(5L)
                .firstName("Cart")
                .lastName("User")
                .email("cartuser@example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        // Act
        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .build();

        // Assert
        assertThat(cart.getId()).isEqualTo(1L);
        assertThat(cart.getUser()).isEqualTo(user);
        assertThat(cart.getCartItems()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Should populate timestamps automatically on @PrePersist and @PreUpdate")
    void shouldPopulateTimestampsOnPrePersistAndPreUpdate() {
        // Arrange
        Cart cart = new Cart();

        // Act - Simulating PrePersist
        cart.prePersist();

        // Assert
        assertThat(cart.getCreatedAt()).isNotNull();
        assertThat(cart.getUpdatedAt()).isNotNull();

        LocalDateTime initialUpdatedAt = cart.getUpdatedAt();

        // Act - Simulating PreUpdate
        cart.preUpdate();

        // Assert
        assertThat(cart.getUpdatedAt()).isNotNull();
        assertThat(cart.getUpdatedAt()).isAfterOrEqualTo(initialUpdatedAt);
    }
}
