package com.amazonscale.user.entity;

import com.amazonscale.user.enums.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserBuilderAndGettersSetters() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("encoded_pass")
                .role(Role.ADMIN)
                .enabled(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertEquals(1L, user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("encoded_pass", user.getPassword());
        assertEquals(Role.ADMIN, user.getRole());
        assertTrue(user.isEnabled());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    void testPrePersistAndPreUpdate() {
        // Arrange
        User user = new User(1L, "A", "B", "a@b.com", "pass", Role.CUSTOMER, true, null, null);

        // Act
        user.onCreate();

        // Assert
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());

        user.onUpdate();
        assertNotNull(user.getUpdatedAt());
    }
}