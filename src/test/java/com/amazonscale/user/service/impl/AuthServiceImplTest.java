package com.amazonscale.user.service.impl;

import com.amazonscale.security.CustomUserDetails;
import com.amazonscale.security.JwtService;
import com.amazonscale.user.dto.LoginRequest;
import com.amazonscale.user.dto.LoginResponse;
import com.amazonscale.user.entity.User;
import com.amazonscale.user.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
        userDetails = new CustomUserDetails(user);
    }

    @Test
    @DisplayName("Should successfully authenticate and return LoginResponse on valid credentials")
    void shouldLoginSuccessfullyWhenCredentialsAreValid() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("generated_jwt_token");

        // Act
        LoginResponse response = authService.login(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("generated_jwt_token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when authentication manager fails")
    void shouldThrowExceptionWhenAuthenticationFails() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid credentials");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Should build AuthServiceImpl using Builder pattern")
    void shouldBuildAuthServiceImplUsingBuilder() {
        // Arrange & Act
        AuthServiceImpl service = AuthServiceImpl.builder()
                .authenticationManager(authenticationManager)
                .jwtService(jwtService)
                .build();

        // Assert
        assertThat(service).isNotNull();
    }
}