package com.amazonscale.product.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductUnavailableExceptionTest {

    @Test
    void testExceptionMessage() {
        ProductUnavailableException ex = new ProductUnavailableException(100L);
        assertThat(ex.getMessage()).contains("100");
    }
}
