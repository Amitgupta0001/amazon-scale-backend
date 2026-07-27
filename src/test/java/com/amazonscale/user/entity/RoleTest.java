package com.amazonscale.user.entity;

import com.amazonscale.user.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    @Test
    @DisplayName("Should contain correct Role enum constants and allow valueOf mapping")
    void shouldVerifyRoleEnumValues() {
        // Act & Assert
        assertThat(Role.values()).containsExactlyInAnyOrder(Role.ADMIN, Role.SELLER, Role.CUSTOMER);
        assertThat(Role.valueOf("ADMIN")).isEqualTo(Role.ADMIN);
        assertThat(Role.valueOf("SELLER")).isEqualTo(Role.SELLER);
        assertThat(Role.valueOf("CUSTOMER")).isEqualTo(Role.CUSTOMER);
    }
}
