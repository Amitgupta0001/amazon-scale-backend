package com.amazonscale.product.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductUnavailableExceptionTest {

    @Test
    @DisplayName("Should create ProductUnavailableException with formatted message containing product ID")
    void shouldCreateProductUnavailableExceptionWithCorrectMessage() {
        // Arrange & Act
        ProductUnavailableException ex = new ProductUnavailableException(100L);

        // Assert
        assertThat(ex).isNotNull();
        assertThat(ex.getMessage()).isEqualTo("Product 100 is unavailable");
    }
}
