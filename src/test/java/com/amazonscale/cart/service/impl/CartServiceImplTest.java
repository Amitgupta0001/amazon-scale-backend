package com.amazonscale.cart.service.impl;

import com.amazonscale.cart.dto.AddToCartRequest;
import com.amazonscale.cart.dto.CartResponse;
import com.amazonscale.cart.dto.UpdateCartItemRequest;
import com.amazonscale.cart.entity.Cart;
import com.amazonscale.cart.entity.CartItem;
import com.amazonscale.cart.exception.CartItemNotFoundException;
import com.amazonscale.cart.exception.CartNotFoundException;
import com.amazonscale.cart.mapper.CartMapper;
import com.amazonscale.cart.repository.CartItemRepository;
import com.amazonscale.cart.repository.CartRepository;
import com.amazonscale.inventory.exception.InsufficientStockException;
import com.amazonscale.product.entity.Product;
import com.amazonscale.product.exception.ProductNotFoundException;
import com.amazonscale.product.exception.ProductUnavailableException;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;


    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;
    private AddToCartRequest addToCartRequest;
    private UpdateCartItemRequest updateCartItemRequest;
    private CartResponse expectedCartResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("testuser@example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        product = Product.builder()
                .id(10L)
                .name("Smartphone")
                .price(new BigDecimal("699.99"))
                .stock(50)
                .active(true)
                .build();

        cart = Cart.builder()
                .id(100L)
                .user(user)
                .cartItems(new ArrayList<>())
                .build();

        cartItem = CartItem.builder()
                .id(1000L)
                .cart(cart)
                .product(product)
                .quantity(2)
                .priceAtAddition(new BigDecimal("699.99"))
                .build();

        addToCartRequest = AddToCartRequest.builder()
                .productId(10L)
                .quantity(2)
                .build();

        updateCartItemRequest = UpdateCartItemRequest.builder()
                .quantity(5)
                .build();

        expectedCartResponse = CartResponse.builder()
                .cartId(100L)
                .userId(1L)
                .totalItems(2)
                .totalAmount(new BigDecimal("1399.98"))
                .build();
    }

    @Test
    @DisplayName("Should add new item to cart successfully")
    void shouldAddNewItemToCartSuccessfully() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCart_IdAndProduct_Id(100L, 10L)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        // Act
        CartResponse response = cartService.addItemToCart(1L, addToCartRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getCartId()).isEqualTo(cart.getId());
        assertThat(response.getUserId()).isEqualTo(user.getId());

        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should increment quantity when item already exists in cart")
    void shouldIncrementQuantityWhenItemExistsInCart() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCart_IdAndProduct_Id(100L, 10L)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        // Act
        CartResponse response = cartService.addItemToCart(1L, addToCartRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(cartItem.getQuantity()).isEqualTo(4); // 2 + 2

        verify(cartItemRepository).save(cartItem);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when adding item for non-existent user")
    void shouldThrowUserNotFoundExceptionWhenUserMissing() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cartService.addItemToCart(99L, addToCartRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when adding non-existent product")
    void shouldThrowProductNotFoundExceptionWhenProductMissing() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cartService.addItemToCart(1L, addToCartRequest))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("10");
    }

    @Test
    @DisplayName("Should throw ProductUnavailableException when adding inactive product to cart")
    void shouldThrowProductUnavailableExceptionWhenProductInactive() {
        // Arrange
        product.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        // Act & Assert
        assertThatThrownBy(() -> cartService.addItemToCart(1L, addToCartRequest))
                .isInstanceOf(ProductUnavailableException.class)
                .hasMessageContaining("10");
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when adding quantity exceeds available stock")
    void shouldThrowInsufficientStockExceptionWhenExceedsStock() {
        // Arrange
        product.setStock(1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCart_IdAndProduct_Id(100L, 10L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cartService.addItemToCart(1L, addToCartRequest))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Insufficient stock for product 10");
    }

    @Test
    @DisplayName("Should update cart item quantity successfully")
    void shouldUpdateCartItemQuantitySuccessfully() {
        // Arrange
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCart_IdAndProduct_Id(100L, 10L)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);


        // Act
        CartResponse response = cartService.updateCartItem(1L, 10L, updateCartItemRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(cartItem.getQuantity()).isEqualTo(5);

        verify(cartItemRepository).save(cartItem);
    }

    @Test
    @DisplayName("Should throw CartItemNotFoundException when updating item not present in cart")
    void shouldThrowCartItemNotFoundExceptionWhenUpdatingMissingItem() {
        // Arrange
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCart_IdAndProduct_Id(100L, 10L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cartService.updateCartItem(1L, 10L, updateCartItemRequest))
                .isInstanceOf(CartItemNotFoundException.class)
                .hasMessageContaining("10");
    }

    @Test
    @DisplayName("Should remove cart item successfully")
    void shouldRemoveCartItemSuccessfully() {
        // Arrange
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCart_IdAndProduct_Id(100L, 10L)).thenReturn(Optional.of(cartItem));

        // Act
        cartService.removeCartItem(1L, 10L);

        // Assert
        verify(cartItemRepository).deleteByCart_IdAndProduct_Id(100L, 10L);
    }

    @Test
    @DisplayName("Should clear cart successfully")
    void shouldClearCartSuccessfully() {
        // Arrange
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));

        // Act
        cartService.clearCart(1L);

        // Assert
        verify(cartItemRepository).deleteByCart_Id(100L);
    }

    @Test
    @DisplayName("Should get user cart successfully")
    void shouldGetCartSuccessfully() {
        // Arrange
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(cart));

        // Act
        CartResponse response = cartService.getCart(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getCartId()).isEqualTo(cart.getId());
        assertThat(response.getUserId()).isEqualTo(user.getId());

        verify(cartRepository).findByUser_Id(1L);
    }

    @Test
    @DisplayName("Should throw CartNotFoundException when cart does not exist for user")
    void shouldThrowCartNotFoundExceptionWhenCartMissing() {
        // Arrange
        when(cartRepository.findByUser_Id(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cartService.getCart(99L))
                .isInstanceOf(CartNotFoundException.class)
                .hasMessageContaining("99");
    }
}
