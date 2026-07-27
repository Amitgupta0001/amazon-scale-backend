package com.amazonscale.order.controller;

import com.amazonscale.order.dto.CreateOrderRequest;
import com.amazonscale.order.dto.OrderResponse;
import com.amazonscale.order.enums.OrderStatus;
import com.amazonscale.order.enums.PaymentMethod;
import com.amazonscale.order.service.OrderService;
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
                .orderId(500L)
                .orderStatus(OrderStatus.PENDING)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .shippingAddress("123 Street")
                .subtotal(new BigDecimal("100.00"))
                .tax(new BigDecimal("18.00"))
                .shippingFee(new BigDecimal("40.00"))
                .discount(BigDecimal.ZERO)
                .total(new BigDecimal("158.00"))
                .build();
    }

    @Test
    @DisplayName("Should place order successfully and return HTTP 201 Created")
    void shouldPlaceOrderSuccessfully() throws Exception {
        // Arrange
        CreateOrderRequest request = CreateOrderRequest.builder()
                .shippingAddress("123 Street")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        when(orderService.createOrder(eq(1L), any(CreateOrderRequest.class))).thenReturn(sampleOrderResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/orders")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(500L))
                .andExpect(jsonPath("$.orderStatus").value("PENDING"));

        verify(orderService, times(1)).createOrder(eq(1L), any(CreateOrderRequest.class));
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when CreateOrderRequest validation fails")
    void shouldReturnBadRequestWhenPlaceOrderValidationFails() throws Exception {
        // Arrange
        CreateOrderRequest invalidRequest = new CreateOrderRequest(); // missing shippingAddress and paymentMethod

        // Act & Assert
        mockMvc.perform(post("/api/v1/orders")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrder(any(), any());
    }

    @Test
    @DisplayName("Should get order by ID and return HTTP 200 OK")
    void shouldGetOrderByIdSuccessfully() throws Exception {
        // Arrange
        when(orderService.getOrder(1L, 500L)).thenReturn(sampleOrderResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/orders/500")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(500L));

        verify(orderService, times(1)).getOrder(1L, 500L);
    }

    @Test
    @DisplayName("Should get all orders for user and return HTTP 200 OK")
    void shouldGetOrdersByUserIdSuccessfully() throws Exception {
        // Arrange
        when(orderService.getOrdersByUserId(1L)).thenReturn(List.of(sampleOrderResponse));

        // Act & Assert
        mockMvc.perform(get("/api/v1/orders")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderId").value(500L));

        verify(orderService, times(1)).getOrdersByUserId(1L);
    }

    @Test
    @DisplayName("Should cancel order and return HTTP 200 OK")
    void shouldCancelOrderSuccessfully() throws Exception {
        // Arrange
        sampleOrderResponse.setOrderStatus(OrderStatus.CANCELLED);
        when(orderService.cancelOrder(1L, 500L)).thenReturn(sampleOrderResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/orders/500/cancel")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("CANCELLED"));

        verify(orderService, times(1)).cancelOrder(1L, 500L);
    }

    @Test
    @DisplayName("Should update order status and return HTTP 200 OK")
    void shouldUpdateOrderStatusSuccessfully() throws Exception {
        // Arrange
        sampleOrderResponse.setOrderStatus(OrderStatus.CONFIRMED);
        when(orderService.updateOrderStatus(500L, OrderStatus.CONFIRMED)).thenReturn(sampleOrderResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/orders/500/status")
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("CONFIRMED"));

        verify(orderService, times(1)).updateOrderStatus(500L, OrderStatus.CONFIRMED);
    }
}
