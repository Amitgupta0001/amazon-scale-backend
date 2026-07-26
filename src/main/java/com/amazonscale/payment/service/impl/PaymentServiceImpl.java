package com.amazonscale.payment.service.impl;

import com.amazonscale.order.entity.Order;
import com.amazonscale.order.entity.OrderItem;
import com.amazonscale.order.enums.OrderStatus;
import com.amazonscale.order.exception.OrderNotFoundException;
import com.amazonscale.order.repository.OrderRepository;
import com.amazonscale.payment.dto.CreatePaymentRequest;
import com.amazonscale.payment.dto.PaymentResponse;
import com.amazonscale.payment.dto.RefundRequest;
import com.amazonscale.payment.entity.Payment;
import com.amazonscale.payment.enums.PaymentStatus;
import com.amazonscale.payment.exception.InvalidPaymentException;
import com.amazonscale.payment.exception.PaymentFailedException;
import com.amazonscale.payment.exception.PaymentNotFoundException;
import com.amazonscale.payment.mapper.PaymentMapper;
import com.amazonscale.payment.repository.PaymentRepository;
import com.amazonscale.payment.service.PaymentService;
import com.amazonscale.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;  // use for fetching order details

    // helper methods
    private void validateRequest(Order order) {

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidPaymentException(
                    "Payment can only be initiated for pending orders."
            );
        }

        boolean alreadyPaid = paymentRepository.findByOrder_Id(order.getId())
                .stream()
                .anyMatch(payment -> payment.getStatus() == PaymentStatus.SUCCESS);

        if (alreadyPaid) {
            throw new InvalidPaymentException(
                    "Order has already been paid."
            );
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();

            if (!Boolean.TRUE.equals(product.getActive())) {
                throw new InvalidPaymentException(
                        "Cannot process payment because product '" +
                                product.getName() + "' is not active."
                );
            }
        }
    }

    private String generateTransactionId(){
        return "TXN-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16)
                .toUpperCase();
    }

    private Payment getAuthorizedPayment(Long userId, Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new PaymentNotFoundException(
                                "No payment found with id: " + paymentId
                        ));

        if (!payment.getOrder().getUser().getId().equals(userId)) {
            throw new InvalidPaymentException(
                    "You are not authorized to access this payment."
            );
        }

        return payment;
    }

    // Service implementation starts here
    @Override
    public PaymentResponse initiatePayment(Long userId, CreatePaymentRequest request) {

        // Fetch the requested order.
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() ->
                        new OrderNotFoundException(request.getOrderId()));

        // Ensure the order belongs to the current user.
        if (!order.getUser().getId().equals(userId)) {
            throw new InvalidPaymentException(
                    "You are not authorized to pay for this order."
            );
        }
        // Validate whether the order is eligible for payment.
        validateRequest(order);

        // Generate a unique transaction ID.
        String transactionId = generateTransactionId();
        Payment payment = PaymentMapper.toPayment(
                request,
                order,
                transactionId
        );

        payment = paymentRepository.save(payment);
        return PaymentMapper.toPaymentResponse(payment);
    }


    // Payment verification happens here
    @Override
    public PaymentResponse verifyPayment(Long userId ,Long paymentId) {

        Payment payment = getAuthorizedPayment(userId, paymentId);

        if(payment.getStatus()== PaymentStatus.SUCCESS){
            throw new InvalidPaymentException("Payment has already been verified");
        }

        if(payment.getStatus()== PaymentStatus.REFUNDED){
            throw new InvalidPaymentException("Refunded payments cannot be verified");
        }

        if(payment.getStatus()== PaymentStatus.FAILED){
            throw new PaymentFailedException("Failed payment cannot be verified.");
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        payment = paymentRepository.save(payment);
        return PaymentMapper.toPaymentResponse(payment);
    }


    //Getting Payment details here
    @Override
    public PaymentResponse getPayment(Long userId,Long paymentId){
        Payment payment = getAuthorizedPayment(userId, paymentId);
        return PaymentMapper.toPaymentResponse(payment);
    }

    @Override
    public List<PaymentResponse> getPaymentsByOrder(Long userId, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Ensure the order belongs to the current user.
        if (!order.getUser().getId().equals(userId)) {
            throw new InvalidPaymentException("You are not authorized to access payments for this order.");
        }
        // Fetch all payments for the order
        List<Payment> payments = paymentRepository.findByOrder_Id(orderId);

        if (payments.isEmpty()) {
            throw new PaymentNotFoundException("No payments found for order id: "+orderId);
        }
        // Convert entities to response DTOs.
        return payments.stream()
                .map(PaymentMapper::toPaymentResponse)
                .toList();
    }


    @Override
    public PaymentResponse refundPayment(Long userId ,Long paymentId, RefundRequest request) {
        Payment payment = getAuthorizedPayment(userId, paymentId);

        // Pending payments cannot be refunded.
        if (payment.getStatus() == PaymentStatus.PENDING) {
            throw new InvalidPaymentException(
                    "Pending payment cannot be refunded."
            );
        }
        // Failed payments cannot be refunded.
        if (payment.getStatus() == PaymentStatus.FAILED) {
            throw new PaymentFailedException(
                    "Failed payment cannot be refunded."
            );
        }
        // Prevent duplicate refunds.
        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new InvalidPaymentException(
                    "Payment has already been refunded."
            );
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundReason(request.getReason());

        // Persist changes.
        payment = paymentRepository.save(payment);

        // Return response.
        return PaymentMapper.toPaymentResponse(payment);
    }



}
