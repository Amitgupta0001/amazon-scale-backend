package com.amazonscale.inventory.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InventoryResponseTest {

    @Test
    void testInventoryResponseBuilderAndGetters() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        InventoryResponse response = InventoryResponse.builder()
                .id(1L)
                .productId(2L)
                .productName("Widget")
                .quantity(100)
                .reservedQuantity(10)
                .availableQuantity(90)
                .warehouseLocation("Location B")
                .lowStockThreshold(20)
                .lowStock(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertEquals(1L, response.getId());
        assertEquals(2L, response.getProductId());
        assertEquals("Widget", response.getProductName());
        assertEquals(100, response.getQuantity());
        assertEquals(10, response.getReservedQuantity());
        assertEquals(90, response.getAvailableQuantity());
        assertEquals("Location B", response.getWarehouseLocation());
        assertEquals(20, response.getLowStockThreshold());
        assertFalse(response.getLowStock());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }

    @Test
    void testSetters() {
        // Arrange
        InventoryResponse response = new InventoryResponse();

        // Act
        response.setId(5L);
        response.setLowStock(true);

        // Assert
        assertEquals(5L, response.getId());
        assertTrue(response.getLowStock());
    }
}