package com.amazonscale.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityBeansConfigTest {

    @Test
    void testPasswordEncoderBean() {
        SecurityBeansConfig config = new SecurityBeansConfig();
        PasswordEncoder encoder = config.passwordEncoder();

        assertThat(encoder).isNotNull();

        String rawPassword = "myPassword123";
        String encoded = encoder.encode(rawPassword);

        assertThat(encoded).isNotEqualTo(rawPassword);
        assertThat(encoder.matches(rawPassword, encoded)).isTrue();
    }
}
