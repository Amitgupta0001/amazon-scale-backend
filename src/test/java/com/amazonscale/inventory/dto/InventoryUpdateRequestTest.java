package com.amazonscale.inventory.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryUpdateRequestTest {

    @Test
    void testInventoryUpdateRequestBuilderAndGetters() {
        // Act
        InventoryUpdateRequest request = InventoryUpdateRequest.builder()
                .quantity(75)
                .warehouseLocation("Location C")
                .lowStockThreshold(12)
                .build();

        // Assert
        assertEquals(75, request.getQuantity());
        assertEquals("Location C", request.getWarehouseLocation());
        assertEquals(12, request.getLowStockThreshold());
    }

    @Test
    void testSetters() {
        // Arrange
        InventoryUpdateRequest request = new InventoryUpdateRequest();

        // Act
        request.setQuantity(30);
        request.setWarehouseLocation("Location D");
        request.setLowStockThreshold(8);

        // Assert
        assertEquals(30, request.getQuantity());
        assertEquals("Location D", request.getWarehouseLocation());
        assertEquals(8, request.getLowStockThreshold());
    }
}