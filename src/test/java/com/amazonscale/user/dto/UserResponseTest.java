package com.amazonscale.user.dto;

import com.amazonscale.user.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseTest {

    @Test
    @DisplayName("Should correctly set and get fields using UserResponse Builder")
    void shouldBuildUserResponseAndVerifyGettersSetters() {
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
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("Alice");
        assertThat(response.getLastName()).isEqualTo("Smith");
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
        assertThat(response.getRole()).isEqualTo(Role.ADMIN);
        assertThat(response.isEnabled()).isTrue();
        assertThat(response.getCreatedAt()).isEqualTo(now);

        // Act - Setters
        response.setId(2L);
        response.setFirstName("Bob");
        response.setLastName("Jones");
        response.setEmail("bob@example.com");
        response.setRole(Role.CUSTOMER);
        response.setEnabled(false);

        // Assert
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getFirstName()).isEqualTo("Bob");
        assertThat(response.getLastName()).isEqualTo("Jones");
        assertThat(response.getEmail()).isEqualTo("bob@example.com");
        assertThat(response.getRole()).isEqualTo(Role.CUSTOMER);
        assertThat(response.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Should initialize fields using no-args and all-args constructors")
    void shouldInitializeConstructors() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        UserResponse response1 = new UserResponse();
        UserResponse response2 = new UserResponse(10L, "John", "Doe", "john@example.com", Role.SELLER, true, now);

        // Assert
        assertThat(response1.getId()).isNull();
        assertThat(response2.getId()).isEqualTo(10L);
        assertThat(response2.getFirstName()).isEqualTo("John");
        assertThat(response2.getLastName()).isEqualTo("Doe");
        assertThat(response2.getEmail()).isEqualTo("john@example.com");
        assertThat(response2.getRole()).isEqualTo(Role.SELLER);
        assertThat(response2.isEnabled()).isTrue();
        assertThat(response2.getCreatedAt()).isEqualTo(now);
    }
}