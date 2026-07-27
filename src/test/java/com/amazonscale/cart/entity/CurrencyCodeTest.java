package com.amazonscale.cart.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CurrencyCodeTest {

    @Test
    @DisplayName("Should contain expected currency code values")
    void shouldContainExpectedEnumValues() {
        // Arrange & Act
        CurrencyCode[] values = CurrencyCode.values();

        // Assert
        assertThat(values).containsExactlyInAnyOrder(
                CurrencyCode.INR,
                CurrencyCode.USD,
                CurrencyCode.EUR,
                CurrencyCode.GBP
        );
    }

    @Test
    @DisplayName("Should valueOf resolve enum name correctly")
    void shouldResolveValueOfCorrectly() {
        // Act & Assert
        assertThat(CurrencyCode.valueOf("INR")).isEqualTo(CurrencyCode.INR);
        assertThat(CurrencyCode.valueOf("USD")).isEqualTo(CurrencyCode.USD);
    }
}
