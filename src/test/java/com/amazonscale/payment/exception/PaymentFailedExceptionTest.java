package com.amazonscale.payment.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentFailedExceptionTest {

    @Test
    @DisplayName("Should create PaymentFailedException with custom message")
    void shouldCreatePaymentFailedExceptionWithCustomMessage() {
        // Arrange
        String message = "Payment gateway declined transaction";

        // Act
        PaymentFailedException exception = new PaymentFailedException(message);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
