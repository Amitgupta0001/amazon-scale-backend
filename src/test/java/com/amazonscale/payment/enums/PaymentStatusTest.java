package com.amazonscale.payment.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentStatusTest {

    @Test
    @DisplayName("Should contain expected payment status enum values")
    void shouldContainExpectedEnumValues() {
        // Arrange & Act
        PaymentStatus[] values = PaymentStatus.values();

        // Assert
        assertThat(values).containsExactlyInAnyOrder(
                PaymentStatus.PENDING,
                PaymentStatus.PROCESSING,
                PaymentStatus.SUCCESS,
                PaymentStatus.FAILED,
                PaymentStatus.REFUNDED,
                PaymentStatus.CANCELLED,
                PaymentStatus.PAID,
                PaymentStatus.CONFIRMED
        );
    }

    @Test
    @DisplayName("Should valueOf resolve string correctly")
    void shouldResolveValueOfCorrectly() {
        // Act & Assert
        assertThat(PaymentStatus.valueOf("PENDING")).isEqualTo(PaymentStatus.PENDING);
        assertThat(PaymentStatus.valueOf("SUCCESS")).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(PaymentStatus.valueOf("FAILED")).isEqualTo(PaymentStatus.FAILED);
        assertThat(PaymentStatus.valueOf("REFUNDED")).isEqualTo(PaymentStatus.REFUNDED);
    }
}
