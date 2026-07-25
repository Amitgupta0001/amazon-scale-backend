package com.amazonscale.order.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmptyCartExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "Cannot place order with an empty cart.";
        EmptyCartException ex = new EmptyCartException(message);
        assertThat(ex.getMessage()).isEqualTo(message);
    }
}
