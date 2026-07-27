package com.amazonscale.order.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @Test
    @DisplayName("Should contain all expected order status enum values")
    void shouldContainExpectedEnumValues() {
        // Arrange & Act
        OrderStatus[] values = OrderStatus.values();

        // Assert
        assertThat(values).containsExactlyInAnyOrder(
                OrderStatus.PENDING,
                OrderStatus.CONFIRMED,
                OrderStatus.SHIPPED,
                OrderStatus.DELIVERED,
                OrderStatus.CANCELLED
        );
    }

    @Test
    @DisplayName("Should valueOf resolve string correctly")
    void shouldResolveValueOfCorrectly() {
        // Act & Assert
        assertThat(OrderStatus.valueOf("PENDING")).isEqualTo(OrderStatus.PENDING);
        assertThat(OrderStatus.valueOf("CONFIRMED")).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(OrderStatus.valueOf("SHIPPED")).isEqualTo(OrderStatus.SHIPPED);
        assertThat(OrderStatus.valueOf("DELIVERED")).isEqualTo(OrderStatus.DELIVERED);
        assertThat(OrderStatus.valueOf("CANCELLED")).isEqualTo(OrderStatus.CANCELLED);
    }
}
