package com.amazonscale.security;

import com.amazonscale.user.entity.User;
import com.amazonscale.user.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {

    @Test
    @DisplayName("Should correctly return UserDetails properties when constructed with User entity")
    void shouldReturnCorrectUserDetailsPropertiesWhenConstructedWithUser() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .email("customer@example.com")
                .password("encoded_secret_123")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        // Act
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // Assert
        assertThat(userDetails.getUser()).isEqualTo(user);
        assertThat(userDetails.getUsername()).isEqualTo("customer@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("encoded_secret_123");
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();

        Collection<?> authorities = userDetails.getAuthorities();
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().toString()).isEqualTo("CUSTOMER");
    }

    @Test
    @DisplayName("Should correctly build CustomUserDetails using Lombok Builder")
    void shouldBuildCustomUserDetailsUsingBuilder() {
        // Arrange
        User adminUser = User.builder()
                .id(2L)
                .email("admin@example.com")
                .password("admin_encoded_pass")
                .role(Role.ADMIN)
                .enabled(false)
                .build();

        // Act
        CustomUserDetails userDetails = CustomUserDetails.builder()
                .user(adminUser)
                .build();

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUser()).isEqualTo(adminUser);
        assertThat(userDetails.getUsername()).isEqualTo("admin@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("admin_encoded_pass");
        assertThat(userDetails.isEnabled()).isFalse();

        Collection<?> authorities = userDetails.getAuthorities();
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().toString()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Should map SELLER role correctly to granted authorities")
    void shouldMapSellerRoleToGrantedAuthorities() {
        // Arrange
        User sellerUser = User.builder()
                .id(3L)
                .email("seller@example.com")
                .password("seller_pass")
                .role(Role.SELLER)
                .enabled(true)
                .build();

        // Act
        CustomUserDetails userDetails = new CustomUserDetails(sellerUser);

        // Assert
        Collection<?> authorities = userDetails.getAuthorities();
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().toString()).isEqualTo("SELLER");
    }
}