package com.amazonscale.category.repository;

import com.amazonscale.category.entity.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryRepositoryTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Should return true for existsByName when category with name exists")
    void shouldReturnTrueWhenCategoryExistsByName() {
        // Arrange
        when(categoryRepository.existsByName("Books")).thenReturn(true);

        // Act
        boolean exists = categoryRepository.existsByName("Books");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false for existsByName when category name does not exist")
    void shouldReturnFalseWhenCategoryDoesNotExistByName() {
        // Arrange
        when(categoryRepository.existsByName("NonExistent")).thenReturn(false);

        // Act
        boolean exists = categoryRepository.existsByName("NonExistent");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should find Category by name when category exists")
    void shouldFindCategoryByName() {
        // Arrange
        Category category = Category.builder()
                .name("Computers")
                .description("Laptops and Desktops")
                .build();
        when(categoryRepository.findByName("Computers")).thenReturn(Optional.of(category));

        // Act
        Optional<Category> found = categoryRepository.findByName("Computers");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Computers");
    }

    @Test
    @DisplayName("Should return empty Optional for findByName when category does not exist")
    void shouldReturnEmptyWhenFindByNameDoesNotExist() {
        // Arrange
        when(categoryRepository.findByName("Gaming")).thenReturn(Optional.empty());

        // Act
        Optional<Category> found = categoryRepository.findByName("Gaming");

        // Assert
        assertThat(found).isEmpty();
    }
}
