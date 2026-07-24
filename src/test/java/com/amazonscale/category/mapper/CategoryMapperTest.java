package com.amazonscale.category.mapper;

import com.amazonscale.category.dto.CategoryResponse;
import com.amazonscale.category.dto.CreateCategoryRequest;
import com.amazonscale.category.entity.Category;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CategoryMapperTest {

    @Test
    void toCategory() {
        // Arrange
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Books");
        request.setDescription("All kinds of books");
        request.setImageUrl("http://example.com/books.png");

        // Act
        Category category = CategoryMapper.toCategory(request);

        // Assert
        assertNotNull(category);
        assertEquals("Books", category.getName());
        assertEquals("All kinds of books", category.getDescription());
        assertEquals("http://example.com/books.png", category.getImageUrl());
    }

    @Test
    void toResponseWithoutParent() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Category category = Category.builder()
                .id(10L)
                .name("Books")
                .description("All kinds of books")
                .imageUrl("http://example.com/books.png")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Act
        CategoryResponse response = CategoryMapper.toResponse(category);

        // Assert
        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Books", response.getName());
        assertNull(response.getParentCategoryId());
    }

    @Test
    void toResponseWithParent() {
        // Arrange
        Category parent = Category.builder().id(2L).name("Media").build();
        Category category = Category.builder()
                .id(10L)
                .name("Books")
                .parentCategory(parent)
                .build();

        // Act
        CategoryResponse response = CategoryMapper.toResponse(category);

        // Assert
        assertNotNull(response);
        assertEquals(2L, response.getParentCategoryId());
    }
}