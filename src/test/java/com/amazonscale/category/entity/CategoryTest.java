package com.amazonscale.category.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void testCategoryBuilderAndGettersSetters() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Category parent = Category.builder().id(1L).name("Root").build();

        // Act
        Category category = Category.builder()
                .id(2L)
                .name("SubCategory")
                .description("Desc")
                .imageUrl("http://example.com/img.png")
                .parentCategory(parent)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertEquals(2L, category.getId());
        assertEquals("SubCategory", category.getName());
        assertEquals("Desc", category.getDescription());
        assertEquals("http://example.com/img.png", category.getImageUrl());
        assertEquals(parent, category.getParentCategory());
        assertEquals(now, category.getCreatedAt());
        assertEquals(now, category.getUpdatedAt());
    }

    @Test
    void testPrePersistAndPreUpdate() {
        // Arrange
        Category category = new Category();

        // Act
        category.prePersist();

        // Assert
        assertNotNull(category.getCreatedAt());
        assertNotNull(category.getUpdatedAt());

        category.preUpdate();
        assertNotNull(category.getUpdatedAt());
    }
}