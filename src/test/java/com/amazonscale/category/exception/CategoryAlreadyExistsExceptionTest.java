package com.amazonscale.category.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryAlreadyExistsExceptionTest {

    @Test
    @DisplayName("Should create CategoryAlreadyExistsException with formatted message containing category name")
    void shouldCreateCategoryAlreadyExistsExceptionWithCorrectMessage() {
        // Arrange & Act
        CategoryAlreadyExistsException exception = new CategoryAlreadyExistsException("Electronics");

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Category already exists with name: Electronics");
    }
}