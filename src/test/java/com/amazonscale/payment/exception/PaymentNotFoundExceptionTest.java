package com.amazonscale.payment.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentNotFoundExceptionTest {

    @Test
    void testMessage() {
        PaymentNotFoundException ex = new PaymentNotFoundException("Payment not found");
        assertThat(ex.getMessage()).isEqualTo("Payment not found");
    }
}
