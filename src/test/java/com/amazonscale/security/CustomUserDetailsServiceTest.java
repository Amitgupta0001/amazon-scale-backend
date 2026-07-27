package com.amazonscale.security;

import com.amazonscale.user.entity.User;
import com.amazonscale.user.enums.Role;
import com.amazonscale.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Should successfully load UserDetails when user exists by email")
    void shouldLoadUserByUsernameSuccessfullyWhenUserExists() {
        // Arrange
        String email = "user@example.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .password("password123")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo("password123");
        assertThat(userDetails.isEnabled()).isTrue();

        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user is not found by email")
    void shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        // Arrange
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with email: " + email);

        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Should build CustomUserDetailsService using Builder pattern")
    void shouldBuildCustomUserDetailsServiceUsingBuilder() {
        // Arrange & Act
        CustomUserDetailsService service = CustomUserDetailsService.builder()
                .userRepository(userRepository)
                .build();

        // Assert
        assertThat(service).isNotNull();
    }
}