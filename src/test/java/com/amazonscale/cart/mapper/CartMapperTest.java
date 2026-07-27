package com.amazonscale.cart.mapper;

import com.amazonscale.cart.dto.CartItemResponse;
import com.amazonscale.cart.dto.CartResponse;
import com.amazonscale.cart.entity.Cart;
import com.amazonscale.cart.entity.CartItem;
import com.amazonscale.cart.entity.CurrencyCode;
import com.amazonscale.product.entity.Product;
import com.amazonscale.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CartMapperTest {

    @Test
    @DisplayName("Should map CartItem entity to CartItemResponse DTO correctly")
    void shouldMapCartItemToCartItemResponse() {
        // Arrange
        Product product = Product.builder()
                .id(10L)
                .name("Wireless Headset")
                .description("Noise cancelling")
                .imageUrl("https://example.com/headset.jpg")
                .build();

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .priceAtAddition(new BigDecimal("99.99"))
                .build();

        // Act
        CartItemResponse response = CartMapper.toCartItemResponse(cartItem);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getCartItemId()).isEqualTo(1L);
        assertThat(response.getProductId()).isEqualTo(10L);
        assertThat(response.getProductName()).isEqualTo("Wireless Headset");
        assertThat(response.getProductDescription()).isEqualTo("Noise cancelling");
        assertThat(response.getUnitPrice()).isEqualTo(new BigDecimal("99.99"));
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getSubtotal()).isEqualTo(new BigDecimal("199.98"));
        assertThat(response.getImageUrl()).isEqualTo("https://example.com/headset.jpg");
    }

    @Test
    @DisplayName("Should map Cart entity with items to CartResponse DTO correctly")
    void shouldMapCartToCartResponse() {
        // Arrange
        User user = User.builder().id(5L).build();
        LocalDateTime now = LocalDateTime.now();

        Product product1 = Product.builder().id(10L).name("P1").price(new BigDecimal("10.00")).build();
        Product product2 = Product.builder().id(11L).name("P2").price(new BigDecimal("25.00")).build();

        CartItem item1 = CartItem.builder().id(1L).product(product1).quantity(2).priceAtAddition(new BigDecimal("10.00")).build();
        CartItem item2 = CartItem.builder().id(2L).product(product2).quantity(1).priceAtAddition(new BigDecimal("25.00")).build();

        Cart cart = Cart.builder()
                .id(100L)
                .user(user)
                .cartItems(List.of(item1, item2))
                .updatedAt(now)
                .build();

        // Act
        CartResponse response = CartMapper.toCartResponse(cart);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getCartId()).isEqualTo(100L);
        assertThat(response.getUserId()).isEqualTo(5L);
        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getTotalItems()).isEqualTo(3); // 2 + 1
        assertThat(response.getTotalAmount()).isEqualTo(new BigDecimal("45.00")); // (10*2) + (25*1)
        assertThat(response.getUpdatedAt()).isEqualTo(now);
        assertThat(response.getCurrency()).isEqualTo(CurrencyCode.INR);
    }

    @Test
    @DisplayName("Should instantiate private constructor via reflection for test coverage")
    void shouldInstantiatePrivateConstructorForCoverage() throws Exception {
        // Arrange
        Constructor<CartMapper> constructor = CartMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // Act
        CartMapper instance = constructor.newInstance();

        // Assert
        assertThat(instance).isNotNull();
    }
}
