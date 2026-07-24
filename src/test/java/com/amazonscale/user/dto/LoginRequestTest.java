package com.amazonscale.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        LoginRequest request = new LoginRequest();

        // Act
        request.setEmail("user@example.com");
        request.setPassword("secret123");

        // Assert
        assertEquals("user@example.com", request.getEmail());
        assertEquals("secret123", request.getPassword());
    }
}