package com.amazonscale.product.mapper;

import com.amazonscale.product.dto.ProductRequest;
import com.amazonscale.product.dto.ProductResponse;
import com.amazonscale.product.entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    @Test
    void toEntity() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Monitor");
        request.setDescription("4K Monitor");
        request.setPrice(new BigDecimal("399.99"));
        request.setStock(15);
        request.setBrand("DisplayBrand");

        // Act
        Product product = ProductMapper.toEntity(request);

        // Assert
        assertNotNull(product);
        assertEquals("Monitor", product.getName());
        assertEquals("4K Monitor", product.getDescription());
        assertEquals(new BigDecimal("399.99"), product.getPrice());
        assertEquals(15, product.getStock());
        assertEquals("DisplayBrand", product.getBrand());
    }

    @Test
    void toResponse() {
        // Arrange
        Product product = Product.builder()
                .id(1L)
                .name("Monitor")
                .description("4K Monitor")
                .price(new BigDecimal("399.99"))
                .stock(15)
                .brand("DisplayBrand")
                .active(true)
                .build();

        // Act
        ProductResponse response = ProductMapper.toResponse(product);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Monitor", response.getName());
        assertEquals("4K Monitor", response.getDescription());
        assertEquals(new BigDecimal("399.99"), response.getPrice());
        assertEquals(15, response.getStock());
        assertEquals("DisplayBrand", response.getBrand());
        assertTrue(response.getActive());
    }
}