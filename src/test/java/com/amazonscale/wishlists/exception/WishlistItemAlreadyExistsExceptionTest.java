package com.amazonscale.wishlists.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WishlistItemAlreadyExistsExceptionTest {

    @Test
    @DisplayName("Should create WishlistItemAlreadyExistsException with custom message")
    void shouldCreateWishlistItemAlreadyExistsExceptionWithCustomMessage() {
        // Arrange
        String message = "Product already exists in wishlist.";

        // Act
        WishlistItemAlreadyExistsException exception = new WishlistItemAlreadyExistsException(message);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
