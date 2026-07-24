package com.amazonscale.category.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithId() {
        // Act
        CategoryNotFoundException exception = new CategoryNotFoundException(15L);

        // Assert
        assertNotNull(exception);
        assertEquals("Category not found with id: 15", exception.getMessage());
    }
}