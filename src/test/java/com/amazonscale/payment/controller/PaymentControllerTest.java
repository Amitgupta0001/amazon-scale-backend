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
import org.junit.jupiter.api.DisplayName;
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
                .id(1L)
                .orderId(100L)
                .transactionId("TXN-123456")
                .amount(new BigDecimal("199.99"))
                .currency("INR")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .gateway(PaymentGateway.RAZORPAY)
                .status(PaymentStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Should initiate payment successfully and return HTTP 201 Created")
    void shouldInitiatePaymentSuccessfully() throws Exception {
        // Arrange
        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .orderId(100L)
                .gateway(PaymentGateway.RAZORPAY)
                .build();

        when(paymentService.initiatePayment(eq(1L), any(CreatePaymentRequest.class))).thenReturn(samplePaymentResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/payments")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(paymentService, times(1)).initiatePayment(eq(1L), any(CreatePaymentRequest.class));
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when CreatePaymentRequest fails Bean Validation")
    void shouldReturnBadRequestWhenInitiatePaymentValidationFails() throws Exception {
        // Arrange
        CreatePaymentRequest invalidRequest = new CreatePaymentRequest(); // missing orderId and gateway

        // Act & Assert
        mockMvc.perform(post("/api/v1/payments")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(paymentService, never()).initiatePayment(any(), any());
    }

    @Test
    @DisplayName("Should verify payment successfully and return HTTP 200 OK")
    void shouldVerifyPaymentSuccessfully() throws Exception {
        // Arrange
        samplePaymentResponse.setStatus(PaymentStatus.SUCCESS);
        when(paymentService.verifyPayment(1L, 1L)).thenReturn(samplePaymentResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/payments/1/verify")
                        .header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        verify(paymentService, times(1)).verifyPayment(1L, 1L);
    }

    @Test
    @DisplayName("Should get payment details by ID and return HTTP 200 OK")
    void shouldGetPaymentDetailsSuccessfully() throws Exception {
        // Arrange
        when(paymentService.getPayment(1L, 1L)).thenReturn(samplePaymentResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/payments/1")
                        .header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(paymentService, times(1)).getPayment(1L, 1L);
    }

    @Test
    @DisplayName("Should get payments by order ID and return HTTP 200 OK")
    void shouldGetPaymentsByOrderSuccessfully() throws Exception {
        // Arrange
        when(paymentService.getPaymentsByOrder(1L, 100L)).thenReturn(List.of(samplePaymentResponse));

        // Act & Assert
        mockMvc.perform(get("/api/v1/payments/orders/100")
                        .header("X-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(paymentService, times(1)).getPaymentsByOrder(1L, 100L);
    }

    @Test
    @DisplayName("Should refund payment successfully and return HTTP 200 OK")
    void shouldRefundPaymentSuccessfully() throws Exception {
        // Arrange
        RefundRequest request = RefundRequest.builder().reason("Defective product").build();
        samplePaymentResponse.setStatus(PaymentStatus.REFUNDED);

        when(paymentService.refundPayment(eq(1L), eq(1L), any(RefundRequest.class)))
                .thenReturn(samplePaymentResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/payments/1/refund")
                        .header("X-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"));

        verify(paymentService, times(1)).refundPayment(eq(1L), eq(1L), any(RefundRequest.class));
    }
}
