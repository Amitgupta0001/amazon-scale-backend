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

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private User sampleUser;
    private Product activeProduct;
    private Product inactiveProduct;
    private Cart sampleCart;
    private CartItem sampleCartItem;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .build();

        activeProduct = Product.builder()
                .id(10L)
                .name("Laptop")
                .description("Powerful Laptop")
                .price(new BigDecimal("999.99"))
                .stock(10)
                .active(true)
                .imageUrl("https://example.com/laptop.jpg")
                .build();

        inactiveProduct = Product.builder()
                .id(20L)
                .name("Old Phone")
                .stock(5)
                .active(false)
                .build();

        sampleCart = Cart.builder()
                .id(100L)
                .user(sampleUser)
                .cartItems(new ArrayList<>())
                .build();

        sampleCartItem = CartItem.builder()
                .id(1000L)
                .cart(sampleCart)
                .product(activeProduct)
                .quantity(2)
                .priceAtAddition(activeProduct.getPrice())
                .build();
    }

    @Test
    void addItemToCart_NewItem_Success() {
        // Arrange
        AddToCartRequest request = AddToCartRequest.builder()
                .productId(10L)
                .quantity(2)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(productRepository.findById(10L)).thenReturn(Optional.of(activeProduct));
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(sampleCart));
        when(cartItemRepository.findByCart_IdAndProduct_Id(100L, 10L)).thenReturn(Optional.empty());

        // Act
        CartResponse response = cartService.addItemToCart(1L, request);

        // Assert
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void addItemToCart_ExistingItem_Success() {
        // Arrange
        AddToCartRequest request = AddToCartRequest.builder()
                .productId(10L)
                .quantity(3)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(productRepository.findById(10L)).thenReturn(Optional.of(activeProduct));
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(sampleCart));
        when(cartItemRepository.findByCart_IdAndProduct_Id(100L, 10L)).thenReturn(Optional.of(sampleCartItem));

        // Act
        CartResponse response = cartService.addItemToCart(1L, request);

        // Assert
        assertThat(sampleCartItem.getQuantity()).isEqualTo(5); // 2 + 3
        verify(cartItemRepository, times(1)).save(sampleCartItem);
    }

    @Test
    void addItemToCart_UserNotFound_ThrowsException() {
        AddToCartRequest request = AddToCartRequest.builder().productId(10L).quantity(1).build();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addItemToCart(1L, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void addItemToCart_ProductNotFound_ThrowsException() {
        AddToCartRequest request = AddToCartRequest.builder().productId(99L).quantity(1).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addItemToCart(1L, request))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void addItemToCart_ProductInactive_ThrowsException() {
        AddToCartRequest request = AddToCartRequest.builder().productId(20L).quantity(1).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(productRepository.findById(20L)).thenReturn(Optional.of(inactiveProduct));

        assertThatThrownBy(() -> cartService.addItemToCart(1L, request))
                .isInstanceOf(ProductUnavailableException.class);
    }

    @Test
    void addItemToCart_InsufficientStock_ThrowsException() {
        AddToCartRequest request = AddToCartRequest.builder().productId(10L).quantity(100).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(productRepository.findById(10L)).thenReturn(Optional.of(activeProduct));
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(sampleCart));

        assertThatThrownBy(() -> cartService.addItemToCart(1L, request))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void updateCartItem_Success() {
        // Arrange
        UpdateCartItemRequest request = UpdateCartItemRequest.builder().quantity(5).build();
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(sampleCart));
        when(cartItemRepository.findByCart_IdAndProduct_Id(100L, 10L)).thenReturn(Optional.of(sampleCartItem));

        // Act
        CartResponse response = cartService.updateCartItem(1L, 10L, request);

        // Assert
        assertThat(sampleCartItem.getQuantity()).isEqualTo(5);
        verify(cartItemRepository, times(1)).save(sampleCartItem);
    }

    @Test
    void updateCartItem_CartNotFound_ThrowsException() {
        UpdateCartItemRequest request = UpdateCartItemRequest.builder().quantity(5).build();
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.updateCartItem(1L, 10L, request))
                .isInstanceOf(CartNotFoundException.class);
    }

    @Test
    void updateCartItem_ItemNotFound_ThrowsException() {
        UpdateCartItemRequest request = UpdateCartItemRequest.builder().quantity(5).build();
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(sampleCart));
        when(cartItemRepository.findByCart_IdAndProduct_Id(100L, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.updateCartItem(1L, 10L, request))
                .isInstanceOf(CartItemNotFoundException.class);
    }

    @Test
    void removeCartItem_Success() {
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(sampleCart));
        when(cartItemRepository.findByCart_IdAndProduct_Id(100L, 10L)).thenReturn(Optional.of(sampleCartItem));

        cartService.removeCartItem(1L, 10L);

        verify(cartItemRepository, times(1)).deleteByCart_IdAndProduct_Id(100L, 10L);
    }

    @Test
    void clearCart_Success() {
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(sampleCart));

        cartService.clearCart(1L);

        verify(cartItemRepository, times(1)).deleteByCart_Id(100L);
    }

    @Test
    void getCart_Success() {
        when(cartRepository.findByUser_Id(1L)).thenReturn(Optional.of(sampleCart));

        cartService.getCart(1L);

        verify(cartRepository, times(1)).findByUser_Id(1L);
    }
}
