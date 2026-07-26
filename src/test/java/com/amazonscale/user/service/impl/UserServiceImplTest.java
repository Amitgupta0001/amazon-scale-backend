package com.amazonscale.user.service.impl;

import com.amazonscale.user.dto.UserRequest;
import com.amazonscale.user.dto.UserResponse;
import com.amazonscale.user.enums.Role;
import com.amazonscale.user.entity.User;
import com.amazonscale.user.exception.EmailAlreadyExistsException;
import com.amazonscale.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
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
    void registerUserSuccess() {
        // Arrange
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponse response = userService.register(userRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("jane@example.com", response.getEmail());
        assertEquals(Role.CUSTOMER, response.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowEmailAlreadyExistsExceptionWhenEmailIsTaken() {
        // Arrange
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> userService.register(userRequest));
        verify(userRepository, never()).save(any(User.class));
    }
}