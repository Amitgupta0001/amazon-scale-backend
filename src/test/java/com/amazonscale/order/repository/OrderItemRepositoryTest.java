package com.amazonscale.order.repository;

import com.amazonscale.order.entity.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderItemRepositoryTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("Should find OrderItems by Order ID, User ID, and Product ID")
    void shouldFindOrderItemsByCustomQueries() {
        // Arrange
        OrderItem orderItem = OrderItem.builder().id(100L).build();

        when(orderItemRepository.findByOrder_Id(1L)).thenReturn(List.of(orderItem));
        when(orderItemRepository.findByOrder_User_Id(10L)).thenReturn(List.of(orderItem));
        when(orderItemRepository.findByProduct_Id(100L)).thenReturn(List.of(orderItem));
        when(orderItemRepository.countByOrder_Id(1L)).thenReturn(1L);

        // Act
        List<OrderItem> itemsByOrder = orderItemRepository.findByOrder_Id(1L);
        List<OrderItem> itemsByUser = orderItemRepository.findByOrder_User_Id(10L);
        List<OrderItem> itemsByProduct = orderItemRepository.findByProduct_Id(100L);
        long count = orderItemRepository.countByOrder_Id(1L);

        // Assert
        assertThat(itemsByOrder).hasSize(1);
        assertThat(itemsByUser).hasSize(1);
        assertThat(itemsByProduct).hasSize(1);
        assertThat(count).isEqualTo(1L);
    }
}
