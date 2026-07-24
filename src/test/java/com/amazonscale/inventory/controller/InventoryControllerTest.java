package com.amazonscale.inventory.controller;

import com.amazonscale.inventory.dto.InventoryRequest;
import com.amazonscale.inventory.dto.InventoryResponse;
import com.amazonscale.inventory.dto.InventoryUpdateRequest;
import com.amazonscale.inventory.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
                .productName("Test Product")
                .quantity(100)
                .reservedQuantity(10)
                .availableQuantity(90)
                .warehouseLocation("Warehouse A")
                .lowStockThreshold(15)
                .build();
    }

    @Test
    void testCreateInventorySuccess() throws Exception {
        // Arrange
        InventoryRequest request = new InventoryRequest();
        request.setProductId(10L);
        request.setQuantity(100);
        request.setWarehouseLocation("Warehouse A");
        request.setLowStockThreshold(15);

        when(inventoryService.createInventory(any(InventoryRequest.class))).thenReturn(sampleResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.productId").value(10L))
                .andExpect(jsonPath("$.warehouseLocation").value("Warehouse A"));

        verify(inventoryService, times(1)).createInventory(any(InventoryRequest.class));
    }

    @Test
    void testGetInventoryByIdSuccess() throws Exception {
        // Arrange
        when(inventoryService.getInventoryById(1L)).thenReturn(sampleResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/inventory/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.productName").value("Test Product"));

        verify(inventoryService, times(1)).getInventoryById(1L);
    }

    @Test
    void testGetAllInventorySuccess() throws Exception {
        // Arrange
        when(inventoryService.getAllInventory()).thenReturn(List.of(sampleResponse));

        // Act & Assert
        mockMvc.perform(get("/api/v1/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(inventoryService, times(1)).getAllInventory();
    }

    @Test
    void testGetInventoryByProductIdSuccess() throws Exception {
        // Arrange
        when(inventoryService.getInventoryByProductId(10L)).thenReturn(sampleResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/inventory/product/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(10L));

        verify(inventoryService, times(1)).getInventoryByProductId(10L);
    }

    @Test
    void testUpdateInventorySuccess() throws Exception {
        // Arrange
        InventoryUpdateRequest updateRequest = InventoryUpdateRequest.builder()
                .quantity(150)
                .warehouseLocation("Warehouse B")
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
    void testDeleteInventorySuccess() throws Exception {
        // Arrange
        doNothing().when(inventoryService).deleteInventoryById(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/inventory/1"))
                .andExpect(status().isNoContent());

        verify(inventoryService, times(1)).deleteInventoryById(1L);
    }
}