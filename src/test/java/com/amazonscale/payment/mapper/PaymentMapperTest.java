package com.amazonscale.payment.mapper;

import com.amazonscale.order.entity.Order;
import com.amazonscale.order.enums.PaymentMethod;
import com.amazonscale.payment.dto.CreatePaymentRequest;
import com.amazonscale.payment.dto.PaymentResponse;
import com.amazonscale.payment.entity.Payment;
import com.amazonscale.payment.enums.PaymentGateway;
import com.amazonscale.payment.enums.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentMapperTest {

    @Test
    void toPayment_ShouldMapFieldsCorrectly() {
        Order order = Order.builder()
                .id(100L)
                .total(new BigDecimal("299.99"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .orderId(100L)
                .gateway(PaymentGateway.STRIPE)
                .build();

        String transactionId = "TXN-999";

        Payment payment = PaymentMapper.toPayment(request, order, transactionId);

        assertThat(payment).isNotNull();
        assertThat(payment.getOrder()).isEqualTo(order);
        assertThat(payment.getTransactionId()).isEqualTo(transactionId);
        assertThat(payment.getAmount()).isEqualByComparingTo("299.99");
        assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(payment.getGateway()).isEqualTo(PaymentGateway.STRIPE);
        assertThat(payment.getCurrency()).isEqualTo("INR");
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void toPayment_NullCheck() {
        Order order = new Order();
        CreatePaymentRequest request = new CreatePaymentRequest();

        assertThatThrownBy(() -> PaymentMapper.toPayment(null, order, "TXN"))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> PaymentMapper.toPayment(request, null, "TXN"))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> PaymentMapper.toPayment(request, order, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void toPaymentResponse_ShouldMapFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        Order order = Order.builder().id(50L).build();

        Payment payment = Payment.builder()
                .id(1L)
                .order(order)
                .transactionId("TXN-777")
                .amount(new BigDecimal("500.00"))
                .currency("INR")
                .paymentMethod(PaymentMethod.UPI)
                .gateway(PaymentGateway.RAZORPAY)
                .status(PaymentStatus.SUCCESS)
                .createdAt(now)
                .updatedAt(now)
                .build();

        PaymentResponse response = PaymentMapper.toPaymentResponse(payment);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getOrderId()).isEqualTo(50L);
        assertThat(response.getTransactionId()).isEqualTo("TXN-777");
        assertThat(response.getAmount()).isEqualByComparingTo("500.00");
        assertThat(response.getCurrency()).isEqualTo("INR");
        assertThat(response.getPaymentMethod()).isEqualTo(PaymentMethod.UPI);
        assertThat(response.getGateway()).isEqualTo(PaymentGateway.RAZORPAY);
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toPaymentResponse_NullCheck() {
        assertThatThrownBy(() -> PaymentMapper.toPaymentResponse(null))
                .isInstanceOf(NullPointerException.class);
    }
}
