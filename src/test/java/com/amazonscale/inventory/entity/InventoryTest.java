package com.amazonscale.inventory.entity;

import com.amazonscale.product.entity.Product;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    @Test
    void testInventoryGettersSettersAndBuilder() {
        // Arrange
        Product product = Product.builder().id(10L).name("Phone").build();
        LocalDateTime now = LocalDateTime.now();

        // Act
        Inventory inventory = Inventory.builder()
                .id(1L)
                .product(product)
                .quantity(100)
                .reservedQuantity(20)
                .warehouseLocation("Location A")
                .lowStockThreshold(10)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertEquals(1L, inventory.getId());
        assertEquals(product, inventory.getProduct());
        assertEquals(100, inventory.getQuantity());
        assertEquals(20, inventory.getReservedQuantity());
        assertEquals(80, inventory.getAvailableQuantity());
        assertEquals("Location A", inventory.getWarehouseLocation());
        assertEquals(10, inventory.getLowStockThreshold());
        assertEquals(now, inventory.getCreatedAt());
        assertEquals(now, inventory.getUpdatedAt());
    }

    @Test
    void testPrePersistAndPreUpdate() {
        // Arrange
        Inventory inventory = new Inventory();

        // Act
        inventory.prePersist();

        // Assert
        assertNotNull(inventory.getCreatedAt());
        assertNotNull(inventory.getUpdatedAt());

        LocalDateTime prevUpdate = inventory.getUpdatedAt();
        inventory.preUpdate();
        assertNotNull(inventory.getUpdatedAt());
    }

    @Test
    void testGetAvailableQuantityWhenNull() {
        // Arrange
        Inventory inventory = new Inventory();

        // Act & Assert
        assertEquals(0, inventory.getAvailableQuantity());
    }
}