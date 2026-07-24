package com.amazonscale.security;

import com.amazonscale.user.entity.Role;
import com.amazonscale.user.entity.User;
import com.amazonscale.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@example.com")
                .password("secret")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
    }

    @Test
    void loadUserByUsernameSuccess() {
        // Arrange
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("alice@example.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals("alice@example.com", userDetails.getUsername());
        assertEquals("secret", userDetails.getPassword());
        verify(userRepository, times(1)).findByEmail("alice@example.com");
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("unknown@example.com")
        );

        assertEquals("User not found with email: unknown@example.com", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("unknown@example.com");
    }
}