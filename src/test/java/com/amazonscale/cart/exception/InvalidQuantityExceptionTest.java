package com.amazonscale.cart.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidQuantityExceptionTest {

    @Test
    void testExceptionMessage() {
        InvalidQuantityException ex = new InvalidQuantityException("Quantity must be > 0");
        assertThat(ex.getMessage()).isEqualTo("Quantity must be > 0");
    }
}
