package com.amazonscale.inventory.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InsufficientStockExceptionTest {

    @Test
    @DisplayName("Should create InsufficientStockException with custom message")
    void shouldCreateInsufficientStockExceptionWithCustomMessage() {
        // Arrange
        String message = "Not enough stock available";

        // Act
        InsufficientStockException exception = new InsufficientStockException(message);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}