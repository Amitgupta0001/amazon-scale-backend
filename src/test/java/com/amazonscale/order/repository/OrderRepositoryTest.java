package com.amazonscale.order.repository;

import com.amazonscale.order.entity.Order;
import com.amazonscale.order.enums.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryTest {

    @Mock
    private OrderRepository orderRepository;

    @Test
    @DisplayName("Should find Orders by User ID and by Status")
    void shouldFindOrdersByUserIdAndStatus() {
        // Arrange
        Order order = Order.builder().id(1L).status(OrderStatus.PENDING).build();

        when(orderRepository.findByUser_Id(10L)).thenReturn(List.of(order));
        when(orderRepository.findByStatusAndUser_Id(OrderStatus.PENDING, 10L)).thenReturn(List.of(order));
        when(orderRepository.findByIdAndUser_Id(1L, 10L)).thenReturn(Optional.of(order));
        when(orderRepository.countByUser_Id(10L)).thenReturn(1L);

        // Act
        List<Order> userOrders = orderRepository.findByUser_Id(10L);
        List<Order> pendingOrders = orderRepository.findByStatusAndUser_Id(OrderStatus.PENDING, 10L);
        Optional<Order> foundByIdAndUser = orderRepository.findByIdAndUser_Id(1L, 10L);
        long count = orderRepository.countByUser_Id(10L);

        // Assert
        assertThat(userOrders).hasSize(1);
        assertThat(pendingOrders).hasSize(1);
        assertThat(foundByIdAndUser).isPresent();
        assertThat(count).isEqualTo(1L);
    }
}
