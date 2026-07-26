package com.amazonscale.payment.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentStatusTest {

    @Test
    void testEnumValues() {
        assertThat(PaymentStatus.valueOf("PENDING")).isEqualTo(PaymentStatus.PENDING);
        assertThat(PaymentStatus.valueOf("SUCCESS")).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(PaymentStatus.valueOf("FAILED")).isEqualTo(PaymentStatus.FAILED);
        assertThat(PaymentStatus.valueOf("REFUNDED")).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(PaymentStatus.values()).contains(
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
}
