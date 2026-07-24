package com.amazonscale.product.controller;

import com.amazonscale.product.dto.ProductRequest;
import com.amazonscale.product.dto.ProductResponse;
import com.amazonscale.product.service.ProductService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;
    private ProductResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();

        sampleResponse = ProductResponse.builder()
                .id(1L)
                .name("Smartphone")
                .description("Latest smartphone")
                .price(new BigDecimal("699.99"))
                .stock(50)
                .brand("TechBrand")
                .active(true)
                .build();
    }

    @Test
    void testCreateProductSuccess() throws Exception {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Smartphone");
        request.setDescription("Latest smartphone");
        request.setPrice(new BigDecimal("699.99"));
        request.setStock(50);
        request.setBrand("TechBrand");

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(sampleResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Smartphone"));

        verify(productService, times(1)).createProduct(any(ProductRequest.class));
    }

    @Test
    void testGetProductSuccess() throws Exception {
        // Arrange
        when(productService.getProduct(1L)).thenReturn(sampleResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.brand").value("TechBrand"));

        verify(productService, times(1)).getProduct(1L);
    }

    @Test
    void testGetAllProductsSuccess() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenReturn(List.of(sampleResponse));

        // Act & Assert
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Smartphone"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void testUpdateProductSuccess() throws Exception {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Smartphone Pro");
        request.setDescription("Pro model");
        request.setPrice(new BigDecimal("899.99"));
        request.setStock(40);
        request.setBrand("TechBrand");

        when(productService.updateProduct(eq(1L), any(ProductRequest.class))).thenReturn(sampleResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(productService, times(1)).updateProduct(eq(1L), any(ProductRequest.class));
    }

    @Test
    void testDeleteProductSuccess() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }
}