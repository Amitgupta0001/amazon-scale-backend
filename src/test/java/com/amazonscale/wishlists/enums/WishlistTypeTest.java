package com.amazonscale.wishlists.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WishlistTypeTest {

    @Test
    @DisplayName("Should contain all expected WishlistType enum values")
    void shouldContainExpectedEnumValues() {
        // Arrange & Act
        WishlistType[] values = WishlistType.values();

        // Assert
        assertThat(values).containsExactlyInAnyOrder(
                WishlistType.DEFAULT,
                WishlistType.CUSTOM
        );
    }

    @Test
    @DisplayName("Should resolve valueOf correctly")
    void shouldResolveValueOfCorrectly() {
        // Act & Assert
        assertThat(WishlistType.valueOf("DEFAULT")).isEqualTo(WishlistType.DEFAULT);
        assertThat(WishlistType.valueOf("CUSTOM")).isEqualTo(WishlistType.CUSTOM);
    }
}
