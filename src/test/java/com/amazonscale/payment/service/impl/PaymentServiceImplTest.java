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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
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

    private User sampleUser;
    private Order sampleOrder;
    private Product sampleProduct;
    private OrderItem sampleOrderItem;
    private Payment samplePayment;
    private CreatePaymentRequest createPaymentRequest;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .build();

        sampleProduct = Product.builder()
                .id(10L)
                .name("Sample Product")
                .active(true)
                .build();

        sampleOrderItem = OrderItem.builder()
                .id(100L)
                .product(sampleProduct)
                .quantity(1)
                .build();

        sampleOrder = Order.builder()
                .id(50L)
                .user(sampleUser)
                .status(OrderStatus.PENDING)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .total(new BigDecimal("100.00"))
                .items(List.of(sampleOrderItem))
                .build();

        samplePayment = Payment.builder()
                .id(500L)
                .order(sampleOrder)
                .transactionId("TXN12345")
                .amount(new BigDecimal("100.00"))
                .currency("INR")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .gateway(PaymentGateway.STRIPE)
                .status(PaymentStatus.PENDING)
                .build();

        createPaymentRequest = CreatePaymentRequest.builder()
                .orderId(50L)
                .gateway(PaymentGateway.STRIPE)
                .build();
    }

    // ==========================================
    // initiatePayment Tests
    // ==========================================

    @Test
    void initiatePayment_Success() {
        when(orderRepository.findById(50L)).thenReturn(Optional.of(sampleOrder));
        when(paymentRepository.findByOrder_Id(50L)).thenReturn(Collections.emptyList());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> {
            Payment p = i.getArgument(0);
            p.setId(500L);
            return p;
        });

        PaymentResponse response = paymentService.initiatePayment(1L, createPaymentRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(500L);
        assertThat(response.getOrderId()).isEqualTo(50L);
        assertThat(response.getGateway()).isEqualTo(PaymentGateway.STRIPE);
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PENDING);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void initiatePayment_OrderNotFound() {
        when(orderRepository.findById(50L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.initiatePayment(1L, createPaymentRequest))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void initiatePayment_UnauthorizedUser() {
        when(orderRepository.findById(50L)).thenReturn(Optional.of(sampleOrder));

        assertThatThrownBy(() -> paymentService.initiatePayment(999L, createPaymentRequest))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessageContaining("You are not authorized to pay for this order.");
    }

    @Test
    void initiatePayment_OrderStatusNotPending() {
        sampleOrder.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(50L)).thenReturn(Optional.of(sampleOrder));

        assertThatThrownBy(() -> paymentService.initiatePayment(1L, createPaymentRequest))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessageContaining("Payment can only be initiated for pending orders.");
    }

    @Test
    void initiatePayment_AlreadyPaid() {
        Payment paidPayment = Payment.builder()
                .id(501L)
                .order(sampleOrder)
                .status(PaymentStatus.SUCCESS)
                .build();

        when(orderRepository.findById(50L)).thenReturn(Optional.of(sampleOrder));
        when(paymentRepository.findByOrder_Id(50L)).thenReturn(List.of(paidPayment));

        assertThatThrownBy(() -> paymentService.initiatePayment(1L, createPaymentRequest))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessageContaining("Order has already been paid.");
    }

    @Test
    void initiatePayment_InactiveProduct() {
        sampleProduct.setActive(false);
        when(orderRepository.findById(50L)).thenReturn(Optional.of(sampleOrder));
        when(paymentRepository.findByOrder_Id(50L)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> paymentService.initiatePayment(1L, createPaymentRequest))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessageContaining("is not active.");
    }

    // ==========================================
    // verifyPayment Tests
    // ==========================================

    @Test
    void verifyPayment_Success() {
        when(paymentRepository.findById(500L)).thenReturn(Optional.of(samplePayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        PaymentResponse response = paymentService.verifyPayment(1L, 500L);

        assertThat(response.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        verify(paymentRepository, times(1)).save(samplePayment);
    }

    @Test
    void verifyPayment_PaymentNotFound() {
        when(paymentRepository.findById(500L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.verifyPayment(1L, 500L))
                .isInstanceOf(PaymentNotFoundException.class)
                .hasMessageContaining("No payment found with id: 500");
    }

    @Test
    void verifyPayment_UnauthorizedUser() {
        when(paymentRepository.findById(500L)).thenReturn(Optional.of(samplePayment));

        assertThatThrownBy(() -> paymentService.verifyPayment(999L, 500L))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessageContaining("You are not authorized to access this payment.");
    }

    @Test
    void verifyPayment_AlreadySuccess() {
        samplePayment.setStatus(PaymentStatus.SUCCESS);
        when(paymentRepository.findById(500L)).thenReturn(Optional.of(samplePayment));

        assertThatThrownBy(() -> paymentService.verifyPayment(1L, 500L))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessageContaining("Payment has already been verified");
    }

    @Test
    void verifyPayment_RefundedPayment() {
        samplePayment.setStatus(PaymentStatus.REFUNDED);
        when(paymentRepository.findById(500L)).thenReturn(Optional.of(samplePayment));

        assertThatThrownBy(() -> paymentService.verifyPayment(1L, 500L))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessageContaining("Refunded payments cannot be verified");
    }

    @Test
    void verifyPayment_FailedPayment() {
        samplePayment.setStatus(PaymentStatus.FAILED);
        when(paymentRepository.findById(500L)).thenReturn(Optional.of(samplePayment));

        assertThatThrownBy(() -> paymentService.verifyPayment(1L, 500L))
                .isInstanceOf(PaymentFailedException.class)
                .hasMessageContaining("Failed payment cannot be verified.");
    }

    // ==========================================
    // getPayment Tests
    // ==========================================

    @Test
    void getPayment_Success() {
        when(paymentRepository.findById(500L)).thenReturn(Optional.of(samplePayment));

        PaymentResponse response = paymentService.getPayment(1L, 500L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(500L);
    }

    @Test
    void getPayment_UnauthorizedUser() {
        when(paymentRepository.findById(500L)).thenReturn(Optional.of(samplePayment));

        assertThatThrownBy(() -> paymentService.getPayment(999L, 500L))
                .isInstanceOf(InvalidPaymentException.class);
    }

    // ==========================================
    // getPaymentsByOrder Tests
    // ==========================================

    @Test
    void getPaymentsByOrder_Success() {
        when(orderRepository.findById(50L)).thenReturn(Optional.of(sampleOrder));
        when(paymentRepository.findByOrder_Id(50L)).thenReturn(List.of(samplePayment));

        List<PaymentResponse> responses = paymentService.getPaymentsByOrder(1L, 50L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(500L);
    }

    @Test
    void getPaymentsByOrder_OrderNotFound() {
        when(orderRepository.findById(50L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentsByOrder(1L, 50L))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void getPaymentsByOrder_UnauthorizedUser() {
        when(orderRepository.findById(50L)).thenReturn(Optional.of(sampleOrder));

        assertThatThrownBy(() -> paymentService.getPaymentsByOrder(999L, 50L))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessageContaining("You are not authorized to access payments for this order.");
    }

    @Test
    void getPaymentsByOrder_EmptyPayments() {
        when(orderRepository.findById(50L)).thenReturn(Optional.of(sampleOrder));
        when(paymentRepository.findByOrder_Id(50L)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> paymentService.getPaymentsByOrder(1L, 50L))
                .isInstanceOf(PaymentNotFoundException.class)
                .hasMessageContaining("No payments found for order id: 50");
    }

    // ==========================================
    // refundPayment Tests
    // ==========================================

    @Test
    void refundPayment_Success() {
        samplePayment.setStatus(PaymentStatus.SUCCESS);
        RefundRequest refundRequest = new RefundRequest("Item arrived broken");

        when(paymentRepository.findById(500L)).thenReturn(Optional.of(samplePayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        PaymentResponse response = paymentService.refundPayment(1L, 500L, refundRequest);

        assertThat(response.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        verify(paymentRepository, times(1)).save(samplePayment);
    }

    @Test
    void refundPayment_PendingStatus_ThrowsInvalidPaymentException() {
        samplePayment.setStatus(PaymentStatus.PENDING);
        RefundRequest refundRequest = new RefundRequest("Reason");

        when(paymentRepository.findById(500L)).thenReturn(Optional.of(samplePayment));

        assertThatThrownBy(() -> paymentService.refundPayment(1L, 500L, refundRequest))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessageContaining("Pending payment cannot be refunded.");
    }

    @Test
    void refundPayment_FailedStatus_ThrowsPaymentFailedException() {
        samplePayment.setStatus(PaymentStatus.FAILED);
        RefundRequest refundRequest = new RefundRequest("Reason");

        when(paymentRepository.findById(500L)).thenReturn(Optional.of(samplePayment));

        assertThatThrownBy(() -> paymentService.refundPayment(1L, 500L, refundRequest))
                .isInstanceOf(PaymentFailedException.class)
                .hasMessageContaining("Failed payment cannot be refunded.");
    }

    @Test
    void refundPayment_RefundedStatus_ThrowsInvalidPaymentException() {
        samplePayment.setStatus(PaymentStatus.REFUNDED);
        RefundRequest refundRequest = new RefundRequest("Reason");

        when(paymentRepository.findById(500L)).thenReturn(Optional.of(samplePayment));

        assertThatThrownBy(() -> paymentService.refundPayment(1L, 500L, refundRequest))
                .isInstanceOf(InvalidPaymentException.class)
                .hasMessageContaining("Payment has already been refunded.");
    }
}
