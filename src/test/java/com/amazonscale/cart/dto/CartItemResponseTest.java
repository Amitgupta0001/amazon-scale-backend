package com.amazonscale.cart.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CartItemResponseTest {

    @Test
    void testCartItemResponseGettersSettersBuilder() {
        CartItemResponse response = CartItemResponse.builder()
                .cartItemId(1L)
                .productId(100L)
                .productName("Laptop")
                .productDescription("High performance laptop")
                .unitPrice(new BigDecimal("1200.00"))
                .quantity(2)
                .subtotal(new BigDecimal("2400.00"))
                .imageUrl("https://example.com/laptop.jpg")
                .build();

        assertThat(response.getCartItemId()).isEqualTo(1L);
        assertThat(response.getProductId()).isEqualTo(100L);
        assertThat(response.getProductName()).isEqualTo("Laptop");
        assertThat(response.getProductDescription()).isEqualTo("High performance laptop");
        assertThat(response.getUnitPrice()).isEqualTo(new BigDecimal("1200.00"));
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getSubtotal()).isEqualTo(new BigDecimal("2400.00"));
        assertThat(response.getImageUrl()).isEqualTo("https://example.com/laptop.jpg");
    }

    @Test
    void testNoArgsConstructorAndAllArgsConstructor() {
        CartItemResponse empty = new CartItemResponse();
        assertThat(empty.getCartItemId()).isNull();

        CartItemResponse full = new CartItemResponse(
                1L, 100L, "Laptop", "Desc", new BigDecimal("100"), 1, new BigDecimal("100"), "img.jpg"
        );
        assertThat(full.getCartItemId()).isEqualTo(1L);
        assertThat(full.getProductName()).isEqualTo("Laptop");
    }
}
