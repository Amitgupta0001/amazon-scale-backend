package com.amazonscale.inventory.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryResponseTest {

    @Test
    @DisplayName("Should build InventoryResponse using Builder and verify all getters/setters")
    void shouldBuildInventoryResponseAndVerifyGettersSetters() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        InventoryResponse response = InventoryResponse.builder()
                .id(1L)
                .productId(10L)
                .productName("Wireless Mouse")
                .quantity(100)
                .reservedQuantity(10)
                .availableQuantity(90)
                .warehouseLocation("Sector 7G")
                .lowStockThreshold(15)
                .lowStock(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getProductId()).isEqualTo(10L);
        assertThat(response.getProductName()).isEqualTo("Wireless Mouse");
        assertThat(response.getQuantity()).isEqualTo(100);
        assertThat(response.getReservedQuantity()).isEqualTo(10);
        assertThat(response.getAvailableQuantity()).isEqualTo(90);
        assertThat(response.getWarehouseLocation()).isEqualTo("Sector 7G");
        assertThat(response.getLowStockThreshold()).isEqualTo(15);
        assertThat(response.getLowStock()).isFalse();
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should verify no-args and all-args constructors")
    void shouldVerifyConstructors() {
        // Arrange & Act
        InventoryResponse empty = new InventoryResponse();
        InventoryResponse full = new InventoryResponse(
                2L, 20L, "Keyboard", 50, 5, 45, "Warehouse B", 10, true, null, null
        );

        // Assert
        assertThat(empty.getId()).isNull();
        assertThat(full.getId()).isEqualTo(2L);
        assertThat(full.getProductName()).isEqualTo("Keyboard");
        assertThat(full.getAvailableQuantity()).isEqualTo(45);
        assertThat(full.getLowStock()).isTrue();
    }
}