package com.amazonscale.inventory.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryNotFoundExceptionTest {

    @Test
    @DisplayName("Should create InventoryNotFoundException with formatted ID message")
    void shouldCreateInventoryNotFoundExceptionWithIdMessage() {
        // Arrange & Act
        InventoryNotFoundException exception = new InventoryNotFoundException(42L);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Inventory not found with ID: 42");
    }

    @Test
    @DisplayName("Should create InventoryNotFoundException with custom string message")
    void shouldCreateInventoryNotFoundExceptionWithCustomMessage() {
        // Arrange
        String customMsg = "Custom inventory missing message";

        // Act
        InventoryNotFoundException exception = new InventoryNotFoundException(customMsg);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(customMsg);
    }
}