package com.amazonscale.wishlists.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WishlistNotFoundExceptionTest {

    @Test
    @DisplayName("Should create WishlistNotFoundException with custom message")
    void shouldCreateWishlistNotFoundExceptionWithCustomMessage() {
        // Arrange
        String message = "Wishlist not found.";

        // Act
        WishlistNotFoundException exception = new WishlistNotFoundException(message);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
