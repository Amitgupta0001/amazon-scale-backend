package com.amazonscale.product.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductInactiveExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "Product is inactive: Phone";
        ProductInactiveException ex = new ProductInactiveException(message);
        assertThat(ex.getMessage()).isEqualTo(message);
    }
}
