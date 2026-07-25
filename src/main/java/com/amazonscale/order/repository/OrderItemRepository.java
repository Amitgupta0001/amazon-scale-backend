package com.amazonscale.order.repository;

import com.amazonscale.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder_Id(Long orderId);
    List<OrderItem> findByOrder_User_Id(Long userId);
    List<OrderItem> findByProduct_Id(Long productId);
    List<OrderItem> findByOrder_IdAndProduct_Id(Long orderId, Long productId);
    long countByOrder_Id(Long orderId);
    long countByProduct_Id(Long productId);
}