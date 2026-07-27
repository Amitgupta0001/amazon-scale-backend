package com.amazonscale.cart.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CartItemNotFoundExceptionTest {

    @Test
    @DisplayName("Should create CartItemNotFoundException with formatted message containing product ID")
    void shouldCreateCartItemNotFoundExceptionWithCorrectMessage() {
        // Arrange & Act
        CartItemNotFoundException exception = new CartItemNotFoundException(10L);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Could not find cart item with Product ID: 10");
    }
}
