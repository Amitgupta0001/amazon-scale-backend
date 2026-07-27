package com.amazonscale.category.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryResponseTest {

    @Test
    @DisplayName("Should build CategoryResponse using Builder and verify getters/setters")
    void shouldBuildCategoryResponseAndVerifyGettersSetters() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        CategoryResponse response = CategoryResponse.builder()
                .id(1L)
                .name("Books")
                .description("Literature & Fiction")
                .imageUrl("https://example.com/books.jpg")
                .parentCategoryId(10L)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Books");
        assertThat(response.getDescription()).isEqualTo("Literature & Fiction");
        assertThat(response.getImageUrl()).isEqualTo("https://example.com/books.jpg");
        assertThat(response.getParentCategoryId()).isEqualTo(10L);
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);

        // Act - Setters
        response.setId(2L);
        response.setName("Comics");
        response.setParentCategoryId(null);

        // Assert
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getName()).isEqualTo("Comics");
        assertThat(response.getParentCategoryId()).isNull();
    }

    @Test
    @DisplayName("Should initialize constructors (no-args and all-args)")
    void shouldInitializeConstructors() {
        // Arrange & Act
        CategoryResponse empty = new CategoryResponse();
        CategoryResponse full = new CategoryResponse(5L, "Toys", "Games", "http://img.jpg", 1L, null, null);

        // Assert
        assertThat(empty.getId()).isNull();
        assertThat(full.getId()).isEqualTo(5L);
        assertThat(full.getName()).isEqualTo("Toys");
        assertThat(full.getParentCategoryId()).isEqualTo(1L);
    }
}