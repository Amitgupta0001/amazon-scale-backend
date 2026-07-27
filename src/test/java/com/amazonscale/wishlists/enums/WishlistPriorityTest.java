package com.amazonscale.wishlists.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WishlistPriorityTest {

    @Test
    @DisplayName("Should contain all expected WishlistPriority enum values")
    void shouldContainExpectedEnumValues() {
        // Arrange & Act
        WishlistPriority[] values = WishlistPriority.values();

        // Assert
        assertThat(values).containsExactlyInAnyOrder(
                WishlistPriority.LOW,
                WishlistPriority.MEDIUM,
                WishlistPriority.HIGH
        );
    }

    @Test
    @DisplayName("Should resolve valueOf correctly")
    void shouldResolveValueOfCorrectly() {
        // Act & Assert
        assertThat(WishlistPriority.valueOf("LOW")).isEqualTo(WishlistPriority.LOW);
        assertThat(WishlistPriority.valueOf("MEDIUM")).isEqualTo(WishlistPriority.MEDIUM);
        assertThat(WishlistPriority.valueOf("HIGH")).isEqualTo(WishlistPriority.HIGH);
    }
}
