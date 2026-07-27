package com.amazonscale.category.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidCategoryHierarchyExceptionTest {

    @Test
    @DisplayName("Should create InvalidCategoryHierarchyException with default message")
    void shouldCreateInvalidCategoryHierarchyExceptionWithCorrectMessage() {
        // Arrange & Act
        InvalidCategoryHierarchyException exception = new InvalidCategoryHierarchyException();

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("A category cannot be its own parent.");
    }

    @Test
    @DisplayName("Should build InvalidCategoryHierarchyException using Builder pattern")
    void shouldBuildInvalidCategoryHierarchyExceptionUsingBuilder() {
        // Arrange & Act
        InvalidCategoryHierarchyException exception = InvalidCategoryHierarchyException.builder().build();

        // Assert
        assertThat(exception).isNotNull();
    }
}