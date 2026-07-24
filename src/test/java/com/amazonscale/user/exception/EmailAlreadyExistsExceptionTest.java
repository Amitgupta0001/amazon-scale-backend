package com.amazonscale.user.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailAlreadyExistsExceptionTest {

    @Test
    void shouldCreateExceptionWithEmailMessage() {
        // Act
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException("existing@example.com");

        // Assert
        assertNotNull(exception);
        assertEquals("User with email 'existing@example.com' already exists.", exception.getMessage());
    }
}