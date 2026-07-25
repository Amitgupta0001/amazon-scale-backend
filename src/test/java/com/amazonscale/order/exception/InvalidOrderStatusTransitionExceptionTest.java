package com.amazonscale.order.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidOrderStatusTransitionExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "Invalid status transition from PENDING to DELIVERED";
        InvalidOrderStatusTransitionException ex = new InvalidOrderStatusTransitionException(message);
        assertThat(ex.getMessage()).isEqualTo(message);
    }
}
