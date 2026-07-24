package com.amazonscale.inventory.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryRequestTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        InventoryRequest request = new InventoryRequest();

        // Act
        request.setProductId(1L);
        request.setQuantity(50);
        request.setWarehouseLocation("WH-1");
        request.setLowStockThreshold(5);

        // Assert
        assertEquals(1L, request.getProductId());
        assertEquals(50, request.getQuantity());
        assertEquals("WH-1", request.getWarehouseLocation());
        assertEquals(5, request.getLowStockThreshold());
    }
}
