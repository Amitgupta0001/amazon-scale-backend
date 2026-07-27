package com.amazonscale.payment.mapper;

import com.amazonscale.order.entity.Order;
import com.amazonscale.order.enums.PaymentMethod;
import com.amazonscale.payment.dto.CreatePaymentRequest;
import com.amazonscale.payment.dto.PaymentResponse;
import com.amazonscale.payment.entity.Payment;
import com.amazonscale.payment.enums.PaymentGateway;
import com.amazonscale.payment.enums.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentMapperTest {

    @Test
    @DisplayName("Should map CreatePaymentRequest, Order, and transactionId to Payment entity")
    void shouldMapToPayment() {
        // Arrange
        Order order = Order.builder()
                .id(100L)
                .total(new BigDecimal("199.99"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .orderId(100L)
                .gateway(PaymentGateway.RAZORPAY)
                .build();

        String txnId = "TXN-123456";

        // Act
        Payment payment = PaymentMapper.toPayment(request, order, txnId);

        // Assert
        assertThat(payment).isNotNull();
        assertThat(payment.getOrder()).isEqualTo(order);
        assertThat(payment.getTransactionId()).isEqualTo(txnId);
        assertThat(payment.getAmount()).isEqualTo(new BigDecimal("199.99"));
        assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(payment.getGateway()).isEqualTo(PaymentGateway.RAZORPAY);
        assertThat(payment.getCurrency()).isEqualTo("INR");
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    @DisplayName("Should map Payment entity to PaymentResponse DTO")
    void shouldMapToPaymentResponse() {
        // Arrange
        Order order = Order.builder().id(100L).build();
        LocalDateTime now = LocalDateTime.now();

        Payment payment = Payment.builder()
                .id(1L)
                .order(order)
                .transactionId("TXN-123")
                .amount(new BigDecimal("99.99"))
                .currency("INR")
                .paymentMethod(PaymentMethod.UPI)
                .gateway(PaymentGateway.PHONEPAY)
                .status(PaymentStatus.SUCCESS)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Act
        PaymentResponse response = PaymentMapper.toPaymentResponse(payment);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getOrderId()).isEqualTo(100L);
        assertThat(response.getTransactionId()).isEqualTo("TXN-123");
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("99.99"));
        assertThat(response.getCurrency()).isEqualTo("INR");
        assertThat(response.getPaymentMethod()).isEqualTo(PaymentMethod.UPI);
        assertThat(response.getGateway()).isEqualTo(PaymentGateway.PHONEPAY);
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should throw NullPointerException when null input is provided to mapper")
    void shouldThrowNullPointerExceptionForNullInputs() {
        // Act & Assert
        assertThatThrownBy(() -> PaymentMapper.toPayment(null, new Order(), "TXN"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("CreatePaymentRequest cannot be null");

        assertThatThrownBy(() -> PaymentMapper.toPaymentResponse(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Payment cannot be null");
    }

    @Test
    @DisplayName("Should instantiate private constructor via reflection for test coverage")
    void shouldInstantiatePrivateConstructorForCoverage() throws Exception {
        // Arrange
        Constructor<PaymentMapper> constructor = PaymentMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // Act
        PaymentMapper instance = constructor.newInstance();

        // Assert
        assertThat(instance).isNotNull();
    }
}
