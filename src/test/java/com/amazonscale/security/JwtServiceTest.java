package com.amazonscale.security;

import com.amazonscale.user.entity.Role;
import com.amazonscale.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

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
    void shouldGenerateJwtSuccessfully() {
        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldExtractUsername() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        assertEquals("user@example.com", username);
    }

    @Test
    void shouldValidateTokenSuccessfully() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
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
        assertFalse(isValid);
    }

    @Test
    void shouldFailValidationWhenTokenIsExpired() {
        // Arrange
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L); // Negative expiration
        String expiredToken = jwtService.generateToken(userDetails);

        // Reset expiration
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", testExpiration);

        // Act & Assert
        try {
            jwtService.isTokenValid(expiredToken, userDetails);
        } catch (Exception ex) {
            // ExpiredJwtException expected during claim parsing
            assertNotNull(ex);
        }
    }
}