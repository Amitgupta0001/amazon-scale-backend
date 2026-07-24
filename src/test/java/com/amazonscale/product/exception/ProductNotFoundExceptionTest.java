package com.amazonscale.product.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithId() {
        // Act
        ProductNotFoundException exception = new ProductNotFoundException(42L);

        // Assert
        assertNotNull(exception);
        assertEquals("Product not found with id :42", exception.getMessage());
    }
}