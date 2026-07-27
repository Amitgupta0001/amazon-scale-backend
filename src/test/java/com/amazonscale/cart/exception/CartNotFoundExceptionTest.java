package com.amazonscale.cart.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CartNotFoundExceptionTest {

    @Test
    @DisplayName("Should create CartNotFoundException with formatted message containing user ID")
    void shouldCreateCartNotFoundExceptionWithCorrectMessage() {
        // Arrange & Act
        CartNotFoundException exception = new CartNotFoundException(5L);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Cart not found with User Id: 5");
    }
}
