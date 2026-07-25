package com.amazonscale.order.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderCancellationExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "Order cannot be cancelled";
        OrderCancellationException ex = new OrderCancellationException(message);
        assertThat(ex.getMessage()).isEqualTo(message);
    }
}
