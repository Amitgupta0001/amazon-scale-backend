package com.amazonscale.order.repository;

import com.amazonscale.order.entity.Order;
import com.amazonscale.order.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser_Id(Long userId);
    List<Order> findByStatusAndUser_Id(OrderStatus status, Long userId);
    List<Order> findByStatus(OrderStatus status);
    long countByStatus(OrderStatus status);
    List<Order> findByUser_IdOrderByCreatedAtDesc(Long userId);
    List<Order> findAllByOrderByCreatedAtDesc();
    long countByUser_Id(Long userId);

    Optional<Order> findByIdAndUser_Id(Long orderId, Long userId);
}