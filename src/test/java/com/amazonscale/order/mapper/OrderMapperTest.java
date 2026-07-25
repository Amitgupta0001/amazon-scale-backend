package com.amazonscale.order.mapper;

import com.amazonscale.order.dto.OrderItemResponse;
import com.amazonscale.order.dto.OrderResponse;
import com.amazonscale.order.entity.Order;
import com.amazonscale.order.entity.OrderItem;
import com.amazonscale.order.enums.OrderStatus;
import com.amazonscale.order.enums.PaymentMethod;
import com.amazonscale.product.entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperTest {

    @Test
    void toOrderResponse_ShouldReturnNull_WhenOrderIsNull() {
        OrderResponse response = OrderMapper.toOrderResponse(null);
        assertThat(response).isNull();
    }

    @Test
    void toOrderItemResponse_ShouldReturnNull_WhenOrderItemIsNull() {
        OrderItemResponse response = OrderMapper.toOrderItemResponse(null);
        assertThat(response).isNull();
    }

    @Test
    void toOrderResponse_ShouldMapFields_WhenOrderHasNullItems() {
        LocalDateTime now = LocalDateTime.now();
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.PENDING)
                .paymentMethod(PaymentMethod.COD)
                .shippingAddress("Addr")
                .subtotal(new BigDecimal("100.00"))
                .tax(new BigDecimal("18.00"))
                .shippingFee(new BigDecimal("40.00"))
                .discount(BigDecimal.ZERO)
                .total(new BigDecimal("158.00"))
                .createdAt(now)
                .items(null)
                .build();

        OrderResponse response = OrderMapper.toOrderResponse(order);

        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(1L);
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.getPaymentMethod()).isEqualTo(PaymentMethod.COD);
        assertThat(response.getShippingAddress()).isEqualTo("Addr");
        assertThat(response.getItems()).isEmpty();
        assertThat(response.getItemsQuantity()).isEqualTo(0);
        assertThat(response.getSubtotal()).isEqualTo(new BigDecimal("100.00"));
        assertThat(response.getTax()).isEqualTo(new BigDecimal("18.00"));
        assertThat(response.getShippingFee()).isEqualTo(new BigDecimal("40.00"));
        assertThat(response.getDiscount()).isEqualTo(BigDecimal.ZERO);
        assertThat(response.getTotal()).isEqualTo(new BigDecimal("158.00"));
        assertThat(response.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void toOrderResponse_ShouldMapFieldsAndCalculateQuantity_WhenOrderHasItems() {
        Product product1 = Product.builder().id(10L).build();
        Product product2 = Product.builder().id(20L).build();

        OrderItem item1 = OrderItem.builder()
                .id(101L)
                .product(product1)
                .productName("Item 1")
                .sku("10")
                .quantity(2)
                .unitPrice(new BigDecimal("50.00"))
                .lineTotal(new BigDecimal("100.00"))
                .build();

        OrderItem item2 = OrderItem.builder()
                .id(102L)
                .product(product2)
                .productName("Item 2")
                .sku("20")
                .quantity(3)
                .unitPrice(new BigDecimal("100.00"))
                .lineTotal(new BigDecimal("300.00"))
                .build();

        Order order = Order.builder()
                .id(2L)
                .status(OrderStatus.CONFIRMED)
                .paymentMethod(PaymentMethod.UPI)
                .shippingAddress("Main Street")
                .subtotal(new BigDecimal("400.00"))
                .tax(new BigDecimal("72.00"))
                .shippingFee(new BigDecimal("40.00"))
                .discount(BigDecimal.ZERO)
                .total(new BigDecimal("512.00"))
                .items(List.of(item1, item2))
                .build();

        OrderResponse response = OrderMapper.toOrderResponse(order);

        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(2L);
        assertThat(response.getItemsQuantity()).isEqualTo(5);
        assertThat(response.getItems()).hasSize(2);

        OrderItemResponse itemResp1 = response.getItems().get(0);
        assertThat(itemResp1.getProductId()).isEqualTo(10L);
        assertThat(itemResp1.getProductName()).isEqualTo("Item 1");
        assertThat(itemResp1.getSku()).isEqualTo("10");
        assertThat(itemResp1.getQuantity()).isEqualTo(2);
        assertThat(itemResp1.getUnitPrice()).isEqualTo(new BigDecimal("50.00"));
        assertThat(itemResp1.getLineTotal()).isEqualTo(new BigDecimal("100.00"));
    }
}
