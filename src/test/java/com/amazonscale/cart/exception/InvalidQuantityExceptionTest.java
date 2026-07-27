package com.amazonscale.cart.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidQuantityExceptionTest {

    @Test
    @DisplayName("Should create InvalidQuantityException with custom message")
    void shouldCreateInvalidQuantityExceptionWithCustomMessage() {
        // Arrange
        String message = "Quantity must be greater than zero";

        // Act
        InvalidQuantityException exception = new InvalidQuantityException(message);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
