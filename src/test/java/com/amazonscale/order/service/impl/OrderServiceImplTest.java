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
import com.amazonscale.order.enums.PaymentMethod;
import com.amazonscale.order.exception.EmptyCartException;
import com.amazonscale.order.exception.InvalidOrderStatusTransitionException;
import com.amazonscale.order.exception.OrderNotFoundException;
import com.amazonscale.order.repository.OrderRepository;
import com.amazonscale.product.entity.Product;
import com.amazonscale.product.exception.ProductInactiveException;
import com.amazonscale.product.repository.ProductRepository;
import com.amazonscale.user.entity.User;
import com.amazonscale.user.enums.Role;
import com.amazonscale.user.exception.UserNotFoundException;
import com.amazonscale.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;
    private CreateOrderRequest createOrderRequest;
    private Order order;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("Order")
                .lastName("User")
                .email("orderuser@example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        product = Product.builder()
                .id(10L)
                .name("Headphones")
                .price(new BigDecimal("100.00"))
                .stock(50)
                .active(true)
                .build();

        cartItem = CartItem.builder()
                .id(100L)
                .product(product)
                .quantity(2)
                .priceAtAddition(new BigDecimal("100.00"))
                .build();

        cart = Cart.builder()
                .id(1000L)
                .user(user)
                .cartItems(new ArrayList<>(List.of(cartItem)))
                .build();

        createOrderRequest = CreateOrderRequest.builder()
                .shippingAddress("123 Street")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        order = Order.builder()
                .id(500L)
                .user(user)
                .status(OrderStatus.PENDING)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .shippingAddress("123 Street")
                .subtotal(new BigDecimal("200.00"))
                .tax(new BigDecimal("36.00"))
                .shippingFee(new BigDecimal("40.00"))
                .discount(BigDecimal.ZERO)
                .total(new BigDecimal("276.00"))
                .items(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should create order successfully when cart is valid")
    void shouldCreateOrderSuccessfully() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderResponse response = orderService.createOrder(1L, createOrderRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(500L);
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(product.getStock()).isEqualTo(48); // Stock reduced from 50 by 2

        verify(productRepository).saveAll(anyList());
        verify(cartRepository).save(cart);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should apply free shipping when subtotal >= 500")
    void shouldApplyFreeShippingWhenSubtotalHigh() {
        // Arrange
        product.setPrice(new BigDecimal("300.00"));
        cartItem.setPriceAtAddition(new BigDecimal("300.00")); // 300 * 2 = 600

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderResponse response = orderService.createOrder(1L, createOrderRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getShippingFee()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when creating order for non-existent user")
    void shouldThrowUserNotFoundExceptionWhenUserMissing() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(99L, createOrderRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Should throw EmptyCartException when user has empty cart")
    void shouldThrowEmptyCartExceptionWhenCartIsEmpty() {
        // Arrange
        cart.getCartItems().clear();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(1L, createOrderRequest))
                .isInstanceOf(EmptyCartException.class)
                .hasMessage("Cannot place order with an empty cart.");
    }

    @Test
    @DisplayName("Should throw ProductInactiveException when cart contains inactive product")
    void shouldThrowProductInactiveExceptionWhenProductInactive() {
        // Arrange
        product.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(1L, createOrderRequest))
                .isInstanceOf(ProductInactiveException.class)
                .hasMessageContaining("Product is inactive");
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when cart quantity exceeds product stock")
    void shouldThrowInsufficientStockExceptionWhenLowStock() {
        // Arrange
        product.setStock(1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(1L, createOrderRequest))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    @DisplayName("Should get single order by ID and User ID")
    void shouldGetOrderSuccessfully() {
        // Arrange
        when(orderRepository.findByIdAndUser_Id(500L, 1L)).thenReturn(Optional.of(order));

        // Act
        OrderResponse response = orderService.getOrder(1L, 500L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(500L);
    }

    @Test
    @DisplayName("Should cancel PENDING order successfully and restore product stock")
    void shouldCancelPendingOrderSuccessfully() {
        // Arrange
        OrderItem orderItem = OrderItem.builder().product(product).quantity(2).build();
        order.addItem(orderItem);

        when(orderRepository.findByIdAndUser_Id(500L, 1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        // Act
        OrderResponse response = orderService.cancelOrder(1L, 500L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(product.getStock()).isEqualTo(52); // Stock restored from 50 to 52

        verify(productRepository).saveAll(anyList());
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Should throw InvalidOrderStatusTransitionException when cancelling DELIVERED or CANCELLED order")
    void shouldThrowExceptionWhenCancellingInvalidStateOrder() {
        // Arrange
        order.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findByIdAndUser_Id(500L, 1L)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() -> orderService.cancelOrder(1L, 500L))
                .isInstanceOf(InvalidOrderStatusTransitionException.class)
                .hasMessage("Delivered orders cannot be cancelled.");
    }

    @Test
    @DisplayName("Should update order status through valid state transition matrix")
    void shouldUpdateOrderStatusSuccessfully() {
        // Arrange
        when(orderRepository.findById(500L)).thenReturn(Optional.of(order)); // current: PENDING
        when(orderRepository.save(order)).thenReturn(order);

        // Act
        OrderResponse response = orderService.updateOrderStatus(500L, OrderStatus.CONFIRMED);

        // Assert
        assertThat(response).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Should throw InvalidOrderStatusTransitionException for invalid status transition")
    void shouldThrowExceptionForInvalidStatusTransition() {
        // Arrange
        order.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(500L)).thenReturn(Optional.of(order));

        // Act & Assert (PENDING -> DELIVERED is invalid directly)
        assertThatThrownBy(() -> orderService.updateOrderStatus(500L, OrderStatus.DELIVERED))
                .isInstanceOf(InvalidOrderStatusTransitionException.class)
                .hasMessageContaining("Invalid status transition from PENDING to DELIVERED");
    }
}
