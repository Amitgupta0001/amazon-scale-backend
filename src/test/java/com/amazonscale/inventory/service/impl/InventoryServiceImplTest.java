package com.amazonscale.inventory.service.impl;

import com.amazonscale.inventory.dto.InventoryRequest;
import com.amazonscale.inventory.dto.InventoryResponse;
import com.amazonscale.inventory.dto.InventoryUpdateRequest;
import com.amazonscale.inventory.entity.Inventory;
import com.amazonscale.inventory.exception.InsufficientStockException;
import com.amazonscale.inventory.exception.InventoryAlreadyExistsException;
import com.amazonscale.inventory.exception.InventoryNotFoundException;
import com.amazonscale.inventory.repository.InventoryRepository;
import com.amazonscale.product.entity.Product;
import com.amazonscale.product.exception.ProductNotFoundException;
import com.amazonscale.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Product product;
    private Inventory inventory;
    private InventoryRequest createRequest;
    private InventoryUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(10L)
                .name("Smartwatch")
                .build();

        inventory = Inventory.builder()
                .id(1L)
                .product(product)
                .quantity(100)
                .reservedQuantity(10)
                .warehouseLocation("Location A")
                .lowStockThreshold(15)
                .build();

        createRequest = new InventoryRequest();
        createRequest.setProductId(10L);
        createRequest.setQuantity(100);
        createRequest.setWarehouseLocation("Location A");
        createRequest.setLowStockThreshold(15);

        updateRequest = InventoryUpdateRequest.builder()
                .quantity(120)
                .warehouseLocation("Location B")
                .lowStockThreshold(20)
                .build();
    }

    @Test
    @DisplayName("Should create inventory successfully when product exists and inventory does not exist yet")
    void shouldCreateInventorySuccessfully() {
        // Arrange
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(inventoryRepository.existsByProductId(10L)).thenReturn(false);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        // Act
        InventoryResponse response = inventoryService.createInventory(createRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getProductId()).isEqualTo(10L);
        assertThat(response.getProductName()).isEqualTo("Smartwatch");

        verify(productRepository).findById(10L);
        verify(inventoryRepository).existsByProductId(10L);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when creating inventory for non-existent product ID")
    void shouldThrowProductNotFoundExceptionWhenCreatingInventoryForInvalidProduct() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        createRequest.setProductId(99L);

        // Act & Assert
        assertThatThrownBy(() -> inventoryService.createInventory(createRequest))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");

        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Should throw InventoryAlreadyExistsException when inventory for product ID already exists")
    void shouldThrowInventoryAlreadyExistsExceptionWhenDuplicateProduct() {
        // Arrange
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(inventoryRepository.existsByProductId(10L)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> inventoryService.createInventory(createRequest))
                .isInstanceOf(InventoryAlreadyExistsException.class)
                .hasMessageContaining("10");

        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Should update inventory successfully when new quantity >= reserved quantity")
    void shouldUpdateInventorySuccessfully() {
        // Arrange
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        // Act
        InventoryResponse response = inventoryService.updateInventory(1L, updateRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);

        verify(inventoryRepository).findById(1L);
        verify(inventoryRepository).save(inventory);
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when update quantity is less than reserved quantity")
    void shouldThrowInsufficientStockExceptionWhenQuantityLessThanReserved() {
        // Arrange
        updateRequest.setQuantity(5); // Reserved is 10
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));

        // Act & Assert
        assertThatThrownBy(() -> inventoryService.updateInventory(1L, updateRequest))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessage("Quantity cannot be less than reserved quantity.");

        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Should get inventory by ID successfully")
    void shouldGetInventoryByIdSuccessfully() {
        // Arrange
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));

        // Act
        InventoryResponse response = inventoryService.getInventoryById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);

        verify(inventoryRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw InventoryNotFoundException when getting inventory by non-existent ID")
    void shouldThrowInventoryNotFoundExceptionWhenGettingInvalidId() {
        // Arrange
        when(inventoryRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> inventoryService.getInventoryById(99L))
                .isInstanceOf(InventoryNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Should get inventory by Product ID successfully")
    void shouldGetInventoryByProductIdSuccessfully() {
        // Arrange
        when(inventoryRepository.findByProductId(10L)).thenReturn(Optional.of(inventory));

        // Act
        InventoryResponse response = inventoryService.getInventoryByProductId(10L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getProductId()).isEqualTo(10L);

        verify(inventoryRepository).findByProductId(10L);
    }

    @Test
    @DisplayName("Should delete inventory by ID when reserved quantity is zero")
    void shouldDeleteInventorySuccessfullyWhenNoReservedStock() {
        // Arrange
        inventory.setReservedQuantity(0);
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));

        // Act
        inventoryService.deleteInventoryById(1L);

        // Assert
        verify(inventoryRepository).findById(1L);
        verify(inventoryRepository).delete(inventory);
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when deleting inventory with reserved stock > 0")
    void shouldThrowInsufficientStockExceptionWhenDeletingWithReservedStock() {
        // Arrange
        inventory.setReservedQuantity(5);
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));

        // Act & Assert
        assertThatThrownBy(() -> inventoryService.deleteInventoryById(1L))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessage("Cannot delete inventory with reserved stock.");

        verify(inventoryRepository, never()).delete(any(Inventory.class));
    }

    @Test
    @DisplayName("Should return all inventories list")
    void shouldGetAllInventorySuccessfully() {
        // Arrange
        when(inventoryRepository.findAll()).thenReturn(List.of(inventory));

        // Act
        List<InventoryResponse> responses = inventoryService.getAllInventory();

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(1L);

        verify(inventoryRepository).findAll();
    }
}
