package com.amazonscale.wishlists.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WishlistItemNotFoundExceptionTest {

    @Test
    @DisplayName("Should create WishlistItemNotFoundException with custom message")
    void shouldCreateWishlistItemNotFoundExceptionWithCustomMessage() {
        // Arrange
        String message = "Product not found in wishlist.";

        // Act
        WishlistItemNotFoundException exception = new WishlistItemNotFoundException(message);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
