package com.amazonscale.payment.dto;

import com.amazonscale.order.enums.PaymentMethod;
import com.amazonscale.payment.enums.PaymentGateway;
import com.amazonscale.payment.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentResponseTest {

    @Test
    void testGettersSettersAndBuilder() {
        LocalDateTime now = LocalDateTime.now();
        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .orderId(10L)
                .transactionId("TXN123")
                .amount(new BigDecimal("150.00"))
                .currency("INR")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .gateway(PaymentGateway.STRIPE)
                .status(PaymentStatus.SUCCESS)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getOrderId()).isEqualTo(10L);
        assertThat(response.getTransactionId()).isEqualTo("TXN123");
        assertThat(response.getAmount()).isEqualByComparingTo("150.00");
        assertThat(response.getCurrency()).isEqualTo("INR");
        assertThat(response.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(response.getGateway()).isEqualTo(PaymentGateway.STRIPE);
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testNoArgsConstructor() {
        PaymentResponse response = new PaymentResponse();
        response.setId(2L);
        assertThat(response.getId()).isEqualTo(2L);
    }
}
