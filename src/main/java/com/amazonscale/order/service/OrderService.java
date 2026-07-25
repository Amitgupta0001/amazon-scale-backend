package com.amazonscale.order.service;

import com.amazonscale.order.dto.CreateOrderRequest;
import com.amazonscale.order.dto.OrderResponse;
import com.amazonscale.order.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(Long userId, CreateOrderRequest request);
    OrderResponse getOrder(Long userId, Long orderId);
    List<OrderResponse> getOrdersByUserId(Long userId);
    OrderResponse cancelOrder(Long userId, Long orderId);
    OrderResponse updateOrderStatus(Long orderId, OrderStatus orderStatus);
}