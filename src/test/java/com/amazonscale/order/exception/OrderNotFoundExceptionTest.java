package com.amazonscale.order.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderNotFoundExceptionTest {

    @Test
    @DisplayName("Should create OrderNotFoundException with message matching production string format")
    void shouldCreateOrderNotFoundExceptionWithCorrectMessage() {
        // Arrange & Act
        OrderNotFoundException exception = new OrderNotFoundException(100L);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Order not found with id100");
    }
}
