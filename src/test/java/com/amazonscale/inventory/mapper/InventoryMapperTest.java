package com.amazonscale.inventory.mapper;

import com.amazonscale.inventory.dto.InventoryRequest;
import com.amazonscale.inventory.dto.InventoryResponse;
import com.amazonscale.inventory.dto.InventoryUpdateRequest;
import com.amazonscale.inventory.entity.Inventory;
import com.amazonscale.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryMapperTest {

    @Test
    @DisplayName("Should map InventoryRequest DTO to Inventory entity")
    void shouldMapInventoryRequestToInventoryEntity() {
        // Arrange
        InventoryRequest request = new InventoryRequest();
        request.setProductId(10L);
        request.setQuantity(50);
        request.setWarehouseLocation("Section B");
        request.setLowStockThreshold(5);

        // Act
        Inventory inventory = InventoryMapper.toInventory(request);

        // Assert
        assertThat(inventory).isNotNull();
        assertThat(inventory.getQuantity()).isEqualTo(50);
        assertThat(inventory.getWarehouseLocation()).isEqualTo("Section B");
        assertThat(inventory.getLowStockThreshold()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should map Inventory entity to InventoryResponse DTO")
    void shouldMapInventoryEntityToInventoryResponse() {
        // Arrange
        Product product = Product.builder().id(10L).name("Smartwatch").build();
        LocalDateTime now = LocalDateTime.now();

        Inventory inventory = Inventory.builder()
                .id(1L)
                .product(product)
                .quantity(100)
                .reservedQuantity(10)
                .warehouseLocation("Section B")
                .lowStockThreshold(15)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Act
        InventoryResponse response = InventoryMapper.toResponse(inventory);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getProductId()).isEqualTo(10L);
        assertThat(response.getProductName()).isEqualTo("Smartwatch");
        assertThat(response.getQuantity()).isEqualTo(100);
        assertThat(response.getReservedQuantity()).isEqualTo(10);
        assertThat(response.getAvailableQuantity()).isEqualTo(90);
        assertThat(response.getWarehouseLocation()).isEqualTo("Section B");
        assertThat(response.getLowStockThreshold()).isEqualTo(15);
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should update existing Inventory entity from InventoryUpdateRequest DTO")
    void shouldUpdateInventoryFromUpdateRequest() {
        // Arrange
        Inventory inventory = Inventory.builder()
                .quantity(50)
                .warehouseLocation("Old Location")
                .lowStockThreshold(10)
                .build();

        InventoryUpdateRequest updateRequest = InventoryUpdateRequest.builder()
                .quantity(80)
                .warehouseLocation("New Location")
                .lowStockThreshold(20)
                .build();

        // Act
        InventoryMapper.updateInventory(inventory, updateRequest);

        // Assert
        assertThat(inventory.getQuantity()).isEqualTo(80);
        assertThat(inventory.getWarehouseLocation()).isEqualTo("New Location");
        assertThat(inventory.getLowStockThreshold()).isEqualTo(20);
    }

    @Test
    @DisplayName("Should instantiate private constructor via reflection for test coverage")
    void shouldInstantiatePrivateConstructorForCoverage() throws Exception {
        // Arrange
        Constructor<InventoryMapper> constructor = InventoryMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // Act
        InventoryMapper instance = constructor.newInstance();

        // Assert
        assertThat(instance).isNotNull();
    }
}