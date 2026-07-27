package com.amazonscale.user.service.impl;

import com.amazonscale.user.dto.UserRequest;
import com.amazonscale.user.dto.UserResponse;
import com.amazonscale.user.entity.User;
import com.amazonscale.user.enums.Role;
import com.amazonscale.user.exception.EmailAlreadyExistsException;
import com.amazonscale.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest userRequest;
    private User user;

    @BeforeEach
    void setUp() {
        userRequest = UserRequest.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@example.com")
                .password("rawPassword123")
                .build();

        user = User.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@example.com")
                .password("encodedPassword123")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("Should successfully register a new user when email is available")
    void shouldRegisterUserSuccessfully() {
        // Arrange
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponse response = userService.register(userRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("Jane");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getEmail()).isEqualTo("jane@example.com");
        assertThat(response.getRole()).isEqualTo(Role.CUSTOMER);
        assertThat(response.isEnabled()).isTrue();

        verify(userRepository).existsByEmail("jane@example.com");
        verify(passwordEncoder).encode("rawPassword123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email is already registered")
    void shouldThrowEmailAlreadyExistsExceptionWhenEmailIsTaken() {
        // Arrange
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.register(userRequest))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("jane@example.com");

        verify(userRepository).existsByEmail("jane@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should build UserServiceImpl using Builder pattern")
    void shouldBuildUserServiceImplUsingBuilder() {
        // Arrange & Act
        UserServiceImpl service = UserServiceImpl.builder()
                .userRepository(userRepository)
                .passwordEncoder(passwordEncoder)
                .build();

        // Assert
        assertThat(service).isNotNull();
    }
}