package com.amazonscale.payment.entity;

import com.amazonscale.order.entity.Order;
import com.amazonscale.order.enums.PaymentMethod;
import com.amazonscale.payment.enums.PaymentGateway;
import com.amazonscale.payment.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {

    @Test
    void testGettersSettersAndBuilderDefaults() {
        Order order = new Order();
        order.setId(10L);

        Payment payment = Payment.builder()
                .id(1L)
                .order(order)
                .transactionId("TXN-123456")
                .amount(new BigDecimal("999.99"))
                .paymentMethod(PaymentMethod.UPI)
                .gateway(PaymentGateway.PHONEPAY)
                .refundReason("None")
                .build();

        assertThat(payment.getId()).isEqualTo(1L);
        assertThat(payment.getOrder()).isEqualTo(order);
        assertThat(payment.getTransactionId()).isEqualTo("TXN-123456");
        assertThat(payment.getAmount()).isEqualByComparingTo("999.99");
        assertThat(payment.getCurrency()).isEqualTo("INR");
        assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.UPI);
        assertThat(payment.getGateway()).isEqualTo(PaymentGateway.PHONEPAY);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getRefundReason()).isEqualTo("None");
    }

    @Test
    void testLifecycleHooks() throws InterruptedException {
        Payment payment = new Payment();
        payment.onCreate();

        assertThat(payment.getCreatedAt()).isNotNull();
        assertThat(payment.getUpdatedAt()).isNotNull();

        var initialUpdatedAt = payment.getUpdatedAt();
        Thread.sleep(10);
        payment.onUpdate();

        assertThat(payment.getUpdatedAt()).isAfterOrEqualTo(initialUpdatedAt);
    }
}
