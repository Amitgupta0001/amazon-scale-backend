package com.amazonscale.payment.dto;

import com.amazonscale.order.enums.PaymentMethod;
import com.amazonscale.payment.enums.PaymentGateway;
import com.amazonscale.payment.enums.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentResponseTest {

    @Test
    @DisplayName("Should build PaymentResponse using Builder and verify getters/setters")
    void shouldBuildPaymentResponseAndVerifyGettersSetters() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .orderId(100L)
                .transactionId("TXN-123456")
                .amount(new BigDecimal("299.99"))
                .currency("INR")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .gateway(PaymentGateway.RAZORPAY)
                .status(PaymentStatus.SUCCESS)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getOrderId()).isEqualTo(100L);
        assertThat(response.getTransactionId()).isEqualTo("TXN-123456");
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("299.99"));
        assertThat(response.getCurrency()).isEqualTo("INR");
        assertThat(response.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(response.getGateway()).isEqualTo(PaymentGateway.RAZORPAY);
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should test no-args and all-args constructors")
    void shouldTestConstructors() {
        // Arrange & Act
        PaymentResponse empty = new PaymentResponse();
        PaymentResponse full = new PaymentResponse(
                1L, 100L, "TXN-1", BigDecimal.TEN, "INR",
                PaymentMethod.UPI, PaymentGateway.STRIPE, PaymentStatus.PENDING, null, null
        );

        // Assert
        assertThat(empty.getId()).isNull();
        assertThat(full.getId()).isEqualTo(1L);
        assertThat(full.getTransactionId()).isEqualTo("TXN-1");
    }
}
