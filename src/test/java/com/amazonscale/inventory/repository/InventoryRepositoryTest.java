package com.amazonscale.inventory.repository;

import com.amazonscale.inventory.entity.Inventory;
import com.amazonscale.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryRepositoryTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Test
    @DisplayName("Should find Inventory by Product ID when inventory exists")
    void shouldFindInventoryByProductId() {
        // Arrange
        Product product = Product.builder().id(10L).build();
        Inventory inventory = Inventory.builder()
                .product(product)
                .quantity(50)
                .reservedQuantity(5)
                .warehouseLocation("Warehouse Alpha")
                .lowStockThreshold(10)
                .build();
        when(inventoryRepository.findByProductId(10L)).thenReturn(Optional.of(inventory));

        // Act
        Optional<Inventory> found = inventoryRepository.findByProductId(10L);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getProduct().getId()).isEqualTo(10L);
        assertThat(found.get().getQuantity()).isEqualTo(50);
    }

    @Test
    @DisplayName("Should return true for existsByProductId when inventory for Product ID exists")
    void shouldReturnTrueWhenInventoryExistsByProductId() {
        // Arrange
        when(inventoryRepository.existsByProductId(10L)).thenReturn(true);

        // Act
        boolean exists = inventoryRepository.existsByProductId(10L);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false for existsByProductId when inventory for Product ID does not exist")
    void shouldReturnFalseWhenInventoryDoesNotExistByProductId() {
        // Arrange
        when(inventoryRepository.existsByProductId(999L)).thenReturn(false);

        // Act
        boolean exists = inventoryRepository.existsByProductId(999L);

        // Assert
        assertThat(exists).isFalse();
    }
}
