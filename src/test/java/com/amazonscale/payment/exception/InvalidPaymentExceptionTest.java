package com.amazonscale.payment.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidPaymentExceptionTest {

    @Test
    void testMessage() {
        InvalidPaymentException ex = new InvalidPaymentException("Invalid payment state");
        assertThat(ex.getMessage()).isEqualTo("Invalid payment state");
    }
}
