package com.amazonscale.inventory.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryAlreadyExistsExceptionTest {

    @Test
    void shouldCreateExceptionWithCorrectMessage() {
        // Act
        InventoryAlreadyExistsException exception = new InventoryAlreadyExistsException(10L);

        // Assert
        assertNotNull(exception);
        assertEquals("Inventory already exists for product ID: 10", exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }
}