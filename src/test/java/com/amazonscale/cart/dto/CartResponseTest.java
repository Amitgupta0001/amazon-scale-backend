package com.amazonscale.cart.dto;

import com.amazonscale.cart.entity.CurrencyCode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CartResponseTest {

    @Test
    void testCartResponseGettersSettersBuilder() {
        LocalDateTime now = LocalDateTime.now();
        CartItemResponse item = CartItemResponse.builder()
                .cartItemId(1L)
                .quantity(2)
                .build();

        CartResponse response = CartResponse.builder()
                .cartId(10L)
                .userId(5L)
                .items(List.of(item))
                .totalItems(2)
                .totalAmount(new BigDecimal("500.00"))
                .updatedAt(now)
                .currency(CurrencyCode.INR)
                .build();

        assertThat(response.getCartId()).isEqualTo(10L);
        assertThat(response.getUserId()).isEqualTo(5L);
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getTotalItems()).isEqualTo(2);
        assertThat(response.getTotalAmount()).isEqualTo(new BigDecimal("500.00"));
        assertThat(response.getUpdatedAt()).isEqualTo(now);
        assertThat(response.getCurrency()).isEqualTo(CurrencyCode.INR);
    }
}
