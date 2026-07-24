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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("Gaming Laptop")
                .build();

        inventory = Inventory.builder()
                .id(100L)
                .product(product)
                .quantity(50)
                .reservedQuantity(5)
                .warehouseLocation("Section A")
                .lowStockThreshold(10)
                .build();
    }

    @Test
    void createInventorySuccess() {
        // Arrange
        InventoryRequest request = new InventoryRequest();
        request.setProductId(1L);
        request.setQuantity(50);
        request.setWarehouseLocation("Section A");
        request.setLowStockThreshold(10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.existsByProductId(1L)).thenReturn(false);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        // Act
        InventoryResponse response = inventoryService.createInventory(request);

        // Assert
        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Laptop", response.getProductName());
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void shouldThrowProductNotFoundWhenCreatingInventoryForMissingProduct() {
        // Arrange
        InventoryRequest request = new InventoryRequest();
        request.setProductId(99L);

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> inventoryService.createInventory(request));
    }

    @Test
    void shouldThrowInventoryAlreadyExistsWhenCreatingDuplicateInventory() {
        // Arrange
        InventoryRequest request = new InventoryRequest();
        request.setProductId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.existsByProductId(1L)).thenReturn(true);

        // Act & Assert
        assertThrows(InventoryAlreadyExistsException.class, () -> inventoryService.createInventory(request));
    }

    @Test
    void updateInventorySuccess() {
        // Arrange
        InventoryUpdateRequest updateRequest = InventoryUpdateRequest.builder()
                .quantity(60)
                .warehouseLocation("Section B")
                .lowStockThreshold(15)
                .build();

        when(inventoryRepository.findById(100L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        // Act
        InventoryResponse response = inventoryService.updateInventory(100L, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(60, inventory.getQuantity());
        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    void shouldThrowInsufficientStockWhenUpdateQuantityBelowReserved() {
        // Arrange
        InventoryUpdateRequest updateRequest = InventoryUpdateRequest.builder()
                .quantity(2) // lower than reserved quantity (5)
                .warehouseLocation("Section B")
                .lowStockThreshold(15)
                .build();

        when(inventoryRepository.findById(100L)).thenReturn(Optional.of(inventory));

        // Act & Assert
        assertThrows(InsufficientStockException.class, () -> inventoryService.updateInventory(100L, updateRequest));
    }

    @Test
    void getInventoryByIdSuccess() {
        // Arrange
        when(inventoryRepository.findById(100L)).thenReturn(Optional.of(inventory));

        // Act
        InventoryResponse response = inventoryService.getInventoryById(100L);

        // Assert
        assertNotNull(response);
        assertEquals(100L, response.getId());
    }

    @Test
    void shouldThrowInventoryNotFoundWhenGetByIdFails() {
        // Arrange
        when(inventoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InventoryNotFoundException.class, () -> inventoryService.getInventoryById(999L));
    }

    @Test
    void getInventoryByProductIdSuccess() {
        // Arrange
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));

        // Act
        InventoryResponse response = inventoryService.getInventoryByProductId(1L);

        // Assert
        assertNotNull(response);
        assertEquals(100L, response.getId());
    }

    @Test
    void deleteInventoryByIdSuccess() {
        // Arrange
        inventory.setReservedQuantity(0);
        when(inventoryRepository.findById(100L)).thenReturn(Optional.of(inventory));

        // Act
        inventoryService.deleteInventoryById(100L);

        // Assert
        verify(inventoryRepository, times(1)).delete(inventory);
    }

    @Test
    void shouldThrowInsufficientStockWhenDeletingInventoryWithReservedStock() {
        // Arrange
        inventory.setReservedQuantity(5);
        when(inventoryRepository.findById(100L)).thenReturn(Optional.of(inventory));

        // Act & Assert
        assertThrows(InsufficientStockException.class, () -> inventoryService.deleteInventoryById(100L));
    }

    @Test
    void getAllInventorySuccess() {
        // Arrange
        when(inventoryRepository.findAll()).thenReturn(List.of(inventory));

        // Act
        List<InventoryResponse> responses = inventoryService.getAllInventory();

        // Assert
        assertEquals(1, responses.size());
        assertEquals(100L, responses.get(0).getId());
    }
}
