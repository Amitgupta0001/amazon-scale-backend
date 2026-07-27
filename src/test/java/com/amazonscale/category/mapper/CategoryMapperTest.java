package com.amazonscale.category.mapper;

import com.amazonscale.category.dto.CategoryResponse;
import com.amazonscale.category.dto.CreateCategoryRequest;
import com.amazonscale.category.entity.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryMapperTest {

    @Test
    @DisplayName("Should map CreateCategoryRequest DTO to Category entity")
    void shouldMapCreateCategoryRequestToCategoryEntity() {
        // Arrange
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Sports");
        request.setDescription("Sporting goods");
        request.setImageUrl("https://example.com/sports.jpg");

        // Act
        Category category = CategoryMapper.toCategory(request);

        // Assert
        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo("Sports");
        assertThat(category.getDescription()).isEqualTo("Sporting goods");
        assertThat(category.getImageUrl()).isEqualTo("https://example.com/sports.jpg");
    }

    @Test
    @DisplayName("Should map Category entity with parent category to CategoryResponse DTO")
    void shouldMapCategoryWithParentToCategoryResponse() {
        // Arrange
        Category parent = Category.builder().id(10L).name("Parent").build();
        LocalDateTime now = LocalDateTime.now();

        Category category = Category.builder()
                .id(20L)
                .name("SubSports")
                .description("Sub sports category")
                .imageUrl("https://example.com/subsports.jpg")
                .parentCategory(parent)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Act
        CategoryResponse response = CategoryMapper.toResponse(category);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(20L);
        assertThat(response.getName()).isEqualTo("SubSports");
        assertThat(response.getDescription()).isEqualTo("Sub sports category");
        assertThat(response.getImageUrl()).isEqualTo("https://example.com/subsports.jpg");
        assertThat(response.getParentCategoryId()).isEqualTo(10L);
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should map Category entity without parent category to CategoryResponse DTO")
    void shouldMapCategoryWithoutParentToCategoryResponse() {
        // Arrange
        Category category = Category.builder()
                .id(1L)
                .name("RootCategory")
                .parentCategory(null)
                .build();

        // Act
        CategoryResponse response = CategoryMapper.toResponse(category);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getParentCategoryId()).isNull();
    }

    @Test
    @DisplayName("Should instantiate private constructor via reflection for test coverage")
    void shouldInstantiatePrivateConstructorForCoverage() throws Exception {
        // Arrange
        Constructor<CategoryMapper> constructor = CategoryMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // Act
        CategoryMapper instance = constructor.newInstance();

        // Assert
        assertThat(instance).isNotNull();
    }
}