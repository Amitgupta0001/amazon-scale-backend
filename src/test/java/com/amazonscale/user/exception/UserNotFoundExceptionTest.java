package com.amazonscale.user.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {
        UserNotFoundException ex = new UserNotFoundException(42L);
        assertThat(ex.getMessage()).contains("42");
    }
}
