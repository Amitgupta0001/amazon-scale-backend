package com.amazonscale.inventory.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithIdConstructor() {
        // Act
        InventoryNotFoundException exception = new InventoryNotFoundException(5L);

        // Assert
        assertEquals("Inventory not found with ID: 5", exception.getMessage());
    }

    @Test
    void shouldCreateExceptionWithMessageConstructor() {
        // Act
        InventoryNotFoundException exception = new InventoryNotFoundException("Custom not found message");

        // Assert
        assertEquals("Custom not found message", exception.getMessage());
    }
}