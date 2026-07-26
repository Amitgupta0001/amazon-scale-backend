package com.amazonscale.payment.controller;

import com.amazonscale.order.enums.PaymentMethod;
import com.amazonscale.payment.dto.CreatePaymentRequest;
import com.amazonscale.payment.dto.PaymentResponse;
import com.amazonscale.payment.dto.RefundRequest;
import com.amazonscale.payment.enums.PaymentGateway;
import com.amazonscale.payment.enums.PaymentStatus;
import com.amazonscale.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private ObjectMapper objectMapper;
    private PaymentResponse samplePaymentResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
        objectMapper = new ObjectMapper();

        samplePaymentResponse = PaymentResponse.builder()
                .id(500L)
                .orderId(50L)
                .transactionId("TXN12345")
                .amount(new BigDecimal("100.00"))
                .currency("INR")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .gateway(PaymentGateway.STRIPE)
                .status(PaymentStatus.PENDING)
                .build();
    }

    @Test
    void initiatePayment_Success() throws Exception {
        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .orderId(50L)
                .gateway(PaymentGateway.STRIPE)
                .build();

        when(paymentService.initiatePayment(eq(1L), any(CreatePaymentRequest.class))).thenReturn(samplePaymentResponse);

        mockMvc.perform(post("/api/v1/payments")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(500L))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(paymentService, times(1)).initiatePayment(eq(1L), any(CreatePaymentRequest.class));
    }

    @Test
    void initiatePayment_ValidationError() throws Exception {
        CreatePaymentRequest request = new CreatePaymentRequest(); // Missing orderId and gateway

        mockMvc.perform(post("/api/v1/payments")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(paymentService, never()).initiatePayment(any(), any());
    }

    @Test
    void verifyPayment_Success() throws Exception {
        samplePaymentResponse.setStatus(PaymentStatus.SUCCESS);
        when(paymentService.verifyPayment(1L, 500L)).thenReturn(samplePaymentResponse);

        mockMvc.perform(put("/api/v1/payments/500/verify")
                        .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(paymentService, times(1)).verifyPayment(1L, 500L);
    }

    @Test
    void getPaymentDetails_Success() throws Exception {
        when(paymentService.getPayment(1L, 500L)).thenReturn(samplePaymentResponse);

        mockMvc.perform(get("/api/v1/payments/500")
                        .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(500L));

        verify(paymentService, times(1)).getPayment(1L, 500L);
    }

    @Test
    void getPaymentsByOrder_Success() throws Exception {
        when(paymentService.getPaymentsByOrder(1L, 50L)).thenReturn(List.of(samplePaymentResponse));

        mockMvc.perform(get("/api/v1/payments/orders/50")
                        .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(500L));

        verify(paymentService, times(1)).getPaymentsByOrder(1L, 50L);
    }

    @Test
    void refundPayment_Success() throws Exception {
        samplePaymentResponse.setStatus(PaymentStatus.REFUNDED);
        RefundRequest request = new RefundRequest("Item damaged in transit");

        when(paymentService.refundPayment(eq(1L), eq(500L), any(RefundRequest.class))).thenReturn(samplePaymentResponse);

        mockMvc.perform(post("/api/v1/payments/500/refund")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"));

        verify(paymentService, times(1)).refundPayment(eq(1L), eq(500L), any(RefundRequest.class));
    }

    @Test
    void refundPayment_ValidationError() throws Exception {
        RefundRequest request = new RefundRequest(""); // Empty reason

        mockMvc.perform(post("/api/v1/payments/500/refund")
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(paymentService, never()).refundPayment(any(), any(), any());
    }
}
