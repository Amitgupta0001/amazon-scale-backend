package com.amazonscale.order.controller;

import com.amazonscale.order.dto.CreateOrderRequest;
import com.amazonscale.order.dto.OrderResponse;
import com.amazonscale.order.enums.OrderStatus;
import com.amazonscale.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Orders",
        description = "Order Management APIs"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    // Place Order
    @Operation(
            summary = "Place Order",
            description = "Creates a new order from the user's cart."
    )
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestParam Long userId,
            @Valid @RequestBody CreateOrderRequest request) {

        OrderResponse response = orderService.createOrder(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    // Get Single Order
    @Operation(
            summary = "Get Order",
            description = "Returns a specific order belonging to the user."
    )
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @RequestParam Long userId,
            @PathVariable Long orderId) {

        OrderResponse response = orderService.getOrder(userId, orderId);

        return ResponseEntity.ok(response);
    }

    // Get All Orders of User
    @Operation(
            summary = "Get All Orders",
            description = "Returns all orders of the user."
    )
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(
            @RequestParam Long userId) {

        List<OrderResponse> response =
                orderService.getOrdersByUserId(userId);

        return ResponseEntity.ok(response);
    }

    // Cancel Order
    @Operation(
            summary = "Cancel Order",
            description = "Cancels an order if it has not been delivered."
    )
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @RequestParam Long userId,
            @PathVariable Long orderId) {

        OrderResponse response =
                orderService.cancelOrder(userId, orderId);

        return ResponseEntity.ok(response);
    }

    // Update Order Status (Admin)
    @Operation(
            summary = "Update Order Status",
            description = "Updates the status of an existing order."
    )
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {

        OrderResponse response =
                orderService.updateOrderStatus(orderId, status);

        return ResponseEntity.ok(response);
    }
}