package com.amazonscale.order.entity;

import com.amazonscale.order.enums.OrderStatus;
import com.amazonscale.order.enums.PaymentMethod;
import com.amazonscale.user.entity.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    void testOrderBuilderAndGettersSetters() {
        User user = User.builder().id(1L).email("user@example.com").build();
        Order order = Order.builder()
                .id(100L)
                .user(user)
                .status(OrderStatus.PENDING)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .shippingAddress("123 Main St")
                .subtotal(new BigDecimal("100.00"))
                .tax(new BigDecimal("18.00"))
                .shippingFee(new BigDecimal("40.00"))
                .discount(BigDecimal.ZERO)
                .total(new BigDecimal("158.00"))
                .build();

        assertThat(order.getId()).isEqualTo(100L);
        assertThat(order.getUser()).isEqualTo(user);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(order.getShippingAddress()).isEqualTo("123 Main St");
        assertThat(order.getSubtotal()).isEqualTo(new BigDecimal("100.00"));
        assertThat(order.getTax()).isEqualTo(new BigDecimal("18.00"));
        assertThat(order.getShippingFee()).isEqualTo(new BigDecimal("40.00"));
        assertThat(order.getDiscount()).isEqualTo(BigDecimal.ZERO);
        assertThat(order.getTotal()).isEqualTo(new BigDecimal("158.00"));
        assertThat(order.getItems()).isEmpty();
    }

    @Test
    void testOnCreateAndOnUpdateLifecycleHooks() {
        Order order = new Order();
        order.onCreate();

        assertThat(order.getCreatedAt()).isNotNull();
        assertThat(order.getUpdatedAt()).isNotNull();

        LocalDateTime initialUpdatedAt = order.getUpdatedAt();
        order.onUpdate();
        assertThat(order.getUpdatedAt()).isNotNull();
    }

    @Test
    void testAddItemAndRemoveItem() {
        Order order = new Order();
        OrderItem item = OrderItem.builder().id(1L).productName("Laptop").quantity(1).build();

        order.addItem(item);
        assertThat(order.getItems()).containsExactly(item);
        assertThat(item.getOrder()).isEqualTo(order);

        // Duplicate add check
        order.addItem(item);
        assertThat(order.getItems()).hasSize(1);

        // Null add check
        order.addItem(null);
        assertThat(order.getItems()).hasSize(1);

        // Remove item
        order.removeItem(item);
        assertThat(order.getItems()).isEmpty();
        assertThat(item.getOrder()).isNull();

        // Null remove check
        order.removeItem(null);
        assertThat(order.getItems()).isEmpty();
    }
}
