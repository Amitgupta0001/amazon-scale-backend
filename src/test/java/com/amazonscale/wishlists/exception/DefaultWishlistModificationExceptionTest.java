package com.amazonscale.wishlists.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultWishlistModificationExceptionTest {

    @Test
    @DisplayName("Should create DefaultWishlistModificationException with custom message")
    void shouldCreateDefaultWishlistModificationExceptionWithCustomMessage() {
        // Arrange
        String message = "Default wishlists cannot be modified.";

        // Act
        DefaultWishlistModificationException exception = new DefaultWishlistModificationException(message);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
