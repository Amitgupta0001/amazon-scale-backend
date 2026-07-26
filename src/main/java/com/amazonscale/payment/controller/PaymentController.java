package com.amazonscale.payment.controller;

import com.amazonscale.payment.dto.CreatePaymentRequest;
import com.amazonscale.payment.dto.PaymentResponse;
import com.amazonscale.payment.dto.RefundRequest;
import com.amazonscale.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Payments",
        description = "Payment Management APIs"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    //Initiate payment
    @Operation(
            summary = "Initiate payment",
            description = "Initiates the payment process"
    )
    @PostMapping
    public ResponseEntity<PaymentResponse> initiatePayment(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreatePaymentRequest request) {

        PaymentResponse response = paymentService.initiatePayment(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Verifies a pending payment
    @Operation(
            summary = "Verify Payment",
            description = "Verifies a pending payment for the authenticated user."
    )
    @PutMapping("/{paymentId}/verify")
    public ResponseEntity<PaymentResponse> verifyPayment(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long paymentId){
        PaymentResponse response = paymentService.verifyPayment(userId,paymentId);
        return ResponseEntity.ok(response);
    }

    //Get All Payment Details
    @Operation(
            summary = "Get Payment Details",
            description = "Retrieves payment details for the specified payment."
    )
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentDetails(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long paymentId){
        PaymentResponse response = paymentService.getPayment(userId,paymentId);
        return ResponseEntity.ok(response);
    }

    //Get payment details by orderId
    @Operation(
            summary = "Get Payments by Order",
            description = "Retrieves all payments for the specified order"
    )
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByOrder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long orderId){
        List<PaymentResponse> response = paymentService.getPaymentsByOrder(userId, orderId);
        return ResponseEntity.ok(response);
    }

    //Requesting Refund by paymentId
    @Operation(
            summary = "Request Payment Refund",
            description = "Requesting refund using paymentId"
    )
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long paymentId,
            @Valid @RequestBody RefundRequest request){
        PaymentResponse response = paymentService.refundPayment(userId, paymentId, request);
        return ResponseEntity.ok(response);
    }

}
