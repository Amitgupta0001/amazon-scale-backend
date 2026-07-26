package com.amazonscale.security;

import com.amazonscale.user.enums.Role;
import com.amazonscale.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {

    @Test
    void testCustomUserDetailsProperties() {
        User user = User.builder()
                .id(1L)
                .email("john@example.com")
                .password("encoded_password")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(user);

        assertThat(userDetails.getUser()).isEqualTo(user);
        assertThat(userDetails.getUsername()).isEqualTo("john@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("encoded_password");
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("CUSTOMER");
    }

    @Test
    void testCustomUserDetailsBuilder() {
        User user = User.builder()
                .id(2L)
                .email("admin@example.com")
                .password("admin_pass")
                .role(Role.ADMIN)
                .enabled(false)
                .build();

        CustomUserDetails userDetails = CustomUserDetails.builder()
                .user(user)
                .build();

        assertThat(userDetails.getUsername()).isEqualTo("admin@example.com");
        assertThat(userDetails.isEnabled()).isFalse();
    }
}