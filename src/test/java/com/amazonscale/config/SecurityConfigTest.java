package com.amazonscale.config;

import com.amazonscale.security.CustomUserDetailsService;
import com.amazonscale.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void testAuthenticationProviderBean() {
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AuthenticationProvider provider = securityConfig.authenticationProvider(passwordEncoder);

        assertThat(provider).isNotNull();
    }

    @Test
    void testAuthenticationManagerBean() throws Exception {
        AuthenticationConfiguration configuration = mock(AuthenticationConfiguration.class);
        AuthenticationManager expectedManager = mock(AuthenticationManager.class);
        when(configuration.getAuthenticationManager()).thenReturn(expectedManager);

        AuthenticationManager manager = securityConfig.authenticationManager(configuration);

        assertThat(manager).isEqualTo(expectedManager);
    }
}
