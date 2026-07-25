package com.amazonscale.order.dto;

import com.amazonscale.order.enums.OrderStatus;
import com.amazonscale.order.enums.PaymentMethod;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class OrderResponseTest {

    @Test
    void testOrderResponseBuilderAndGettersSetters() {
        LocalDateTime now = LocalDateTime.now();
        OrderResponse response = OrderResponse.builder()
                .orderId(1L)
                .orderStatus(OrderStatus.PENDING)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .shippingAddress("456 Avenue")
                .items(Collections.emptyList())
                .itemsQuantity(0)
                .subtotal(new BigDecimal("100.00"))
                .tax(new BigDecimal("18.00"))
                .shippingFee(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO)
                .total(new BigDecimal("118.00"))
                .createdAt(now)
                .build();

        assertThat(response.getOrderId()).isEqualTo(1L);
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(response.getShippingAddress()).isEqualTo("456 Avenue");
        assertThat(response.getItems()).isEmpty();
        assertThat(response.getItemsQuantity()).isEqualTo(0);
        assertThat(response.getSubtotal()).isEqualTo(new BigDecimal("100.00"));
        assertThat(response.getTax()).isEqualTo(new BigDecimal("18.00"));
        assertThat(response.getShippingFee()).isEqualTo(BigDecimal.ZERO);
        assertThat(response.getDiscount()).isEqualTo(BigDecimal.ZERO);
        assertThat(response.getTotal()).isEqualTo(new BigDecimal("118.00"));
        assertThat(response.getCreatedAt()).isEqualTo(now);
    }
}
