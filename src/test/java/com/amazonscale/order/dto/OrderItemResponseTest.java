package com.amazonscale.order.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemResponseTest {

    @Test
    void testOrderItemResponseBuilderAndGettersSetters() {
        OrderItemResponse response = OrderItemResponse.builder()
                .productId(10L)
                .productName("Widget")
                .sku("10")
                .quantity(2)
                .unitPrice(new BigDecimal("25.00"))
                .lineTotal(new BigDecimal("50.00"))
                .build();

        assertThat(response.getProductId()).isEqualTo(10L);
        assertThat(response.getProductName()).isEqualTo("Widget");
        assertThat(response.getSku()).isEqualTo("10");
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getUnitPrice()).isEqualTo(new BigDecimal("25.00"));
        assertThat(response.getLineTotal()).isEqualTo(new BigDecimal("50.00"));
    }
}
