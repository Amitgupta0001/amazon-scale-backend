package com.amazonscale.inventory.controller;

import com.amazonscale.inventory.dto.InventoryRequest;
import com.amazonscale.inventory.dto.InventoryResponse;
import com.amazonscale.inventory.dto.InventoryUpdateRequest;
import com.amazonscale.inventory.exception.InventoryNotFoundException;
import com.amazonscale.inventory.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private InventoryResponse buildResponse() {

        return InventoryResponse.builder()
                .id(1L)
                .productId(1L)
                .productName("Laptop")
                .quantity(100)
                .reservedQuantity(0)
                .availableQuantity(100)
                .warehouseLocation("Warehouse A")
                .lowStockThreshold(10)
                .build();
    }
    @Test
    void shouldCreateInventory() throws Exception {

        InventoryRequest request = new InventoryRequest();

        request.setProductId(1L);
        request.setQuantity(100);
        request.setWarehouseLocation("Warehouse A");
        request.setLowStockThreshold(10);

        when(inventoryService.createInventory(any()))
                .thenReturn(buildResponse());

        mockMvc.perform(post("/api/v1/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.quantity").value(100))
                .andExpect(jsonPath("$.warehouseLocation")
                        .value("Warehouse A"));
    }

    @Test
    void shouldGetInventoryById() throws Exception {

        when(inventoryService.getInventoryById(1L))
                .thenReturn(buildResponse());

        mockMvc.perform(get("/api/v1/inventory/1"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productName")
                        .value("Laptop"))
                .andExpect(jsonPath("$.quantity")
                        .value(100));
    }

    @Test
    void shouldGetAllInventories() throws Exception {

        when(inventoryService.getAllInventory())
                .thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/v1/inventory"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].productName")
                        .value("Laptop"));
    }

    @Test
    void shouldGetInventoryByProductId() throws Exception {

        when(inventoryService.getInventoryByProductId(1L))
                .thenReturn(buildResponse());

        mockMvc.perform(get("/api/v1/inventory/product/1"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId")
                        .value(1))
                .andExpect(jsonPath("$.quantity")
                        .value(100));
    }

    @Test
    void shouldUpdateInventory() throws Exception {

        InventoryUpdateRequest request = InventoryUpdateRequest.builder()
                .quantity(200)
                .warehouseLocation("Warehouse B")
                .lowStockThreshold(20)
                .build();

        InventoryResponse response = InventoryResponse.builder()
                .id(1L)
                .productId(1L)
                .productName("Laptop")
                .quantity(200)
                .reservedQuantity(0)
                .availableQuantity(200)
                .warehouseLocation("Warehouse B")
                .lowStockThreshold(20)
                .build();

        when(inventoryService.updateInventory(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(
                        put("/api/v1/inventory/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(200))
                .andExpect(jsonPath("$.warehouseLocation")
                        .value("Warehouse B"));
    }

    @Test
    void shouldDeleteInventory() throws Exception {

        doNothing().when(inventoryService)
                .deleteInventoryById(1L);

        mockMvc.perform(delete("/api/v1/inventory/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenInventoryNotFound() throws Exception {

        when(inventoryService.getInventoryById(1L))
                .thenThrow(new InventoryNotFoundException(1L));

        mockMvc.perform(get("/api/v1/inventory/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenCreateRequestIsInvalid() throws Exception {

        InventoryRequest request = new InventoryRequest();

        mockMvc.perform(
                        post("/api/v1/inventory")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenUpdateRequestIsInvalid() throws Exception {

        InventoryUpdateRequest request =
                InventoryUpdateRequest.builder().build();

        mockMvc.perform(
                        put("/api/v1/inventory/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenProductInventoryDoesNotExist() throws Exception {

        when(inventoryService.getInventoryByProductId(99L))
                .thenThrow(
                        new InventoryNotFoundException(
                                "Inventory not found for product ID: 99"
                        )
                );

        mockMvc.perform(get("/api/v1/inventory/product/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnEmptyInventoryList() throws Exception {

        when(inventoryService.getAllInventory())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

}