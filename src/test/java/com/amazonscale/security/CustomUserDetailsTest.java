package com.amazonscale.security;

import com.amazonscale.user.entity.Role;
import com.amazonscale.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    private User user;
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encoded_password")
                .role(Role.ADMIN)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        customUserDetails = new CustomUserDetails(user);
    }

    @Test
    void shouldReturnCorrectAuthorities() {
        // Act
        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();

        // Assert
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertEquals("ADMIN", authorities.iterator().next().getAuthority());
    }

    @Test
    void shouldReturnCorrectPassword() {
        // Act & Assert
        assertEquals("encoded_password", customUserDetails.getPassword());
    }

    @Test
    void shouldReturnCorrectUsername() {
        // Act & Assert
        assertEquals("john.doe@example.com", customUserDetails.getUsername());
    }

    @Test
    void shouldReturnAccountNonExpiredAsTrue() {
        // Act & Assert
        assertTrue(customUserDetails.isAccountNonExpired());
    }

    @Test
    void shouldReturnAccountNonLockedAsTrue() {
        // Act & Assert
        assertTrue(customUserDetails.isAccountNonLocked());
    }

    @Test
    void shouldReturnCredentialsNonExpiredAsTrue() {
        // Act & Assert
        assertTrue(customUserDetails.isCredentialsNonExpired());
    }

    @Test
    void shouldReturnEnabledStatus() {
        // Act & Assert
        assertTrue(customUserDetails.isEnabled());
    }

    @Test
    void shouldReturnWrappedUser() {
        // Act & Assert
        assertEquals(user, customUserDetails.getUser());
    }
}