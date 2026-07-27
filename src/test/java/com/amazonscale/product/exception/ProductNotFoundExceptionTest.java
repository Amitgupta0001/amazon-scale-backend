package com.amazonscale.product.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductNotFoundExceptionTest {

    @Test
    @DisplayName("Should create ProductNotFoundException with formatted message containing ID")
    void shouldCreateProductNotFoundExceptionWithCorrectMessage() {
        // Arrange & Act
        ProductNotFoundException exception = new ProductNotFoundException(42L);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Product not found with id :42");
    }
}