package com.amazonscale.order.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "Order not found with id100";
        Long orderId = 100L;
        OrderNotFoundException ex = new OrderNotFoundException(orderId);
        assertThat(ex.getMessage()).isEqualTo(message);
    }
}
