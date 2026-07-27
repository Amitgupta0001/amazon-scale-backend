package com.amazonscale.user.repository;

import com.amazonscale.user.entity.User;
import com.amazonscale.user.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Should find user by email when user exists")
    void shouldFindUserByEmailWhenUserExists() {
        // Arrange
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encoded_pass")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        // Act
        Optional<User> foundUser = userRepository.findByEmail("john.doe@example.com");

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(foundUser.get().getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should return empty Optional when user with given email does not exist")
    void shouldReturnEmptyWhenUserDoesNotExistByEmail() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Should return true for existsByEmail when user exists with email")
    void shouldReturnTrueWhenExistsByEmail() {
        // Arrange
        when(userRepository.existsByEmail("alice.smith@example.com")).thenReturn(true);

        // Act
        boolean exists = userRepository.existsByEmail("alice.smith@example.com");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false for existsByEmail when user does not exist with email")
    void shouldReturnFalseWhenNotExistsByEmail() {
        // Arrange
        when(userRepository.existsByEmail("nobody@example.com")).thenReturn(false);

        // Act
        boolean exists = userRepository.existsByEmail("nobody@example.com");

        // Assert
        assertThat(exists).isFalse();
    }
}
