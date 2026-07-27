package com.amazonscale.payment.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentNotFoundExceptionTest {

    @Test
    @DisplayName("Should create PaymentNotFoundException with custom message")
    void shouldCreatePaymentNotFoundExceptionWithCustomMessage() {
        // Arrange
        String message = "No payment found with id: 10";

        // Act
        PaymentNotFoundException exception = new PaymentNotFoundException(message);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
