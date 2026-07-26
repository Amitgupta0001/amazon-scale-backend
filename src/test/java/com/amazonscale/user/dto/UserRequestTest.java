package com.amazonscale.user.dto;

import com.amazonscale.user.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRequestTest {

    @Test
    void testUserRequestBuilderAndGettersSetters() {
        // Act
        UserRequest request = UserRequest.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@example.com")
                .password("password123")
                .role(Role.ADMIN)
                .build();

        // Assert
        assertEquals("Alice", request.getFirstName());
        assertEquals("Smith", request.getLastName());
        assertEquals("alice@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
        assertEquals(Role.ADMIN, request.getRole());
    }
}