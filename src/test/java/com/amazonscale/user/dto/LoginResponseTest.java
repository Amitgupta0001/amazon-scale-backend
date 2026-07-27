package com.amazonscale.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginResponseTest {

    @Test
    @DisplayName("Should initialize fields correctly using all-args constructor and allow modification via setters")
    void shouldInitializeAndModifyFieldsCorrectly() {
        // Act
        LoginResponse response = new LoginResponse("token_abc", "Bearer");

        // Assert
        assertThat(response.getAccessToken()).isEqualTo("token_abc");
        assertThat(response.getTokenType()).isEqualTo("Bearer");

        // Act - Modify via setters
        response.setAccessToken("token_xyz");
        response.setTokenType("Basic");

        // Assert
        assertThat(response.getAccessToken()).isEqualTo("token_xyz");
        assertThat(response.getTokenType()).isEqualTo("Basic");
    }

    @Test
    @DisplayName("Should initialize null fields using no-args constructor")
    void shouldInitializeNullFieldsWithNoArgsConstructor() {
        // Act
        LoginResponse response = new LoginResponse();

        // Assert
        assertThat(response.getAccessToken()).isNull();
        assertThat(response.getTokenType()).isNull();
    }

    @Test
    @DisplayName("Should build LoginResponse successfully using Builder pattern")
    void shouldBuildLoginResponseUsingBuilder() {
        // Act
        LoginResponse response = LoginResponse.builder()
                .accessToken("jwt_token_value")
                .tokenType("Bearer")
                .build();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("jwt_token_value");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }
}