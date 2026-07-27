package com.amazonscale.security;

import com.amazonscale.user.entity.User;
import com.amazonscale.user.enums.Role;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should pass through filter chain without authentication when Authorization header is missing")
    void shouldPassThroughWhenAuthorizationHeaderIsMissing() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should pass through filter chain when Authorization header does not start with Bearer")
    void shouldPassThroughWhenAuthorizationHeaderDoesNotStartWithBearer() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basic 1234567890");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should authenticate user and set SecurityContext when valid Bearer token is provided")
    void shouldAuthenticateUserWhenValidTokenIsProvided() throws ServletException, IOException {
        // Arrange
        String token = "valid_token_str";
        User user = User.builder().id(1L).email("user@example.com").role(Role.CUSTOMER).enabled(true).build();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn("user@example.com");
        when(customUserDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("Should not authenticate user if token is invalid according to JwtService")
    void shouldNotAuthenticateUserWhenTokenIsInvalid() throws ServletException, IOException {
        // Arrange
        String token = "invalid_token_str";
        User user = User.builder().id(1L).email("user@example.com").role(Role.CUSTOMER).enabled(true).build();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn("user@example.com");
        when(customUserDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should not re-authenticate user if SecurityContext already contains an Authentication")
    void shouldNotReauthenticateIfSecurityContextAlreadyHasAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "valid_token_str";
        User existingUser = User.builder().id(2L).email("existing@example.com").role(Role.ADMIN).enabled(true).build();
        CustomUserDetails existingUserDetails = new CustomUserDetails(existingUser);
        UsernamePasswordAuthenticationToken existingAuth = new UsernamePasswordAuthenticationToken(
                existingUserDetails, null, existingUserDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn("user@example.com");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("existing@example.com");
    }

    @Test
    @DisplayName("Should catch JwtException and safely pass through filter chain")
    void shouldCatchJwtExceptionAndPassThrough() throws ServletException, IOException {
        // Arrange
        String token = "malformed_token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenThrow(new JwtException("Invalid token format"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should catch IllegalArgumentException and safely pass through filter chain")
    void shouldCatchIllegalArgumentExceptionAndPassThrough() throws ServletException, IOException {
        // Arrange
        String token = "empty_token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenThrow(new IllegalArgumentException("Token claims string is empty"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Should build JwtAuthenticationFilter using Builder pattern")
    void shouldBuildJwtAuthenticationFilterUsingBuilder() {
        // Arrange & Act
        JwtAuthenticationFilter filter = JwtAuthenticationFilter.builder()
                .jwtService(jwtService)
                .customUserDetailsService(customUserDetailsService)
                .build();

        // Assert
        assertThat(filter).isNotNull();
    }
}