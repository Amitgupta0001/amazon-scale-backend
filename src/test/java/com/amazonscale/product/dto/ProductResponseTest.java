package com.amazonscale.product.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductResponseTest {

    @Test
    void testProductResponseBuilderAndGettersSetters() {
        // Act
        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("Headphones")
                .description("Noise cancelling")
                .price(new BigDecimal("199.99"))
                .stock(30)
                .brand("AudioBrand")
                .active(true)
                .build();

        // Assert
        assertEquals(1L, response.getId());
        assertEquals("Headphones", response.getName());
        assertEquals("Noise cancelling", response.getDescription());
        assertEquals(new BigDecimal("199.99"), response.getPrice());
        assertEquals(30, response.getStock());
        assertEquals("AudioBrand", response.getBrand());
        assertTrue(response.getActive());
    }
}