package com.amazonscale.order.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidOrderStatusTransitionExceptionTest {

    @Test
    @DisplayName("Should create InvalidOrderStatusTransitionException with custom message")
    void shouldCreateInvalidOrderStatusTransitionExceptionWithCustomMessage() {
        // Arrange
        String message = "Invalid status transition";

        // Act
        InvalidOrderStatusTransitionException exception = new InvalidOrderStatusTransitionException(message);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
