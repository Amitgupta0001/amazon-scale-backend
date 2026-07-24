package com.amazonscale.product.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testProductBuilderAndGettersSetters() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        Product product = Product.builder()
                .id(10L)
                .name("Camera")
                .description("DSLR Camera")
                .price(new BigDecimal("999.99"))
                .stock(5)
                .brand("CamBrand")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertEquals(10L, product.getId());
        assertEquals("Camera", product.getName());
        assertEquals("DSLR Camera", product.getDescription());
        assertEquals(new BigDecimal("999.99"), product.getPrice());
        assertEquals(5, product.getStock());
        assertEquals("CamBrand", product.getBrand());
        assertTrue(product.getActive());
        assertEquals(now, product.getCreatedAt());
        assertEquals(now, product.getUpdatedAt());
    }

    @Test
    void testPrePersistAndPreUpdate() {
        // Arrange
        Product product = new Product();

        // Act
        product.prePersist();

        // Assert
        assertNotNull(product.getCreatedAt());
        assertNotNull(product.getUpdatedAt());

        product.preUpdate();
        assertNotNull(product.getUpdatedAt());
    }
}