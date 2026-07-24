package com.amazonscale.category.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateCategoryRequestTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        UpdateCategoryRequest request = new UpdateCategoryRequest();

        // Act
        request.setName("Garden");
        request.setDescription("Garden tools");
        request.setImageUrl("http://example.com/garden.png");
        request.setParentCategoryId(2L);

        // Assert
        assertEquals("Garden", request.getName());
        assertEquals("Garden tools", request.getDescription());
        assertEquals("http://example.com/garden.png", request.getImageUrl());
        assertEquals(2L, request.getParentCategoryId());
    }
}
