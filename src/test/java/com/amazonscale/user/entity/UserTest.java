package com.amazonscale.user.entity;

import com.amazonscale.user.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("Should correctly set and get fields using User Builder and getters/setters")
    void shouldBuildUserAndVerifyGettersSetters() {
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
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getPassword()).isEqualTo("encoded_pass");
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should populate timestamps automatically on @PrePersist (onCreate) and @PreUpdate (onUpdate)")
    void shouldPopulateTimestampsOnPrePersistAndPreUpdate() {
        // Arrange
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("jane@example.com");
        user.setPassword("password");
        user.setRole(Role.CUSTOMER);

        // Act - Simulating PrePersist
        user.onCreate();

        // Assert
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();

        LocalDateTime initialUpdatedAt = user.getUpdatedAt();

        // Act - Simulating PreUpdate
        user.onUpdate();

        // Assert
        assertThat(user.getUpdatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isAfterOrEqualTo(initialUpdatedAt);
    }

    @Test
    @DisplayName("Should verify constructors (no-args and all-args)")
    void shouldVerifyConstructors() {
        // Arrange & Act
        User emptyUser = new User();
        User fullUser = new User(5L, "Bob", "Marley", "bob@example.com", "pass", Role.SELLER, true, null, null);

        // Assert
        assertThat(emptyUser.getId()).isNull();
        assertThat(fullUser.getId()).isEqualTo(5L);
        assertThat(fullUser.getFirstName()).isEqualTo("Bob");
        assertThat(fullUser.getLastName()).isEqualTo("Marley");
        assertThat(fullUser.getRole()).isEqualTo(Role.SELLER);
    }
}