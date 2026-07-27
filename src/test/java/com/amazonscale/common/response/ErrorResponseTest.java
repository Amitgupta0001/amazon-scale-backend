package com.amazonscale.common.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    @DisplayName("Should correctly set and get fields using no-args constructor and setters")
    void shouldSetAndGetFieldsCorrectlyWhenUsingNoArgsConstructor() {
        // Arrange
        ErrorResponse response = new ErrorResponse();
        LocalDateTime timestamp = LocalDateTime.of(2026, 7, 27, 12, 0, 0);

        // Act
        response.setTimestamp(timestamp);
        response.setStatus(404);
        response.setError("Not Found");
        response.setMessage("Resource not found");
        response.setPath("/api/v1/resource");

        // Assert
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getError()).isEqualTo("Not Found");
        assertThat(response.getMessage()).isEqualTo("Resource not found");
        assertThat(response.getPath()).isEqualTo("/api/v1/resource");
    }

    @Test
    @DisplayName("Should correctly initialize fields using all-args constructor")
    void shouldInitializeFieldsCorrectlyWhenUsingAllArgsConstructor() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.of(2026, 7, 27, 12, 0, 0);

        // Act
        ErrorResponse response = new ErrorResponse(
                timestamp,
                400,
                "Bad Request",
                "Invalid parameters",
                "/api/v1/test"
        );

        // Assert
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getError()).isEqualTo("Bad Request");
        assertThat(response.getMessage()).isEqualTo("Invalid parameters");
        assertThat(response.getPath()).isEqualTo("/api/v1/test");
    }

    @Test
    @DisplayName("Should correctly build ErrorResponse using Builder pattern")
    void shouldBuildErrorResponseSuccessfullyWhenUsingBuilder() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.of(2026, 7, 27, 12, 0, 0);

        // Act
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(timestamp)
                .status(500)
                .error("Internal Server Error")
                .message("An unexpected error occurred.")
                .path("/api/v1/orders")
                .build();

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTimestamp()).isEqualTo(timestamp);
        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getError()).isEqualTo("Internal Server Error");
        assertThat(response.getMessage()).isEqualTo("An unexpected error occurred.");
        assertThat(response.getPath()).isEqualTo("/api/v1/orders");
    }

    @Test
    @DisplayName("Should handle null and default values gracefully")
    void shouldHandleNullValuesInFields() {
        // Arrange & Act
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(null)
                .status(0)
                .error(null)
                .message(null)
                .path(null)
                .build();

        // Assert
        assertThat(response.getTimestamp()).isNull();
        assertThat(response.getStatus()).isEqualTo(0);
        assertThat(response.getError()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getPath()).isNull();
    }
}
