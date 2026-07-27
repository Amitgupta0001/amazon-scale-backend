package com.amazonscale.user.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailAlreadyExistsExceptionTest {

    @Test
    @DisplayName("Should create EmailAlreadyExistsException with formatted message containing email")
    void shouldCreateEmailAlreadyExistsExceptionWithCorrectMessage() {
        // Arrange & Act
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException("existing@example.com");

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("User with email 'existing@example.com' already exists.");
    }
}