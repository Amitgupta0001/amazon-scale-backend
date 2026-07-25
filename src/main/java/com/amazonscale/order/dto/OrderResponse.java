package com.amazonscale.order.dto;

import com.amazonscale.order.enums.OrderStatus;
import com.amazonscale.order.enums.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private OrderStatus orderStatus;

    private PaymentMethod paymentMethod;
    private String shippingAddress;

    private List<OrderItemResponse> items;
    private Integer itemsQuantity;

    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal shippingFee;
    private BigDecimal discount;
    private BigDecimal total;

    private LocalDateTime createdAt;

}
