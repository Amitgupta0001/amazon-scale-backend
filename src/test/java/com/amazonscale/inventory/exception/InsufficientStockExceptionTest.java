package com.amazonscale.inventory.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InsufficientStockExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        // Act
        InsufficientStockException exception = new InsufficientStockException("Out of stock");

        // Assert
        assertEquals("Out of stock", exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }
}