package com.amazonscale.payment.service;

import com.amazonscale.payment.dto.CreatePaymentRequest;
import com.amazonscale.payment.dto.PaymentResponse;
import com.amazonscale.payment.dto.RefundRequest;

import java.util.List;

public interface PaymentService {

    PaymentResponse initiatePayment(Long authenticatedUserId ,CreatePaymentRequest request);

    PaymentResponse verifyPayment(Long userId ,Long paymentId);

    PaymentResponse getPayment(Long userId ,Long paymentId);

    List<PaymentResponse> getPaymentsByOrder(Long userId ,Long orderId);

    PaymentResponse refundPayment(Long userId ,Long paymentId, RefundRequest request);

}
