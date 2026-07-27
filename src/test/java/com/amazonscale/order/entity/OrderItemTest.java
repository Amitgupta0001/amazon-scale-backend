package com.amazonscale.order.entity;

import com.amazonscale.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemTest {

    @Test
    @DisplayName("Should build OrderItem entity and verify getters/setters")
    void shouldBuildOrderItemAndVerifyGettersSetters() {
        // Arrange
        Order order = Order.builder().id(1L).build();
        Product product = Product.builder().id(10L).build();

        // Act
        OrderItem item = OrderItem.builder()
                .id(100L)
                .order(order)
                .product(product)
                .productName("Monitor")
                .sku("MON100")
                .quantity(2)
                .unitPrice(new BigDecimal("150.00"))
                .lineTotal(new BigDecimal("300.00"))
                .build();

        // Assert
        assertThat(item.getId()).isEqualTo(100L);
        assertThat(item.getOrder()).isEqualTo(order);
        assertThat(item.getProduct()).isEqualTo(product);
        assertThat(item.getProductName()).isEqualTo("Monitor");
        assertThat(item.getSku()).isEqualTo("MON100");
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getUnitPrice()).isEqualTo(new BigDecimal("150.00"));
        assertThat(item.getLineTotal()).isEqualTo(new BigDecimal("300.00"));
    }

    @Test
    @DisplayName("Should calculate lineTotal dynamically on calculateLineTotal lifecycle hook")
    void shouldCalculateLineTotalOnPrePersistPreUpdate() throws Exception {
        // Arrange
        OrderItem item = OrderItem.builder()
                .unitPrice(new BigDecimal("25.00"))
                .quantity(4)
                .build();

        Method method = OrderItem.class.getDeclaredMethod("calculateLineTotal");
        method.setAccessible(true);

        // Act
        method.invoke(item);

        // Assert
        assertThat(item.getLineTotal()).isEqualTo(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Should test equals and hashCode based on ID")
    void shouldTestEqualsAndHashCode() {
        // Arrange
        OrderItem item1 = OrderItem.builder().id(1L).build();
        OrderItem item2 = OrderItem.builder().id(1L).build();
        OrderItem item3 = OrderItem.builder().id(2L).build();

        // Assert
        assertThat(item1).isEqualTo(item2);
        assertThat(item1).hasSameHashCodeAs(item2);
        assertThat(item1).isNotEqualTo(item3);
    }
}
