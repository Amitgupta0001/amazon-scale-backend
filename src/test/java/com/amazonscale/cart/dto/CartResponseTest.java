package com.amazonscale.cart.dto;

import com.amazonscale.cart.entity.CurrencyCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CartResponseTest {

    @Test
    @DisplayName("Should build CartResponse using Builder and verify getters/setters")
    void shouldBuildCartResponseAndVerifyGettersSetters() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        CartItemResponse item = CartItemResponse.builder()
                .cartItemId(1L)
                .productName("Keyboard")
                .quantity(1)
                .subtotal(new BigDecimal("49.99"))
                .build();

        // Act
        CartResponse response = CartResponse.builder()
                .cartId(100L)
                .userId(5L)
                .items(List.of(item))
                .totalItems(1)
                .totalAmount(new BigDecimal("49.99"))
                .updatedAt(now)
                .currency(CurrencyCode.INR)
                .build();

        // Assert
        assertThat(response.getCartId()).isEqualTo(100L);
        assertThat(response.getUserId()).isEqualTo(5L);
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getTotalItems()).isEqualTo(1);
        assertThat(response.getTotalAmount()).isEqualTo(new BigDecimal("49.99"));
        assertThat(response.getUpdatedAt()).isEqualTo(now);
        assertThat(response.getCurrency()).isEqualTo(CurrencyCode.INR);
    }

    @Test
    @DisplayName("Should test no-args and all-args constructors")
    void shouldTestConstructors() {
        // Arrange & Act
        CartResponse empty = new CartResponse();
        CartResponse full = new CartResponse(1L, 2L, List.of(), 0, BigDecimal.ZERO, null, CurrencyCode.USD);

        // Assert
        assertThat(empty.getCartId()).isNull();
        assertThat(full.getCartId()).isEqualTo(1L);
        assertThat(full.getCurrency()).isEqualTo(CurrencyCode.USD);
    }
}
