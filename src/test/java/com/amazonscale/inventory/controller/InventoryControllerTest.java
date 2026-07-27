package com.amazonscale.inventory.controller;

import com.amazonscale.inventory.dto.InventoryRequest;
import com.amazonscale.inventory.dto.InventoryResponse;
import com.amazonscale.inventory.dto.InventoryUpdateRequest;
import com.amazonscale.inventory.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private ObjectMapper objectMapper;
    private InventoryResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(inventoryController).build();
        objectMapper = new ObjectMapper();

        sampleResponse = InventoryResponse.builder()
                .id(1L)
                .productId(10L)
                .productName("Smartwatch")
                .quantity(100)
                .reservedQuantity(10)
                .availableQuantity(90)
                .warehouseLocation("Location A")
                .lowStockThreshold(15)
                .lowStock(false)
                .build();
    }

    @Test
    @DisplayName("Should create inventory successfully and return HTTP 201 Created with InventoryResponse")
    void shouldCreateInventorySuccessfully() throws Exception {
        // Arrange
        InventoryRequest request = new InventoryRequest();
        request.setProductId(10L);
        request.setQuantity(100);
        request.setWarehouseLocation("Location A");
        request.setLowStockThreshold(15);

        when(inventoryService.createInventory(any(InventoryRequest.class))).thenReturn(sampleResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.productId").value(10L))
                .andExpect(jsonPath("$.quantity").value(100));

        verify(inventoryService, times(1)).createInventory(any(InventoryRequest.class));
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when InventoryRequest fails Bean Validation")
    void shouldReturnBadRequestWhenCreateInventoryValidationFails() throws Exception {
        // Arrange
        InventoryRequest invalidRequest = new InventoryRequest(); // missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/v1/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(inventoryService, never()).createInventory(any(InventoryRequest.class));
    }

    @Test
    @DisplayName("Should get inventory by ID and return HTTP 200 OK")
    void shouldGetInventoryByIdSuccessfully() throws Exception {
        // Arrange
        when(inventoryService.getInventoryById(1L)).thenReturn(sampleResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/inventory/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.productName").value("Smartwatch"));

        verify(inventoryService, times(1)).getInventoryById(1L);
    }

    @Test
    @DisplayName("Should get all inventories list and return HTTP 200 OK")
    void shouldGetAllInventorySuccessfully() throws Exception {
        // Arrange
        when(inventoryService.getAllInventory()).thenReturn(List.of(sampleResponse));

        // Act & Assert
        mockMvc.perform(get("/api/v1/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].productName").value("Smartwatch"));

        verify(inventoryService, times(1)).getAllInventory();
    }

    @Test
    @DisplayName("Should get inventory by Product ID and return HTTP 200 OK")
    void shouldGetInventoryByProductIdSuccessfully() throws Exception {
        // Arrange
        when(inventoryService.getInventoryByProductId(10L)).thenReturn(sampleResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/inventory/product/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(10L))
                .andExpect(jsonPath("$.productName").value("Smartwatch"));

        verify(inventoryService, times(1)).getInventoryByProductId(10L);
    }

    @Test
    @DisplayName("Should update inventory by ID and return HTTP 200 OK")
    void shouldUpdateInventorySuccessfully() throws Exception {
        // Arrange
        InventoryUpdateRequest updateRequest = InventoryUpdateRequest.builder()
                .quantity(120)
                .warehouseLocation("Location B")
                .lowStockThreshold(20)
                .build();

        when(inventoryService.updateInventory(eq(1L), any(InventoryUpdateRequest.class))).thenReturn(sampleResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/inventory/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(inventoryService, times(1)).updateInventory(eq(1L), any(InventoryUpdateRequest.class));
    }

    @Test
    @DisplayName("Should delete inventory by ID and return HTTP 204 No Content")
    void shouldDeleteInventorySuccessfully() throws Exception {
        // Arrange
        doNothing().when(inventoryService).deleteInventoryById(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/inventory/1"))
                .andExpect(status().isNoContent());

        verify(inventoryService, times(1)).deleteInventoryById(1L);
    }
}