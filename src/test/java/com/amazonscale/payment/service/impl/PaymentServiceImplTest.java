package com.amazonscale.payment.service.impl;

import com.amazonscale.order.entity.Order;
import com.amazonscale.order.entity.OrderItem;
import com.amazonscale.order.enums.OrderStatus;
import com.amazonscale.order.enums.PaymentMethod;
import com.amazonscale.order.exception.OrderNotFoundException;
import com.amazonscale.order.repository.OrderRepository;
import com.amazonscale.payment.dto.CreatePaymentRequest;
import com.amazonscale.payment.dto.PaymentResponse;
import com.amazonscale.payment.dto.RefundRequest;
import com.amazonscale.payment.entity.Payment;
import com.amazonscale.payment.enums.PaymentGateway;
import com.amazonscale.payment.enums.PaymentStatus;
import com.amazonscale.payment.exception.InvalidPaymentException;
import com.amazonscale.payment.exception.PaymentFailedException;
import com.amazonscale.payment.exception.PaymentNotFoundException;
import com.amazonscale.payment.repository.PaymentRepository;
import com.amazonscale.product.entity.Product;
import com.amazonscale.user.entity.User;
import com.amazonscale.user.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User user;
    private Order order;
    private Product product;
    private OrderItem orderItem;
    private Payment payment;
    private CreatePaymentRequest createPaymentRequest;
    private RefundRequest refundRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("Pay")
                .lastName("User")
                .email("payuser@example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        product = Product.builder()
                .id(10L)
                .name("Camera")
                .active(true)
                .build();

        orderItem = OrderItem.builder()
                .id(100L)
                .product(product)
                .quantity(1)
                .unitPrice(new BigDecimal("499.99"))
                .lineTotal(new BigDecimal("499.99"))
                .build();

        order = Order.builder()
                .id(500L)
                .user(user)
                .status(OrderStatus.PENDING)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .shippingAddress("123 Main St")
                .total(new BigDecimal("499.99"))
                .items(new ArrayList<>(List.of(orderItem)))
                .build();

        payment = Payment.builder()
                .id(1L)
                .order(order)
                .transactionId("TXN-1234567890123456")
                .amount(new BigDecimal("499.99"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .gateway(PaymentGateway.RAZORPAY)
                .status(PaymentStatus.PENDING)
                .currency("INR")
                .build();

        createPaymentRequest = CreatePaymentRequest.builder()
                .orderId(500L)
                .gateway(PaymentGateway.RAZORPAY)
                .build();

        refundRequest = RefundRequest.builder()
                .reason("Defective product")
                .build();
    }

    @Test
    @DisplayName("Should initiate payment successfully")
    void shouldInitiatePaymentSuccessfully() {
        // Arrange
        when(orderRepository.findById(500L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrder_Id(500L)).thenReturn(List.of());
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        PaymentResponse response = paymentService.initiatePayment(1L, createPaymentRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PENDING);

        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order does not exist during payment initiation")
    void shouldThrowOrderNotFoundExceptionWhenOrderMissing() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        createPaymentRequest.setOrderId(999L);

        // Act & Assert
        assertThatThrownBy(() -> paymentService.initiatePayment(1L, createPaymentRequest))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw InvalidPaymentException when user is unauthorized for order")
    void shouldThrowInvalidPaymentExceptionWhenUnauthorizedUser() {
        // Arrange
        when(orderRepository.findById(500L)).thenReturn(Optional.of(order));

        // Act & Assert (user 99L != order.user 1L)
        assertThatThrownBy(() -> paymentService.initiatePayment(99L, createPaymentRequest))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessage("You are not authorized to pay for this order.");
    }

    @Test
    @DisplayName("Should throw InvalidPaymentException when order is not in PENDING state")
    void shouldThrowInvalidPaymentExceptionWhenOrderNotPending() {
        // Arrange
        order.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(500L)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() -> paymentService.initiatePayment(1L, createPaymentRequest))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessage("Payment can only be initiated for pending orders.");
    }

    @Test
    @DisplayName("Should throw InvalidPaymentException when order is already paid")
    void shouldThrowInvalidPaymentExceptionWhenAlreadyPaid() {
        // Arrange
        Payment paidPayment = Payment.builder().id(2L).status(PaymentStatus.SUCCESS).build();
        when(orderRepository.findById(500L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrder_Id(500L)).thenReturn(List.of(paidPayment));

        // Act & Assert
        assertThatThrownBy(() -> paymentService.initiatePayment(1L, createPaymentRequest))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessage("Order has already been paid.");
    }

    @Test
    @DisplayName("Should verify pending payment successfully and change status to SUCCESS")
    void shouldVerifyPaymentSuccessfully() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);

        // Act
        PaymentResponse response = paymentService.verifyPayment(1L, 1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);

        verify(paymentRepository).save(payment);
    }

    @Test
    @DisplayName("Should throw InvalidPaymentException when verifying already verified payment")
    void shouldThrowExceptionWhenVerifyingAlreadyVerifiedPayment() {
        // Arrange
        payment.setStatus(PaymentStatus.SUCCESS);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        // Act & Assert
        assertThatThrownBy(() -> paymentService.verifyPayment(1L, 1L))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessage("Payment has already been verified");
    }

    @Test
    @DisplayName("Should throw PaymentFailedException when verifying a FAILED payment")
    void shouldThrowExceptionWhenVerifyingFailedPayment() {
        // Arrange
        payment.setStatus(PaymentStatus.FAILED);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        // Act & Assert
        assertThatThrownBy(() -> paymentService.verifyPayment(1L, 1L))
                .isInstanceOf(PaymentFailedException.class)
                .hasMessage("Failed payment cannot be verified.");
    }

    @Test
    @DisplayName("Should get payment details for authorized user")
    void shouldGetPaymentSuccessfully() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        // Act
        PaymentResponse response = paymentService.getPayment(1L, 1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should refund successful payment and set refundReason")
    void shouldRefundPaymentSuccessfully() {
        // Arrange
        payment.setStatus(PaymentStatus.SUCCESS);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);

        // Act
        PaymentResponse response = paymentService.refundPayment(1L, 1L, refundRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(payment.getRefundReason()).isEqualTo("Defective product");

        verify(paymentRepository).save(payment);
    }

    @Test
    @DisplayName("Should throw InvalidPaymentException when refunding a PENDING payment")
    void shouldThrowExceptionWhenRefundingPendingPayment() {
        // Arrange
        payment.setStatus(PaymentStatus.PENDING);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        // Act & Assert
        assertThatThrownBy(() -> paymentService.refundPayment(1L, 1L, refundRequest))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessage("Pending payment cannot be refunded.");
    }
}
