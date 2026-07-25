package com.amazonscale.order.mapper;

import com.amazonscale.order.dto.OrderItemResponse;
import com.amazonscale.order.dto.OrderResponse;
import com.amazonscale.order.entity.Order;
import com.amazonscale.order.entity.OrderItem;

import java.util.Collections;
import java.util.List;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toOrderResponse(Order order) {

        if (order == null) {
            return null;
        }

        List<OrderItem> orderItems = order.getItems() == null
                ? Collections.emptyList()
                : order.getItems();

        List<OrderItemResponse> items = orderItems.stream()
                .map(OrderMapper::toOrderItemResponse)
                .toList();

        int totalQuantity = orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        return OrderResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getStatus())
                .itemsQuantity(totalQuantity)
                .items(items)
                .subtotal(order.getSubtotal())
                .tax(order.getTax())
                .shippingFee(order.getShippingFee())
                .discount(order.getDiscount())
                .total(order.getTotal())
                .paymentMethod(order.getPaymentMethod())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .build();
    }
    public static OrderItemResponse toOrderItemResponse(OrderItem orderItem) {

        if (orderItem == null) {
            return null;
        }

        return OrderItemResponse.builder()
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProductName())
                .sku(orderItem.getSku())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .lineTotal(orderItem.getLineTotal())
                .build();
    }
}