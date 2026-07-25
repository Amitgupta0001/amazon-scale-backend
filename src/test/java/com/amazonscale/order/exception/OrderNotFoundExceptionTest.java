package com.amazonscale.order.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "Order not found with id: 100";
        OrderNotFoundException ex = new OrderNotFoundException(message);
        assertThat(ex.getMessage()).isEqualTo(message);
    }
}
