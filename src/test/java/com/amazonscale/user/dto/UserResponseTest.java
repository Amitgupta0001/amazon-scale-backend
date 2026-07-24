package com.amazonscale.user.dto;

import com.amazonscale.user.entity.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserResponseTest {

    @Test
    void testUserResponseBuilderAndGettersSetters() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        UserResponse response = UserResponse.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@example.com")
                .role(Role.ADMIN)
                .enabled(true)
                .createdAt(now)
                .build();

        // Assert
        assertEquals(1L, response.getId());
        assertEquals("Alice", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals("alice@example.com", response.getEmail());
        assertEquals(Role.ADMIN, response.getRole());
        assertTrue(response.isEnabled());
        assertEquals(now, response.getCreatedAt());
    }
}