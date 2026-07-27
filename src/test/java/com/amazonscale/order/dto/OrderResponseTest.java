package com.amazonscale.order.dto;

import com.amazonscale.order.enums.OrderStatus;
import com.amazonscale.order.enums.PaymentMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderResponseTest {

    @Test
    @DisplayName("Should build OrderResponse using Builder and verify all getters/setters")
    void shouldBuildOrderResponseAndVerifyGettersSetters() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        OrderItemResponse item = OrderItemResponse.builder()
                .productId(1L)
                .productName("Item")
                .quantity(2)
                .build();

        // Act
        OrderResponse response = OrderResponse.builder()
                .orderId(100L)
                .orderStatus(OrderStatus.CONFIRMED)
                .paymentMethod(PaymentMethod.UPI)
                .shippingAddress("123 Street")
                .items(List.of(item))
                .itemsQuantity(2)
                .subtotal(new BigDecimal("100.00"))
                .tax(new BigDecimal("18.00"))
                .shippingFee(new BigDecimal("40.00"))
                .discount(BigDecimal.ZERO)
                .total(new BigDecimal("158.00"))
                .createdAt(now)
                .build();

        // Assert
        assertThat(response.getOrderId()).isEqualTo(100L);
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(response.getPaymentMethod()).isEqualTo(PaymentMethod.UPI);
        assertThat(response.getShippingAddress()).isEqualTo("123 Street");
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItemsQuantity()).isEqualTo(2);
        assertThat(response.getSubtotal()).isEqualTo(new BigDecimal("100.00"));
        assertThat(response.getTax()).isEqualTo(new BigDecimal("18.00"));
        assertThat(response.getShippingFee()).isEqualTo(new BigDecimal("40.00"));
        assertThat(response.getDiscount()).isEqualTo(BigDecimal.ZERO);
        assertThat(response.getTotal()).isEqualTo(new BigDecimal("158.00"));
        assertThat(response.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should test no-args and all-args constructors")
    void shouldTestConstructors() {
        // Arrange & Act
        OrderResponse empty = new OrderResponse();
        OrderResponse full = new OrderResponse(
                1L, OrderStatus.PENDING, PaymentMethod.COD, "Addr", List.of(), 0,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null
        );

        // Assert
        assertThat(empty.getOrderId()).isNull();
        assertThat(full.getOrderId()).isEqualTo(1L);
        assertThat(full.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
    }
}
