package com.amazonscale.payment.entity;

import com.amazonscale.order.entity.Order;
import com.amazonscale.order.enums.PaymentMethod;
import com.amazonscale.payment.enums.PaymentGateway;
import com.amazonscale.payment.enums.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {

    @Test
    @DisplayName("Should build Payment entity using Builder and verify defaults and getters/setters")
    void shouldBuildPaymentAndVerifyGettersSetters() {
        // Arrange
        Order order = Order.builder().id(100L).build();

        // Act
        Payment payment = Payment.builder()
                .id(1L)
                .order(order)
                .transactionId("TXN-999")
                .amount(new BigDecimal("150.00"))
                .paymentMethod(PaymentMethod.UPI)
                .gateway(PaymentGateway.PHONEPAY)
                .build();

        // Assert
        assertThat(payment.getId()).isEqualTo(1L);
        assertThat(payment.getOrder()).isEqualTo(order);
        assertThat(payment.getTransactionId()).isEqualTo("TXN-999");
        assertThat(payment.getAmount()).isEqualTo(new BigDecimal("150.00"));
        assertThat(payment.getCurrency()).isEqualTo("INR"); // Builder default
        assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.UPI);
        assertThat(payment.getGateway()).isEqualTo(PaymentGateway.PHONEPAY);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING); // Builder default
    }

    @Test
    @DisplayName("Should populate createdAt and updatedAt on @PrePersist (onCreate) and @PreUpdate (onUpdate)")
    void shouldPopulateTimestampsOnPrePersistAndPreUpdate() {
        // Arrange
        Payment payment = new Payment();

        // Act - Simulating PrePersist
        payment.onCreate();

        // Assert
        assertThat(payment.getCreatedAt()).isNotNull();
        assertThat(payment.getUpdatedAt()).isNotNull();

        LocalDateTime initialUpdatedAt = payment.getUpdatedAt();

        // Act - Simulating PreUpdate
        payment.onUpdate();

        // Assert
        assertThat(payment.getUpdatedAt()).isNotNull();
        assertThat(payment.getUpdatedAt()).isAfterOrEqualTo(initialUpdatedAt);
    }
}
