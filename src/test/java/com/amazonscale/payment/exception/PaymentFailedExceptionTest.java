package com.amazonscale.payment.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentFailedExceptionTest {

    @Test
    void testMessage() {
        PaymentFailedException ex = new PaymentFailedException("Payment processing failed");
        assertThat(ex.getMessage()).isEqualTo("Payment processing failed");
    }
}
