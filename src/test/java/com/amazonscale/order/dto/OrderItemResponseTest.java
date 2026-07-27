package com.amazonscale.order.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemResponseTest {

    @Test
    @DisplayName("Should build OrderItemResponse using Builder and verify getters/setters")
    void shouldBuildOrderItemResponseAndVerifyGettersSetters() {
        // Act
        OrderItemResponse response = OrderItemResponse.builder()
                .productId(10L)
        .productName("Laptop")
        .sku("LAP123")
        .quantity(1)
        .unitPrice(new BigDecimal("999.99"))
        .lineTotal(new BigDecimal("999.99"))
        .build();

        // Assert
        assertThat(response.getProductId()).isEqualTo(10L);
        assertThat(response.getProductName()).isEqualTo("Laptop");
        assertThat(response.getSku()).isEqualTo("LAP123");
        assertThat(response.getQuantity()).isEqualTo(1);
        assertThat(response.getUnitPrice()).isEqualTo(new BigDecimal("999.99"));
        assertThat(response.getLineTotal()).isEqualTo(new BigDecimal("999.99"));
    }

    @Test
    @DisplayName("Should test no-args and all-args constructors")
    void shouldTestConstructors() {
        // Arrange & Act
        OrderItemResponse empty = new OrderItemResponse();
        OrderItemResponse full = new OrderItemResponse(
                1L, "Phone", "SKU1", 2, BigDecimal.TEN, new BigDecimal("20.00")
        );

        // Assert
        assertThat(empty.getProductId()).isNull();
        assertThat(full.getProductId()).isEqualTo(1L);
        assertThat(full.getProductName()).isEqualTo("Phone");
    }
}
