package com.amazonscale.product.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductInactiveExceptionTest {

    @Test
    @DisplayName("Should create ProductInactiveException with custom message")
    void shouldCreateProductInactiveExceptionWithCustomMessage() {
        // Arrange
        String message = "Product is inactive: Phone";

        // Act
        ProductInactiveException ex = new ProductInactiveException(message);

        // Assert
        assertThat(ex).isNotNull();
        assertThat(ex.getMessage()).isEqualTo(message);
    }
}
