package com.amazonscale.inventory.mapper;

import com.amazonscale.inventory.dto.InventoryRequest;
import com.amazonscale.inventory.dto.InventoryResponse;
import com.amazonscale.inventory.dto.InventoryUpdateRequest;
import com.amazonscale.inventory.entity.Inventory;
import com.amazonscale.product.entity.Product;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InventoryMapperTest {

    @Test
    void toInventory() {
        // Arrange
        InventoryRequest request = new InventoryRequest();
        request.setProductId(5L);
        request.setQuantity(20);
        request.setWarehouseLocation("Rack 4");
        request.setLowStockThreshold(5);

        // Act
        Inventory inventory = InventoryMapper.toInventory(request);

        // Assert
        assertNotNull(inventory);
        assertEquals(20, inventory.getQuantity());
        assertEquals("Rack 4", inventory.getWarehouseLocation());
        assertEquals(5, inventory.getLowStockThreshold());
    }

    @Test
    void toResponse() {
        // Arrange
        Product product = Product.builder()
                .id(5L)
                .name("Keyboard")
                .build();

        LocalDateTime now = LocalDateTime.now();
        Inventory inventory = Inventory.builder()
                .id(1L)
                .product(product)
                .quantity(50)
                .reservedQuantity(10)
                .warehouseLocation("Rack 4")
                .lowStockThreshold(5)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Act
        InventoryResponse response = InventoryMapper.toResponse(inventory);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(5L, response.getProductId());
        assertEquals("Keyboard", response.getProductName());
        assertEquals(50, response.getQuantity());
        assertEquals(10, response.getReservedQuantity());
        assertEquals(40, response.getAvailableQuantity());
        assertEquals("Rack 4", response.getWarehouseLocation());
    }

    @Test
    void updateInventory() {
        // Arrange
        Inventory inventory = Inventory.builder()
                .quantity(10)
                .warehouseLocation("Old")
                .lowStockThreshold(2)
                .build();

        InventoryUpdateRequest updateRequest = InventoryUpdateRequest.builder()
                .quantity(100)
                .warehouseLocation("New")
                .lowStockThreshold(15)
                .build();

        // Act
        InventoryMapper.updateInventory(inventory, updateRequest);

        // Assert
        assertEquals(100, inventory.getQuantity());
        assertEquals("New", inventory.getWarehouseLocation());
        assertEquals(15, inventory.getLowStockThreshold());
    }
}