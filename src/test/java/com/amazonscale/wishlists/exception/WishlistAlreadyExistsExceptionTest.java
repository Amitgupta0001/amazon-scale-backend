package com.amazonscale.wishlists.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WishlistAlreadyExistsExceptionTest {

    @Test
    @DisplayName("Should create WishlistAlreadyExistsException with custom message")
    void shouldCreateWishlistAlreadyExistsExceptionWithCustomMessage() {
        // Arrange
        String message = "Wishlist with name 'Tech' already exists.";

        // Act
        WishlistAlreadyExistsException exception = new WishlistAlreadyExistsException(message);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
