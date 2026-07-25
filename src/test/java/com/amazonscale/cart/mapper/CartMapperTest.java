package com.amazonscale.cart.mapper;

import com.amazonscale.cart.dto.CartItemResponse;
import com.amazonscale.cart.dto.CartResponse;
import com.amazonscale.cart.entity.Cart;
import com.amazonscale.cart.entity.CartItem;
import com.amazonscale.cart.entity.CurrencyCode;
import com.amazonscale.product.entity.Product;
import com.amazonscale.user.entity.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CartMapperTest {

    @Test
    void testToCartItemResponse() {
        Product product = Product.builder()
                .id(10L)
                .name("Keyboard")
                .description("Mechanical keyboard")
                .imageUrl("https://example.com/keyboard.jpg")
                .price(new BigDecimal("99.99"))
                .build();

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .product(product)
                .quantity(3)
                .priceAtAddition(new BigDecimal("99.99"))
                .build();

        CartItemResponse response = CartMapper.toCartItemResponse(cartItem);

        assertThat(response).isNotNull();
        assertThat(response.getCartItemId()).isEqualTo(1L);
        assertThat(response.getProductId()).isEqualTo(10L);
        assertThat(response.getProductName()).isEqualTo("Keyboard");
        assertThat(response.getProductDescription()).isEqualTo("Mechanical keyboard");
        assertThat(response.getUnitPrice()).isEqualTo(new BigDecimal("99.99"));
        assertThat(response.getQuantity()).isEqualTo(3);
        assertThat(response.getSubtotal()).isEqualTo(new BigDecimal("299.97"));
        assertThat(response.getImageUrl()).isEqualTo("https://example.com/keyboard.jpg");
    }

    @Test
    void testToCartResponse() {
        User user = User.builder().id(5L).build();

        Product product1 = Product.builder().id(10L).name("P1").description("D1").price(new BigDecimal("50.00")).imageUrl("u1").build();
        Product product2 = Product.builder().id(11L).name("P2").description("D2").price(new BigDecimal("30.00")).imageUrl("u2").build();

        CartItem item1 = CartItem.builder().id(1L).product(product1).quantity(2).priceAtAddition(new BigDecimal("50.00")).build();
        CartItem item2 = CartItem.builder().id(2L).product(product2).quantity(1).priceAtAddition(new BigDecimal("30.00")).build();

        LocalDateTime now = LocalDateTime.now();
        Cart cart = Cart.builder()
                .id(100L)
                .user(user)
                .cartItems(List.of(item1, item2))
                .updatedAt(now)
                .build();

        CartResponse response = CartMapper.toCartResponse(cart);

        assertThat(response).isNotNull();
        assertThat(response.getCartId()).isEqualTo(100L);
        assertThat(response.getUserId()).isEqualTo(5L);
        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getTotalItems()).isEqualTo(3);
        assertThat(response.getTotalAmount()).isEqualTo(new BigDecimal("130.00"));
        assertThat(response.getCurrency()).isEqualTo(CurrencyCode.INR);
    }
}
