package com.amazonscale.inventory.entity;

import com.amazonscale.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryTest {

    @Test
    @DisplayName("Should build Inventory entity and verify getters and setters")
    void shouldBuildInventoryAndVerifyGettersSetters() {
        // Arrange
        Product product = Product.builder().id(10L).name("Laptop").build();
        LocalDateTime now = LocalDateTime.now();

        // Act
        Inventory inventory = Inventory.builder()
                .id(1L)
                .product(product)
                .quantity(100)
                .reservedQuantity(20)
                .warehouseLocation("Location X")
                .lowStockThreshold(15)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertThat(inventory.getId()).isEqualTo(1L);
        assertThat(inventory.getProduct()).isEqualTo(product);
        assertThat(inventory.getQuantity()).isEqualTo(100);
        assertThat(inventory.getReservedQuantity()).isEqualTo(20);
        assertThat(inventory.getWarehouseLocation()).isEqualTo("Location X");
        assertThat(inventory.getLowStockThreshold()).isEqualTo(15);
        assertThat(inventory.getCreatedAt()).isEqualTo(now);
        assertThat(inventory.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should correctly calculate available quantity dynamically via getAvailableQuantity()")
    void shouldCalculateAvailableQuantityCorrectly() {
        // Arrange
        Inventory inventory = Inventory.builder()
                .quantity(50)
                .reservedQuantity(15)
                .build();

        // Act & Assert
        assertThat(inventory.getAvailableQuantity()).isEqualTo(35);

        // Edge case - reserved > quantity returns 0, not negative
        inventory.setReservedQuantity(60);
        assertThat(inventory.getAvailableQuantity()).isEqualTo(0);

        // Edge case - null fields handled safely as 0
        Inventory empty = new Inventory();
        assertThat(empty.getAvailableQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should populate timestamps automatically on @PrePersist (prePersist) and @PreUpdate (preUpdate)")
    void shouldPopulateTimestampsOnPrePersistAndPreUpdate() {
        // Arrange
        Inventory inventory = new Inventory();

        // Act - Simulating PrePersist
        inventory.prePersist();

        // Assert
        assertThat(inventory.getCreatedAt()).isNotNull();
        assertThat(inventory.getUpdatedAt()).isNotNull();

        LocalDateTime initialUpdatedAt = inventory.getUpdatedAt();

        // Act - Simulating PreUpdate
        inventory.preUpdate();

        // Assert
        assertThat(inventory.getUpdatedAt()).isNotNull();
        assertThat(inventory.getUpdatedAt()).isAfterOrEqualTo(initialUpdatedAt);
    }
}