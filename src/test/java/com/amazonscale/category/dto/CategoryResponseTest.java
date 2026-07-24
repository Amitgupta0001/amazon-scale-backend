package com.amazonscale.category.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CategoryResponseTest {

    @Test
    void testCategoryResponseBuilderAndGettersSetters() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        CategoryResponse response = CategoryResponse.builder()
                .id(1L)
                .name("Toys")
                .description("Kids toys")
                .imageUrl("http://example.com/toys.png")
                .parentCategoryId(3L)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertEquals(1L, response.getId());
        assertEquals("Toys", response.getName());
        assertEquals("Kids toys", response.getDescription());
        assertEquals("http://example.com/toys.png", response.getImageUrl());
        assertEquals(3L, response.getParentCategoryId());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }
}