package com.amazonscale.cart.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CartItemNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {
        CartItemNotFoundException ex = new CartItemNotFoundException(100L);
        assertThat(ex.getMessage()).contains("100");
    }
}
