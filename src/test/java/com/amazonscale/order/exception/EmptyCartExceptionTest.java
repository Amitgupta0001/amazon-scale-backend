package com.amazonscale.order.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmptyCartExceptionTest {

    @Test
    @DisplayName("Should create EmptyCartException with custom message")
    void shouldCreateEmptyCartExceptionWithCustomMessage() {
        // Arrange
        String message = "Cart is empty";

        // Act
        EmptyCartException exception = new EmptyCartException(message);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
