package com.amazonscale.order.entity;

import com.amazonscale.order.enums.OrderStatus;
import com.amazonscale.order.enums.PaymentMethod;
import com.amazonscale.user.entity.User;
import com.amazonscale.user.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    @DisplayName("Should build Order entity and verify default values and getters/setters")
    void shouldBuildOrderAndVerifyGettersSetters() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .firstName("Order")
                .lastName("User")
                .email("orderuser@example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        // Act
        Order order = Order.builder()
                .id(10L)
                .user(user)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .shippingAddress("123 Street")
                .subtotal(new BigDecimal("100.00"))
                .total(new BigDecimal("118.00"))
                .build();

        // Assert
        assertThat(order.getId()).isEqualTo(10L);
        assertThat(order.getUser()).isEqualTo(user);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING); // Builder default
        assertThat(order.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(order.getShippingAddress()).isEqualTo("123 Street");
        assertThat(order.getTax()).isEqualTo(BigDecimal.ZERO); // Builder default
        assertThat(order.getShippingFee()).isEqualTo(BigDecimal.ZERO); // Builder default
        assertThat(order.getDiscount()).isEqualTo(BigDecimal.ZERO); // Builder default
        assertThat(order.getItems()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Should add and remove OrderItem correctly using helper methods")
    void shouldAddAndRemoveOrderItemCorrectly() {
        // Arrange
        Order order = new Order();
        OrderItem item = OrderItem.builder().id(100L).productName("Gadget").build();

        // Act - Add item
        order.addItem(item);

        // Assert
        assertThat(order.getItems()).containsExactly(item);
        assertThat(item.getOrder()).isEqualTo(order);

        // Act - Remove item
        order.removeItem(item);

        // Assert
        assertThat(order.getItems()).isEmpty();
        assertThat(item.getOrder()).isNull();

        // Edge cases: null item
        order.addItem(null);
        order.removeItem(null);
        assertThat(order.getItems()).isEmpty();
    }

    @Test
    @DisplayName("Should populate createdAt and updatedAt on @PrePersist (onCreate) and @PreUpdate (onUpdate)")
    void shouldPopulateTimestampsOnPrePersistAndPreUpdate() {
        // Arrange
        Order order = new Order();

        // Act - Simulating PrePersist
        order.onCreate();

        // Assert
        assertThat(order.getCreatedAt()).isNotNull();
        assertThat(order.getUpdatedAt()).isNotNull();

        LocalDateTime initialUpdatedAt = order.getUpdatedAt();

        // Act - Simulating PreUpdate
        order.onUpdate();

        // Assert
        assertThat(order.getUpdatedAt()).isNotNull();
        assertThat(order.getUpdatedAt()).isAfterOrEqualTo(initialUpdatedAt);
    }

    @Test
    @DisplayName("Should test equals and hashCode based on ID")
    void shouldTestEqualsAndHashCode() {
        // Arrange
        Order order1 = Order.builder().id(1L).build();
        Order order2 = Order.builder().id(1L).build();
        Order order3 = Order.builder().id(2L).build();

        // Assert
        assertThat(order1).isEqualTo(order2);
        assertThat(order1).hasSameHashCodeAs(order2);
        assertThat(order1).isNotEqualTo(order3);
    }
}
