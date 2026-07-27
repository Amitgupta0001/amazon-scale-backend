package com.amazonscale.security;

import com.amazonscale.user.entity.User;
import com.amazonscale.user.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;
    private CustomUserDetails userDetails;

    // 256-bit Base64 encoded key for testing
    private final String testSecret = "dGhpcy1pcy1hLXZlcnktc2VjdXJlLTI1Ni1iaXQtc2VjcmV0LWtleS1mb3ItdGVzdGluZw==";
    private final Long testExpiration = 3600000L; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(testSecret, testExpiration);

        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .password("password")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
        userDetails = new CustomUserDetails(user);
    }

    @Test
    @DisplayName("Should generate valid non-empty JWT token for UserDetails")
    void shouldGenerateJwtSuccessfully() {
        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Should extract username from valid JWT token")
    void shouldExtractUsername() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        assertThat(username).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("Should extract arbitrary claims using custom claims resolver")
    void shouldExtractClaimUsingCustomResolver() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);
        Date issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);

        // Assert
        assertThat(expiration).isNotNull().isAfter(new Date());
        assertThat(issuedAt).isNotNull().isBeforeOrEqualTo(new Date());
    }

    @Test
    @DisplayName("Should validate token successfully when token is valid and user matches")
    void shouldValidateTokenSuccessfully() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should fail validation when token username does not match user details")
    void shouldFailValidationWhenUsernameDoesNotMatch() {
        // Arrange
        String token = jwtService.generateToken(userDetails);
        User differentUser = User.builder()
                .id(2L)
                .email("other@example.com")
                .password("password")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();
        CustomUserDetails differentUserDetails = new CustomUserDetails(differentUser);

        // Act
        boolean isValid = jwtService.isTokenValid(token, differentUserDetails);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should throw ExpiredJwtException when validating an expired token")
    void shouldThrowExpiredJwtExceptionWhenTokenIsExpired() {
        // Arrange
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L); // Negative expiration
        String expiredToken = jwtService.generateToken(userDetails);

        // Reset expiration for parsing check
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", testExpiration);

        // Act & Assert
        assertThatThrownBy(() -> jwtService.isTokenValid(expiredToken, userDetails))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("Should throw MalformedJwtException when token structure is invalid")
    void shouldThrowMalformedJwtExceptionWhenTokenIsMalformed() {
        // Arrange
        String invalidToken = "invalid.jwt.token";

        // Act & Assert
        assertThatThrownBy(() -> jwtService.extractUsername(invalidToken))
                .isInstanceOf(MalformedJwtException.class);
    }
}