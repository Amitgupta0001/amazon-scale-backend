package com.amazonscale.cart.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CartItemResponseTest {

    @Test
    @DisplayName("Should build CartItemResponse using Builder and verify getters/setters")
    void shouldBuildCartItemResponseAndVerifyGettersSetters() {
        // Act
        CartItemResponse response = CartItemResponse.builder()
                .cartItemId(1L)
                .productId(10L)
                .productName("Wireless Mouse")
                .productDescription("Optical Mouse")
                .unitPrice(new BigDecimal("29.99"))
                .quantity(2)
                .subtotal(new BigDecimal("59.98"))
                .imageUrl("https://example.com/mouse.jpg")
                .build();

        // Assert
        assertThat(response.getCartItemId()).isEqualTo(1L);
        assertThat(response.getProductId()).isEqualTo(10L);
        assertThat(response.getProductName()).isEqualTo("Wireless Mouse");
        assertThat(response.getProductDescription()).isEqualTo("Optical Mouse");
        assertThat(response.getUnitPrice()).isEqualTo(new BigDecimal("29.99"));
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getSubtotal()).isEqualTo(new BigDecimal("59.98"));
        assertThat(response.getImageUrl()).isEqualTo("https://example.com/mouse.jpg");
    }

    @Test
    @DisplayName("Should test no-args and all-args constructors")
    void shouldTestConstructors() {
        // Arrange & Act
        CartItemResponse empty = new CartItemResponse();
        CartItemResponse full = new CartItemResponse(
                1L, 10L, "Name", "Desc", BigDecimal.TEN, 1, BigDecimal.TEN, "http://url"
        );

        // Assert
        assertThat(empty.getCartItemId()).isNull();
        assertThat(full.getCartItemId()).isEqualTo(1L);
        assertThat(full.getProductName()).isEqualTo("Name");
    }
}
