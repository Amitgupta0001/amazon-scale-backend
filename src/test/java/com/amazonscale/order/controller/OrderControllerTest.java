package com.amazonscale.order.controller;

import com.amazonscale.order.dto.CreateOrderRequest;
import com.amazonscale.order.dto.OrderResponse;
import com.amazonscale.order.enums.OrderStatus;
import com.amazonscale.order.enums.PaymentMethod;
import com.amazonscale.order.service.OrderService;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private ObjectMapper objectMapper;
    private OrderResponse sampleOrderResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();

        sampleOrderResponse = OrderResponse.builder()
                .orderId(100L)
                .orderStatus(OrderStatus.PENDING)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .shippingAddress("123 Main St")
                .items(Collections.emptyList())
                .itemsQuantity(0)
                .subtotal(new BigDecimal("100.00"))
                .tax(new BigDecimal("18.00"))
                .shippingFee(new BigDecimal("40.00"))
                .discount(BigDecimal.ZERO)
                .total(new BigDecimal("158.00"))
                .build();
    }

    @Test
    void testPlaceOrderSuccess() throws Exception {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .shippingAddress("123 Main St")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        when(orderService.createOrder(eq(1L), any(CreateOrderRequest.class))).thenReturn(sampleOrderResponse);

        mockMvc.perform(post("/api/v1/orders")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(100L))
                .andExpect(jsonPath("$.orderStatus").value("PENDING"));

        verify(orderService, times(1)).createOrder(eq(1L), any(CreateOrderRequest.class));
    }

    @Test
    void testPlaceOrderValidationError() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(); // Missing shippingAddress and paymentMethod

        mockMvc.perform(post("/api/v1/orders")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrder(any(), any());
    }

    @Test
    void testGetOrderSuccess() throws Exception {
        when(orderService.getOrder(1L, 100L)).thenReturn(sampleOrderResponse);

        mockMvc.perform(get("/api/v1/orders/100")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(100L));

        verify(orderService, times(1)).getOrder(1L, 100L);
    }

    @Test
    void testGetOrdersSuccess() throws Exception {
        when(orderService.getOrdersByUserId(1L)).thenReturn(List.of(sampleOrderResponse));

        mockMvc.perform(get("/api/v1/orders")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderId").value(100L));

        verify(orderService, times(1)).getOrdersByUserId(1L);
    }

    @Test
    void testCancelOrderSuccess() throws Exception {
        sampleOrderResponse.setOrderStatus(OrderStatus.CANCELLED);
        when(orderService.cancelOrder(1L, 100L)).thenReturn(sampleOrderResponse);

        mockMvc.perform(put("/api/v1/orders/100/cancel")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("CANCELLED"));

        verify(orderService, times(1)).cancelOrder(1L, 100L);
    }

    @Test
    void testUpdateOrderStatusSuccess() throws Exception {
        sampleOrderResponse.setOrderStatus(OrderStatus.CONFIRMED);
        when(orderService.updateOrderStatus(100L, OrderStatus.CONFIRMED)).thenReturn(sampleOrderResponse);

        mockMvc.perform(patch("/api/v1/orders/100/status")
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("CONFIRMED"));

        verify(orderService, times(1)).updateOrderStatus(100L, OrderStatus.CONFIRMED);
    }
}
