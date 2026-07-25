package com.amazonscale.order.entity;

import com.amazonscale.product.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemTest {

    @Test
    void testOrderItemBuilderAndGettersSetters() {
        Product product = Product.builder().id(10L).name("Phone").build();
        Order order = Order.builder().id(100L).build();

        OrderItem item = OrderItem.builder()
                .id(5L)
                .order(order)
                .product(product)
                .productName("Phone")
                .sku("SKU123")
                .quantity(2)
                .unitPrice(new BigDecimal("500.00"))
                .lineTotal(new BigDecimal("1000.00"))
                .build();

        assertThat(item.getId()).isEqualTo(5L);
        assertThat(item.getOrder()).isEqualTo(order);
        assertThat(item.getProduct()).isEqualTo(product);
        assertThat(item.getProductName()).isEqualTo("Phone");
        assertThat(item.getSku()).isEqualTo("SKU123");
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getUnitPrice()).isEqualTo(new BigDecimal("500.00"));
        assertThat(item.getLineTotal()).isEqualTo(new BigDecimal("1000.00"));
    }

    @Test
    void testCalculateLineTotalPrePersist() {
        OrderItem item = new OrderItem();
        item.setUnitPrice(new BigDecimal("150.00"));
        item.setQuantity(3);

        ReflectionTestUtils.invokeMethod(item, "calculateLineTotal");

        assertThat(item.getLineTotal()).isEqualTo(new BigDecimal("450.00"));
    }
}
