package com.amazonscale.user.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserNotFoundExceptionTest {

    @Test
    @DisplayName("Should create UserNotFoundException with formatted message containing userId")
    void shouldCreateUserNotFoundExceptionWithCorrectMessage() {
        // Arrange & Act
        UserNotFoundException ex = new UserNotFoundException(42L);

        // Assert
        assertThat(ex).isNotNull();
        assertThat(ex.getMessage()).isEqualTo("User 42 not found");
    }
}
