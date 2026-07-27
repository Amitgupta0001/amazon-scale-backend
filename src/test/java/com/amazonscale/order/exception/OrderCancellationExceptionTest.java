package com.amazonscale.order.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderCancellationExceptionTest {

    @Test
    @DisplayName("Should create OrderCancellationException with custom message")
    void shouldCreateOrderCancellationExceptionWithCustomMessage() {
        // Arrange
        String message = "Cannot cancel order in current state";

        // Act
        OrderCancellationException exception = new OrderCancellationException(message);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
