package com.amazonscale.category.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryNotFoundExceptionTest {

    @Test
    @DisplayName("Should create CategoryNotFoundException with formatted message containing ID")
    void shouldCreateCategoryNotFoundExceptionWithCorrectMessage() {
        // Arrange & Act
        CategoryNotFoundException exception = new CategoryNotFoundException(100L);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Category not found with id: 100");
    }
}