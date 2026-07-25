package com.amazonscale.cart.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CartNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {
        CartNotFoundException ex = new CartNotFoundException(50L);
        assertThat(ex.getMessage()).contains("50");
    }
}
