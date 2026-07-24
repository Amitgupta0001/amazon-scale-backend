package com.amazonscale.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseTest {

    @Test
    void testConstructorsAndGettersSetters() {
        // Act
        LoginResponse response = new LoginResponse("token_abc", "Bearer");

        // Assert
        assertEquals("token_abc", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());

        response.setAccessToken("token_xyz");
        response.setTokenType("Basic");

        assertEquals("token_xyz", response.getAccessToken());
        assertEquals("Basic", response.getTokenType());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        LoginResponse response = new LoginResponse();
        assertNull(response.getAccessToken());
        assertNull(response.getTokenType());
    }
}