package com.amazonscale.inventory.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryAlreadyExistsExceptionTest {

    @Test
    @DisplayName("Should create InventoryAlreadyExistsException with formatted message containing product ID")
    void shouldCreateInventoryAlreadyExistsExceptionWithCorrectMessage() {
        // Arrange & Act
        InventoryAlreadyExistsException exception = new InventoryAlreadyExistsException(100L);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Inventory already exists for product ID: 100");
    }
}