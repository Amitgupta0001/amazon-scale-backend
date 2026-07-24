package com.amazonscale.category.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryAlreadyExistsExceptionTest {

    @Test
    void shouldCreateExceptionWithName() {
        // Act
        CategoryAlreadyExistsException exception = new CategoryAlreadyExistsException("Gadgets");

        // Assert
        assertNotNull(exception);
        assertEquals("Category already exists with name: Gadgets", exception.getMessage());
    }
}