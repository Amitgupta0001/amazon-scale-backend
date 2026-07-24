package com.amazonscale.category.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidCategoryHierarchyExceptionTest {

    @Test
    void shouldCreateExceptionWithDefaultMessage() {
        // Act
        InvalidCategoryHierarchyException exception = new InvalidCategoryHierarchyException();

        // Assert
        assertNotNull(exception);
        assertEquals("A category cannot be its own parent.", exception.getMessage());
    }
}