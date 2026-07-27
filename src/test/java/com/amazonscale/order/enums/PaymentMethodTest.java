package com.amazonscale.order.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentMethodTest {

    @Test
    @DisplayName("Should contain all expected payment method enum values")
    void shouldContainExpectedEnumValues() {
        // Arrange & Act
        PaymentMethod[] values = PaymentMethod.values();

        // Assert
        assertThat(values).containsExactlyInAnyOrder(
                PaymentMethod.COD,
                PaymentMethod.UPI,
                PaymentMethod.CREDIT_CARD,
                PaymentMethod.DEBIT_CARD,
                PaymentMethod.NET_BANKING
        );
    }

    @Test
    @DisplayName("Should valueOf resolve string correctly")
    void shouldResolveValueOfCorrectly() {
        // Act & Assert
        assertThat(PaymentMethod.valueOf("COD")).isEqualTo(PaymentMethod.COD);
        assertThat(PaymentMethod.valueOf("UPI")).isEqualTo(PaymentMethod.UPI);
        assertThat(PaymentMethod.valueOf("CREDIT_CARD")).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(PaymentMethod.valueOf("DEBIT_CARD")).isEqualTo(PaymentMethod.DEBIT_CARD);
        assertThat(PaymentMethod.valueOf("NET_BANKING")).isEqualTo(PaymentMethod.NET_BANKING);
    }
}
