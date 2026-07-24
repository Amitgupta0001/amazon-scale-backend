package com.amazonscale.product.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductRequestTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        ProductRequest request = new ProductRequest();

        // Act
        request.setName("Headphones");
        request.setDescription("Noise cancelling");
        request.setPrice(new BigDecimal("199.99"));
        request.setStock(30);
        request.setBrand("AudioBrand");

        // Assert
        assertEquals("Headphones", request.getName());
        assertEquals("Noise cancelling", request.getDescription());
        assertEquals(new BigDecimal("199.99"), request.getPrice());
        assertEquals(30, request.getStock());
        assertEquals("AudioBrand", request.getBrand());
    }
}
