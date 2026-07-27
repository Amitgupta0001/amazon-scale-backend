package com.amazonscale.category.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    @Test
    @DisplayName("Should correctly set and get fields using Category Builder and getters/setters")
    void shouldBuildCategoryAndVerifyGettersSetters() {
        // Arrange
        Category parent = Category.builder().id(1L).name("Parent").build();
        LocalDateTime now = LocalDateTime.now();

        // Act
        Category category = Category.builder()
                .id(2L)
                .name("SubCategory")
                .description("Child category description")
                .imageUrl("https://example.com/sub.jpg")
                .parentCategory(parent)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertThat(category.getId()).isEqualTo(2L);
        assertThat(category.getName()).isEqualTo("SubCategory");
        assertThat(category.getDescription()).isEqualTo("Child category description");
        assertThat(category.getImageUrl()).isEqualTo("https://example.com/sub.jpg");
        assertThat(category.getParentCategory()).isEqualTo(parent);
        assertThat(category.getCreatedAt()).isEqualTo(now);
        assertThat(category.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should populate timestamps automatically on @PrePersist (prePersist) and @PreUpdate (preUpdate)")
    void shouldPopulateTimestampsOnPrePersistAndPreUpdate() {
        // Arrange
        Category category = new Category();

        // Act - Simulating PrePersist
        category.prePersist();

        // Assert
        assertThat(category.getCreatedAt()).isNotNull();
        assertThat(category.getUpdatedAt()).isNotNull();

        LocalDateTime initialUpdatedAt = category.getUpdatedAt();

        // Act - Simulating PreUpdate
        category.preUpdate();

        // Assert
        assertThat(category.getUpdatedAt()).isNotNull();
        assertThat(category.getUpdatedAt()).isAfterOrEqualTo(initialUpdatedAt);
    }

    @Test
    @DisplayName("Should verify constructors (no-args and all-args)")
    void shouldVerifyConstructors() {
        // Arrange & Act
        Category empty = new Category();
        Category full = new Category(1L, "Cat", "Desc", "url", null, null, null);

        // Assert
        assertThat(empty.getId()).isNull();
        assertThat(full.getId()).isEqualTo(1L);
        assertThat(full.getName()).isEqualTo("Cat");
    }
}