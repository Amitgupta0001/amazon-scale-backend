package com.amazonscale.common.response;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void testNoArgsConstructorAndSettersGetters() {
        // Arrange
        ErrorResponse response = new ErrorResponse();
        LocalDateTime now = LocalDateTime.now();

        // Act
        response.setTimestamp(now);
        response.setStatus(404);
        response.setError("Not Found");
        response.setMessage("Resource not found");
        response.setPath("/api/v1/resource");

        // Assert
        assertEquals(now, response.getTimestamp());
        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getError());
        assertEquals("Resource not found", response.getMessage());
        assertEquals("/api/v1/resource", response.getPath());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        ErrorResponse response = new ErrorResponse(now, 400, "Bad Request", "Invalid input", "/api/v1/test");

        // Assert
        assertEquals(now, response.getTimestamp());
        assertEquals(400, response.getStatus());
        assertEquals("Bad Request", response.getError());
        assertEquals("Invalid input", response.getMessage());
        assertEquals("/api/v1/test", response.getPath());
    }
}
