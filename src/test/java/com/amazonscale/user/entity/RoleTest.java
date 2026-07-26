package com.amazonscale.user.entity;

import com.amazonscale.user.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testRoleEnumValues() {
        // Act & Assert
        assertEquals(3, Role.values().length);
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
        assertEquals(Role.SELLER, Role.valueOf("SELLER"));
        assertEquals(Role.CUSTOMER, Role.valueOf("CUSTOMER"));
    }
}
