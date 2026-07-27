package com.amazonscale.order.mapper;

import com.amazonscale.order.dto.OrderItemResponse;
import com.amazonscale.order.dto.OrderResponse;
import com.amazonscale.order.entity.Order;
import com.amazonscale.order.entity.OrderItem;
import com.amazonscale.order.enums.OrderStatus;
import com.amazonscale.order.enums.PaymentMethod;
import com.amazonscale.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperTest {

    @Test
    @DisplayName("Should map OrderItem entity to OrderItemResponse DTO")
    void shouldMapOrderItemToOrderItemResponse() {
        // Arrange
        Product product = Product.builder().id(10L).build();
        OrderItem item = OrderItem.builder()
                .product(product)
                .productName("Wireless Mouse")
                .sku("WM10")
                .quantity(2)
                .unitPrice(new BigDecimal("20.00"))
                .lineTotal(new BigDecimal("40.00"))
                .build();

        // Act
        OrderItemResponse response = OrderMapper.toOrderItemResponse(item);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getProductId()).isEqualTo(10L);
        assertThat(response.getProductName()).isEqualTo("Wireless Mouse");
        assertThat(response.getSku()).isEqualTo("WM10");
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getUnitPrice()).isEqualTo(new BigDecimal("20.00"));
        assertThat(response.getLineTotal()).isEqualTo(new BigDecimal("40.00"));
    }

    @Test
    @DisplayName("Should map Order entity with items to OrderResponse DTO")
    void shouldMapOrderToOrderResponse() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Product product = Product.builder().id(10L).build();
        OrderItem item1 = OrderItem.builder().product(product).productName("P1").quantity(2).lineTotal(new BigDecimal("20.00")).build();
        OrderItem item2 = OrderItem.builder().product(product).productName("P2").quantity(3).lineTotal(new BigDecimal("30.00")).build();

        Order order = Order.builder()
                .id(100L)
                .status(OrderStatus.CONFIRMED)
                .paymentMethod(PaymentMethod.UPI)
                .shippingAddress("456 Ocean Ave")
                .subtotal(new BigDecimal("50.00"))
                .tax(new BigDecimal("9.00"))
                .shippingFee(new BigDecimal("40.00"))
                .discount(BigDecimal.ZERO)
                .total(new BigDecimal("99.00"))
                .items(List.of(item1, item2))
                .createdAt(now)
                .build();

        // Act
        OrderResponse response = OrderMapper.toOrderResponse(order);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(100L);
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(response.getItemsQuantity()).isEqualTo(5); // 2 + 3
        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getSubtotal()).isEqualTo(new BigDecimal("50.00"));
        assertThat(response.getTotal()).isEqualTo(new BigDecimal("99.00"));
        assertThat(response.getPaymentMethod()).isEqualTo(PaymentMethod.UPI);
        assertThat(response.getShippingAddress()).isEqualTo("456 Ocean Ave");
    }

    @Test
    @DisplayName("Should handle null order or null orderItem gracefully in mapper")
    void shouldHandleNullsGracefully() {
        // Act & Assert
        assertThat(OrderMapper.toOrderResponse(null)).isNull();
        assertThat(OrderMapper.toOrderItemResponse(null)).isNull();
    }

    @Test
    @DisplayName("Should instantiate private constructor via reflection for test coverage")
    void shouldInstantiatePrivateConstructorForCoverage() throws Exception {
        // Arrange
        Constructor<OrderMapper> constructor = OrderMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // Act
        OrderMapper instance = constructor.newInstance();

        // Assert
        assertThat(instance).isNotNull();
    }
}
