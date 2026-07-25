package com.amazonscale.order.service.impl;

import com.amazonscale.cart.entity.Cart;
import com.amazonscale.cart.entity.CartItem;
import com.amazonscale.cart.repository.CartRepository;
import com.amazonscale.inventory.exception.InsufficientStockException;
import com.amazonscale.order.dto.CreateOrderRequest;
import com.amazonscale.order.dto.OrderResponse;
import com.amazonscale.order.entity.Order;
import com.amazonscale.order.entity.OrderItem;
import com.amazonscale.order.enums.OrderStatus;
import com.amazonscale.order.exception.EmptyCartException;
import com.amazonscale.order.exception.InvalidOrderStatusTransitionException;
import com.amazonscale.order.exception.OrderNotFoundException;
import com.amazonscale.order.mapper.OrderMapper;
import com.amazonscale.order.repository.OrderRepository;
import com.amazonscale.order.service.OrderService;
import com.amazonscale.product.entity.Product;
import com.amazonscale.product.exception.ProductInactiveException;
import com.amazonscale.product.repository.ProductRepository;
import com.amazonscale.user.entity.User;
import com.amazonscale.user.exception.UserNotFoundException;
import com.amazonscale.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    private static final BigDecimal GST_RATE = BigDecimal.valueOf(0.18);
    private static final BigDecimal FREE_SHIPPING_LIMIT = BigDecimal.valueOf(500);
    private static final BigDecimal SHIPPING_FEE = BigDecimal.valueOf(40);

    private BigDecimal calculateTax(BigDecimal subtotal) {
        return subtotal.multiply(GST_RATE).setScale(2, RoundingMode.HALF_UP); // for first version of the  project i will use 18%GST
    }

    private BigDecimal calculateShippingFee(BigDecimal subtotal) {
        if (subtotal.compareTo(FREE_SHIPPING_LIMIT) >= 0) {
            return BigDecimal.ZERO;
        }
        return SHIPPING_FEE;
    }

    private BigDecimal calculateDiscount(BigDecimal subtotal) {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private void validateStock(Cart cart) {
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            if (!Boolean.TRUE.equals(product.getActive())) {
                throw new ProductInactiveException(
                        "Product is inactive: " + product.getName());
            }
            if (product.getStock() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product: "
                                + product.getName());
            }
        }
    }

    private void reduceInventory(Cart cart) {
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
        }
        productRepository.saveAll(
                cart.getCartItems()
                        .stream()
                        .map(CartItem::getProduct)
                        .toList()
        );
    }

    private void restoreInventory(Order order) {
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
        }
        productRepository.saveAll(
                order.getItems()
                        .stream()
                        .map(OrderItem::getProduct)
                        .toList()
        );
    }

    private void clearCart(Cart cart) {
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    private OrderItem buildOrderItem(CartItem cartItem, Order order) {

        Product product = cartItem.getProduct();

        return OrderItem.builder()
                .order(order)
                .product(product)
                .productName(product.getName())
                .sku(product.getId().toString())   // Replace with product.getSku() later
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getPriceAtAddition())
                .lineTotal(
                        cartItem.getPriceAtAddition()
                                .multiply(BigDecimal.valueOf(cartItem.getQuantity()))
                                .setScale(2, RoundingMode.HALF_UP)
                )
                .build();
    }


    //Create Order
    @Override
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseThrow(() ->
                        new EmptyCartException("Cart not found for user: " + userId));

        if (cart.getCartItems().isEmpty()) {
            throw new EmptyCartException("Cannot place order with an empty cart.");
        }

        validateStock(cart);

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getCartItems()) {

            subtotal = subtotal.add(
                    cartItem.getPriceAtAddition()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
        }

        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);

        BigDecimal tax = calculateTax(subtotal);
        BigDecimal shippingFee = calculateShippingFee(subtotal);
        BigDecimal discount = calculateDiscount(subtotal);

        BigDecimal total = subtotal
                .add(tax)
                .add(shippingFee)
                .subtract(discount)
                .setScale(2, RoundingMode.HALF_UP);

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .shippingAddress(request.getShippingAddress())
                .subtotal(subtotal)
                .tax(tax)
                .shippingFee(shippingFee)
                .discount(discount)
                .total(total)
                .build();

        for (CartItem cartItem : cart.getCartItems()) {
            order.addItem(buildOrderItem(cartItem, order));
        }

        reduceInventory(cart);

        Order savedOrder = orderRepository.save(order);

        clearCart(cart);

        return OrderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long userId, Long orderId) {
        Order order = orderRepository
                .findByIdAndUser_Id(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
        return OrderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUser_IdOrderByCreatedAtDesc(userId);
        return orders.stream()
                .map(OrderMapper::toOrderResponse)
                .toList();
    }


    //Cancel Order
    @Override
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository
                .findByIdAndUser_Id(orderId, userId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: " + orderId));

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new InvalidOrderStatusTransitionException(
                    "Delivered orders cannot be cancelled.");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusTransitionException(
                    "Order is already cancelled.");
        }
        restoreInventory(order);
        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toOrderResponse(savedOrder);
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        OrderStatus currentStatus = order.getStatus();

        // Terminal states
        if (currentStatus == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusTransitionException("Cancelled orders cannot change status.");
        }

        if (currentStatus == OrderStatus.DELIVERED) {
            throw new InvalidOrderStatusTransitionException("Delivered orders cannot change status.");
        }

        boolean validTransition = switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED;
            case SHIPPED -> newStatus == OrderStatus.DELIVERED;
            default -> false;
        };

        if (!validTransition) {
            throw new InvalidOrderStatusTransitionException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }

        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toOrderResponse(savedOrder);
    }
}