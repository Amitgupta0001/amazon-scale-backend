package com.amazonscale.payment.mapper;

import com.amazonscale.order.entity.Order;
import com.amazonscale.payment.dto.CreatePaymentRequest;
import com.amazonscale.payment.dto.PaymentResponse;
import com.amazonscale.payment.entity.Payment;
import com.amazonscale.payment.enums.PaymentStatus;

import java.util.Objects;

public final class PaymentMapper {

    private PaymentMapper() {}

    // Creates a new Payment entity using the payment request
    public static Payment toPayment(CreatePaymentRequest request, Order order, String transactionId) {

        Objects.requireNonNull(request, "CreatePaymentRequest cannot be null");
        Objects.requireNonNull(order, "Order cannot be null");
        Objects.requireNonNull(transactionId, "Transaction ID cannot be null");

        return Payment.builder()
                .order(order)
                .transactionId(transactionId)
                .amount(order.getTotal())
                .paymentMethod(order.getPaymentMethod())
                .gateway(request.getGateway())
                .currency("INR")
                .status(PaymentStatus.PENDING)
                .build();
    }

    // Converts a Payment entity into a response DTO
    public static PaymentResponse toPaymentResponse(Payment payment) {
        Objects.requireNonNull(payment, "Payment cannot be null");
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .transactionId(payment.getTransactionId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMethod(payment.getPaymentMethod()) // Payment method (UPI, CARD, COD, etc.)
                .gateway(payment.getGateway())  // Payment gateway (Razorpay, Stripe, etc.).
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}