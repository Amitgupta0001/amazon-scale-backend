package com.amazonscale.user.mapper;

import com.amazonscale.user.dto.UserRequest;
import com.amazonscale.user.dto.UserResponse;
import com.amazonscale.user.enums.Role;
import com.amazonscale.user.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toEntity() {
        // Arrange
        UserRequest request = UserRequest.builder()
                .firstName("Bob")
                .lastName("Marley")
                .email("bob@example.com")
                .password("pass1234")
                .role(Role.SELLER)
                .build();

        // Act
        User user = UserMapper.toEntity(request);

        // Assert
        assertNotNull(user);
        assertEquals("Bob", user.getFirstName());
        assertEquals("Marley", user.getLastName());
        assertEquals("bob@example.com", user.getEmail());
        assertEquals("pass1234", user.getPassword());
        assertEquals(Role.SELLER, user.getRole());
    }

    @Test
    void toResponse() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(10L)
                .firstName("Bob")
                .lastName("Marley")
                .email("bob@example.com")
                .role(Role.SELLER)
                .enabled(true)
                .createdAt(now)
                .build();

        // Act
        UserResponse response = UserMapper.toResponse(user);

        // Assert
        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Bob", response.getFirstName());
        assertEquals("Marley", response.getLastName());
        assertEquals("bob@example.com", response.getEmail());
        assertEquals(Role.SELLER, response.getRole());
        assertTrue(response.isEnabled());
        assertEquals(now, response.getCreatedAt());
    }
}