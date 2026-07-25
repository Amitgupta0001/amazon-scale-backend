package com.amazonscale.order.service.impl;

import com.amazonscale.cart.entity.Cart;
import com.amazonscale.cart.entity.CartItem;
import com.amazonscale.cart.repository.CartRepository;
import com.amazonscale.inventory.exception.InsufficientStockException;
import com.amazonscale.order.dto.CreateOrderRequest;
import com.amazonscale.order.dto.OrderResponse;
import com.amazonscale.order.entity.Order;
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
import com.amazonscale.user.exception.UserNotFoundException;
import com.amazonscale.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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

    private User sampleUser;
    private Product sampleProduct;
    private Cart sampleCart;
    private CartItem sampleCartItem;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        sampleProduct = Product.builder()
                .id(100L)
                .name("Test Product")
                .price(new BigDecimal("100.00"))
                .stock(10)
                .active(true)
                .build();

        sampleCartItem = CartItem.builder()
                .id(10L)
                .product(sampleProduct)
                .quantity(2)
                .priceAtAddition(new BigDecimal("100.00"))
                .build();

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(sampleCartItem);

        sampleCart = Cart.builder()
                .id(50L)
                .user(sampleUser)
                .cartItems(cartItems)
                .build();

        createOrderRequest = CreateOrderRequest.builder()
                .shippingAddress("123 Test Street")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();
    }

    // ==========================================
    // createOrder Tests
    // ==========================================

    @Test
    void createOrder_ShouldReturnOrderResponse_WhenCartSubtotalUnder500() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(sampleCart));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(1000L);
            return o;
        });

        OrderResponse response = orderService.createOrder(1L, createOrderRequest);

        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(1000L);
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
        assertThat(response.getShippingAddress()).isEqualTo("123 Test Street");
        assertThat(response.getSubtotal()).isEqualByComparingTo("200.00");
        assertThat(response.getTax()).isEqualByComparingTo("36.00");
        assertThat(response.getShippingFee()).isEqualByComparingTo("40.00");
        assertThat(response.getDiscount()).isEqualByComparingTo("0.00");
        assertThat(response.getTotal()).isEqualByComparingTo("276.00");
        assertThat(response.getItemsQuantity()).isEqualTo(2);

        // Verify inventory reduction
        assertThat(sampleProduct.getStock()).isEqualTo(8);
        verify(productRepository, times(1)).saveAll(anyList());

        // Verify cart cleared
        assertThat(sampleCart.getCartItems()).isEmpty();
        verify(cartRepository, times(1)).save(sampleCart);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_ShouldApplyFreeShipping_WhenSubtotalIs500OrMore() {
        sampleProduct.setPrice(new BigDecimal("300.00"));
        sampleCartItem.setPriceAtAddition(new BigDecimal("300.00"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(sampleCart));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponse response = orderService.createOrder(1L, createOrderRequest);

        assertThat(response.getSubtotal()).isEqualByComparingTo("600.00");
        assertThat(response.getTax()).isEqualByComparingTo("108.00");
        assertThat(response.getShippingFee()).isEqualByComparingTo("0.00");
        assertThat(response.getTotal()).isEqualByComparingTo("708.00");
    }

    @Test
    void createOrder_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(1L, createOrderRequest))
                .isInstanceOf(UserNotFoundException.class);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_ShouldThrowEmptyCartException_WhenCartNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(1L, createOrderRequest))
                .isInstanceOf(EmptyCartException.class)
                .hasMessageContaining("Cart not found for user: 1");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_ShouldThrowEmptyCartException_WhenCartIsEmpty() {
        sampleCart.setCartItems(new ArrayList<>());

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(sampleCart));

        assertThatThrownBy(() -> orderService.createOrder(1L, createOrderRequest))
                .isInstanceOf(EmptyCartException.class)
                .hasMessageContaining("Cannot place order with an empty cart.");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_ShouldThrowProductInactiveException_WhenProductIsInactive() {
        sampleProduct.setActive(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(sampleCart));

        assertThatThrownBy(() -> orderService.createOrder(1L, createOrderRequest))
                .isInstanceOf(ProductInactiveException.class)
                .hasMessageContaining("Product is inactive");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_ShouldThrowInsufficientStockException_WhenStockIsInsufficient() {
        sampleProduct.setStock(1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(sampleCart));

        assertThatThrownBy(() -> orderService.createOrder(1L, createOrderRequest))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Insufficient stock");

        verify(orderRepository, never()).save(any());
    }

    // ==========================================
    // getOrder Tests
    // ==========================================

    @Test
    void getOrder_ShouldReturnOrderResponse_WhenOrderExists() {
        Order order = Order.builder()
                .id(100L)
                .user(sampleUser)
                .status(OrderStatus.PENDING)
                .paymentMethod(PaymentMethod.COD)
                .shippingAddress("Addr")
                .subtotal(new BigDecimal("100.00"))
                .tax(new BigDecimal("18.00"))
                .shippingFee(new BigDecimal("40.00"))
                .discount(BigDecimal.ZERO)
                .total(new BigDecimal("158.00"))
                .build();

        when(orderRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrder(1L, 100L);

        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(100L);
        verify(orderRepository, times(1)).findByIdAndUser_Id(100L, 1L);
    }

    @Test
    void getOrder_ShouldThrowOrderNotFoundException_WhenOrderDoesNotExistOrBelongsToWrongUser() {
        when(orderRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(1L, 100L))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("Order not found with id: 100");
    }

    // ==========================================
    // getOrdersByUserId Tests
    // ==========================================

    @Test
    void getOrdersByUserId_ShouldReturnOrderResponseList_WhenOrdersExist() {
        Order order1 = Order.builder().id(1L).user(sampleUser).status(OrderStatus.PENDING).build();
        Order order2 = Order.builder().id(2L).user(sampleUser).status(OrderStatus.DELIVERED).build();

        when(orderRepository.findByUser_IdOrderByCreatedAtDesc(1L)).thenReturn(List.of(order1, order2));

        List<OrderResponse> responses = orderService.getOrdersByUserId(1L);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getOrderId()).isEqualTo(1L);
        assertThat(responses.get(1).getOrderId()).isEqualTo(2L);
    }

    @Test
    void getOrdersByUserId_ShouldReturnEmptyList_WhenNoOrdersExist() {
        when(orderRepository.findByUser_IdOrderByCreatedAtDesc(1L)).thenReturn(Collections.emptyList());

        List<OrderResponse> responses = orderService.getOrdersByUserId(1L);

        assertThat(responses).isEmpty();
    }

    // ==========================================
    // cancelOrder Tests
    // ==========================================

    @Test
    void cancelOrder_ShouldCancelOrderAndRestoreInventory_WhenOrderIsPending() {
        sampleProduct.setStock(5);
        com.amazonscale.order.entity.OrderItem item = com.amazonscale.order.entity.OrderItem.builder()
                .id(1L)
                .product(sampleProduct)
                .quantity(3)
                .unitPrice(new BigDecimal("50.00"))
                .lineTotal(new BigDecimal("150.00"))
                .build();

        Order order = Order.builder()
                .id(100L)
                .user(sampleUser)
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>(List.of(item)))
                .build();

        when(orderRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponse response = orderService.cancelOrder(1L, 100L);

        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);

        // Verify inventory restored (5 + 3 = 8)
        assertThat(sampleProduct.getStock()).isEqualTo(8);
        verify(productRepository, times(1)).saveAll(anyList());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void cancelOrder_ShouldThrowException_WhenOrderIsAlreadyCancelled() {
        Order order = Order.builder()
                .id(100L)
                .user(sampleUser)
                .status(OrderStatus.CANCELLED)
                .build();

        when(orderRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L, 100L))
                .isInstanceOf(InvalidOrderStatusTransitionException.class)
                .hasMessageContaining("Order is already cancelled.");
    }

    @Test
    void cancelOrder_ShouldThrowException_WhenOrderIsDelivered() {
        Order order = Order.builder()
                .id(100L)
                .user(sampleUser)
                .status(OrderStatus.DELIVERED)
                .build();

        when(orderRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L, 100L))
                .isInstanceOf(InvalidOrderStatusTransitionException.class)
                .hasMessageContaining("Delivered orders cannot be cancelled.");
    }

    @Test
    void cancelOrder_ShouldThrowOrderNotFoundException_WhenOrderNotFound() {
        when(orderRepository.findByIdAndUser_Id(100L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancelOrder(1L, 100L))
                .isInstanceOf(OrderNotFoundException.class);
    }

    // ==========================================
    // updateOrderStatus Tests
    // ==========================================

    @Test
    void updateOrderStatus_ShouldTransitionFromPendingToConfirmed() {
        Order order = Order.builder().id(100L).status(OrderStatus.PENDING).build();

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponse response = orderService.updateOrderStatus(100L, OrderStatus.CONFIRMED);

        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void updateOrderStatus_ShouldTransitionFromPendingToCancelled() {
        Order order = Order.builder().id(100L).status(OrderStatus.PENDING).build();

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponse response = orderService.updateOrderStatus(100L, OrderStatus.CANCELLED);

        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void updateOrderStatus_ShouldTransitionFromConfirmedToShipped() {
        Order order = Order.builder().id(100L).status(OrderStatus.CONFIRMED).build();

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponse response = orderService.updateOrderStatus(100L, OrderStatus.SHIPPED);

        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void updateOrderStatus_ShouldTransitionFromConfirmedToCancelled() {
        Order order = Order.builder().id(100L).status(OrderStatus.CONFIRMED).build();

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponse response = orderService.updateOrderStatus(100L, OrderStatus.CANCELLED);

        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void updateOrderStatus_ShouldTransitionFromShippedToDelivered() {
        Order order = Order.builder().id(100L).status(OrderStatus.SHIPPED).build();

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponse response = orderService.updateOrderStatus(100L, OrderStatus.DELIVERED);

        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void updateOrderStatus_ShouldThrowException_WhenInvalidTransition() {
        Order order = Order.builder().id(100L).status(OrderStatus.PENDING).build();

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrderStatus(100L, OrderStatus.DELIVERED))
                .isInstanceOf(InvalidOrderStatusTransitionException.class)
                .hasMessageContaining("Invalid status transition from PENDING to DELIVERED");
    }

    @Test
    void updateOrderStatus_ShouldThrowException_WhenOrderIsCancelled() {
        Order order = Order.builder().id(100L).status(OrderStatus.CANCELLED).build();

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrderStatus(100L, OrderStatus.CONFIRMED))
                .isInstanceOf(InvalidOrderStatusTransitionException.class)
                .hasMessageContaining("Cancelled orders cannot change status.");
    }

    @Test
    void updateOrderStatus_ShouldThrowException_WhenOrderIsDelivered() {
        Order order = Order.builder().id(100L).status(OrderStatus.DELIVERED).build();

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrderStatus(100L, OrderStatus.SHIPPED))
                .isInstanceOf(InvalidOrderStatusTransitionException.class)
                .hasMessageContaining("Delivered orders cannot change status.");
    }

    @Test
    void updateOrderStatus_ShouldThrowOrderNotFoundException_WhenOrderNotFound() {
        when(orderRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrderStatus(100L, OrderStatus.CONFIRMED))
                .isInstanceOf(OrderNotFoundException.class);
    }
}
