package com.amazonscale.category.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateCategoryRequestTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        CreateCategoryRequest request = new CreateCategoryRequest();

        // Act
        request.setName("Home");
        request.setDescription("Home items");
        request.setImageUrl("http://example.com/home.png");
        request.setParentCategoryId(1L);

        // Assert
        assertEquals("Home", request.getName());
        assertEquals("Home items", request.getDescription());
        assertEquals("http://example.com/home.png", request.getImageUrl());
        assertEquals(1L, request.getParentCategoryId());
    }
}
