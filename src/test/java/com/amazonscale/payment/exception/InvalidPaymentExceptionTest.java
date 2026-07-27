package com.amazonscale.payment.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidPaymentExceptionTest {

    @Test
    @DisplayName("Should create InvalidPaymentException with custom message")
    void shouldCreateInvalidPaymentExceptionWithCustomMessage() {
        // Arrange
        String message = "Payment method not supported";

        // Act
        InvalidPaymentException exception = new InvalidPaymentException(message);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
