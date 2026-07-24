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

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    private InventoryRequest inventoryRequest;
    private InventoryUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {

        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .build();

        inventory = Inventory.builder()
                .id(1L)
                .product(product)
                .quantity(100)
                .reservedQuantity(0)
                .warehouseLocation("Warehouse A")
                .lowStockThreshold(10)
                .build();

        inventoryRequest = new InventoryRequest();
        inventoryRequest.setProductId(1L);
        inventoryRequest.setQuantity(100);
        inventoryRequest.setWarehouseLocation("Warehouse A");
        inventoryRequest.setLowStockThreshold(10);

        updateRequest = InventoryUpdateRequest.builder()
                .quantity(150)
                .warehouseLocation("Warehouse B")
                .lowStockThreshold(20)
                .build();
    }

    @Test
    void shouldCreateInventorySuccessfully() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(inventoryRepository.existsByProductId(1L))
                .thenReturn(false);

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        InventoryResponse response =
                inventoryService.createInventory(inventoryRequest);

        assertNotNull(response);
        assertEquals(100, response.getQuantity());
        assertEquals(1L, response.getProductId());
        assertEquals("Warehouse A", response.getWarehouseLocation());

        verify(productRepository).findById(1L);
        verify(inventoryRepository).existsByProductId(1L);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void shouldCaptureSavedInventory() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(inventoryRepository.existsByProductId(1L))
                .thenReturn(false);

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        inventoryService.createInventory(inventoryRequest);

        ArgumentCaptor<Inventory> captor =
                ArgumentCaptor.forClass(Inventory.class);

        verify(inventoryRepository).save(captor.capture());

        Inventory savedInventory = captor.getValue();

        assertEquals(100, savedInventory.getQuantity());
        assertEquals(product, savedInventory.getProduct());
        assertEquals("Warehouse A", savedInventory.getWarehouseLocation());
    }

    @Test
    void shouldThrowProductNotFoundException() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> inventoryService.createInventory(inventoryRequest)
        );

        verify(productRepository).findById(1L);

        verify(inventoryRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowInventoryAlreadyExistsException() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(inventoryRepository.existsByProductId(1L))
                .thenReturn(true);

        assertThrows(
                InventoryAlreadyExistsException.class,
                () -> inventoryService.createInventory(inventoryRequest)
        );

        verify(inventoryRepository, never())
                .save(any());
    }

    @Test
    void shouldUpdateInventorySuccessfully() {

        when(inventoryRepository.findById(1L))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        InventoryResponse response =
                inventoryService.updateInventory(1L, updateRequest);

        assertNotNull(response);
        assertEquals(150, response.getQuantity());
        assertEquals("Warehouse B", response.getWarehouseLocation());
        assertEquals(20, response.getLowStockThreshold());

        verify(inventoryRepository).findById(1L);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void shouldCaptureUpdatedInventory() {

        when(inventoryRepository.findById(1L))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        inventoryService.updateInventory(1L, updateRequest);

        ArgumentCaptor<Inventory> captor =
                ArgumentCaptor.forClass(Inventory.class);

        verify(inventoryRepository).save(captor.capture());

        Inventory updatedInventory = captor.getValue();

        assertEquals(150, updatedInventory.getQuantity());
        assertEquals("Warehouse B", updatedInventory.getWarehouseLocation());
        assertEquals(20, updatedInventory.getLowStockThreshold());
    }

    @Test
    void shouldThrowInventoryNotFoundWhileUpdating() {

        when(inventoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                InventoryNotFoundException.class,
                () -> inventoryService.updateInventory(1L, updateRequest)
        );

        verify(inventoryRepository).findById(1L);
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void shouldThrowInsufficientStockExceptionWhileUpdating() {

        inventory.setReservedQuantity(120);

        updateRequest.setQuantity(100);

        when(inventoryRepository.findById(1L))
                .thenReturn(Optional.of(inventory));

        assertThrows(
                InsufficientStockException.class,
                () -> inventoryService.updateInventory(1L, updateRequest)
        );

        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void shouldGetInventoryByIdSuccessfully() {

        when(inventoryRepository.findById(1L))
                .thenReturn(Optional.of(inventory));

        InventoryResponse response =
                inventoryService.getInventoryById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(100, response.getQuantity());
        assertEquals("Warehouse A", response.getWarehouseLocation());

        verify(inventoryRepository).findById(1L);
    }

    @Test
    void shouldThrowInventoryNotFoundWhileGettingById() {

        when(inventoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                InventoryNotFoundException.class,
                () -> inventoryService.getInventoryById(1L)
        );

        verify(inventoryRepository).findById(1L);
    }

    @Test
    void shouldGetInventoryByProductIdSuccessfully() {

        when(inventoryRepository.findByProductId(1L))
                .thenReturn(Optional.of(inventory));

        InventoryResponse response =
                inventoryService.getInventoryByProductId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getProductId());
        assertEquals(100, response.getQuantity());

        verify(inventoryRepository).findByProductId(1L);
    }

    @Test
    void shouldThrowInventoryNotFoundWhileGettingByProductId() {

        when(inventoryRepository.findByProductId(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                InventoryNotFoundException.class,
                () -> inventoryService.getInventoryByProductId(1L)
        );

        verify(inventoryRepository).findByProductId(1L);
    }
    @Test
    void shouldDeleteInventorySuccessfully() {

        when(inventoryRepository.findById(1L))
                .thenReturn(Optional.of(inventory));

        doNothing().when(inventoryRepository).delete(inventory);

        inventoryService.deleteInventoryById(1L);

        verify(inventoryRepository).findById(1L);
        verify(inventoryRepository).delete(inventory);
    }

    @Test
    void shouldThrowInventoryNotFoundWhileDeleting() {

        when(inventoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                InventoryNotFoundException.class,
                () -> inventoryService.deleteInventoryById(1L)
        );

        verify(inventoryRepository).findById(1L);
        verify(inventoryRepository, never()).delete(any());
    }

    @Test
    void shouldThrowInsufficientStockExceptionWhileDeleting() {

        inventory.setReservedQuantity(15);

        when(inventoryRepository.findById(1L))
                .thenReturn(Optional.of(inventory));

        assertThrows(
                InsufficientStockException.class,
                () -> inventoryService.deleteInventoryById(1L)
        );

        verify(inventoryRepository).findById(1L);
        verify(inventoryRepository, never()).delete(any());
    }

    @Test
    void shouldReturnAllInventories() {

        Inventory secondInventory = Inventory.builder()
                .id(2L)
                .product(Product.builder()
                        .id(2L)
                        .name("Keyboard")
                        .build())
                .quantity(40)
                .reservedQuantity(0)
                .warehouseLocation("Warehouse B")
                .lowStockThreshold(5)
                .build();

        when(inventoryRepository.findAll())
                .thenReturn(List.of(inventory, secondInventory));

        List<InventoryResponse> response =
                inventoryService.getAllInventory();

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals("Laptop",
                response.get(0).getProductName());

        assertEquals("Keyboard",
                response.get(1).getProductName());

        verify(inventoryRepository).findAll();
    }

    @Test
    void shouldReturnEmptyInventoryList() {

        when(inventoryRepository.findAll())
                .thenReturn(List.of());

        List<InventoryResponse> response =
                inventoryService.getAllInventory();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(inventoryRepository).findAll();
    }

    @Test
    void shouldVerifyCreateInventoryRepositoryCalls() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(inventoryRepository.existsByProductId(1L))
                .thenReturn(false);

        when(inventoryRepository.save(any()))
                .thenReturn(inventory);

        inventoryService.createInventory(inventoryRequest);

        verify(productRepository, times(1))
                .findById(1L);

        verify(inventoryRepository, times(1))
                .existsByProductId(1L);

        verify(inventoryRepository, times(1))
                .save(any());
    }

    @Test
    void shouldVerifyUpdateRepositoryCalls() {

        when(inventoryRepository.findById(1L))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any()))
                .thenReturn(inventory);

        inventoryService.updateInventory(1L, updateRequest);

        verify(inventoryRepository, times(1))
                .findById(1L);

        verify(inventoryRepository, times(1))
                .save(any());
    }

    @Test
    void shouldVerifyDeleteRepositoryCalls() {

        when(inventoryRepository.findById(1L))
                .thenReturn(Optional.of(inventory));

        doNothing().when(inventoryRepository)
                .delete(inventory);

        inventoryService.deleteInventoryById(1L);

        verify(inventoryRepository, times(1))
                .findById(1L);

        verify(inventoryRepository, times(1))
                .delete(inventory);
    }
}
