package com.amazonscale.payment.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentGatewayTest {

    @Test
    @DisplayName("Should contain expected payment gateway enum values")
    void shouldContainExpectedEnumValues() {
        // Arrange & Act
        PaymentGateway[] values = PaymentGateway.values();

        // Assert
        assertThat(values).containsExactlyInAnyOrder(
                PaymentGateway.STRIPE,
                PaymentGateway.RAZORPAY,
                PaymentGateway.PHONEPAY,
                PaymentGateway.BHARATPAY,
                PaymentGateway.PAYPAL,
                PaymentGateway.COD
        );
    }

    @Test
    @DisplayName("Should valueOf resolve string correctly")
    void shouldResolveValueOfCorrectly() {
        // Act & Assert
        assertThat(PaymentGateway.valueOf("RAZORPAY")).isEqualTo(PaymentGateway.RAZORPAY);
        assertThat(PaymentGateway.valueOf("STRIPE")).isEqualTo(PaymentGateway.STRIPE);
    }
}
