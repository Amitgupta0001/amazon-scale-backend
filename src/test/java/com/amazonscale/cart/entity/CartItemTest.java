package com.amazonscale.cart.entity;

import com.amazonscale.product.entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CartItemTest {

    @Test
    void testCartItemGettersSettersPrePersistPreUpdate() {
        Cart cart = Cart.builder().id(1L).build();
        Product product = Product.builder().id(10L).name("Test Product").build();

        CartItem item = CartItem.builder()
                .id(100L)
                .cart(cart)
                .product(product)
                .quantity(2)
                .priceAtAddition(new BigDecimal("99.99"))
                .build();

        assertThat(item.getId()).isEqualTo(100L);
        assertThat(item.getCart().getId()).isEqualTo(1L);
        assertThat(item.getProduct().getId()).isEqualTo(10L);
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getPriceAtAddition()).isEqualTo(new BigDecimal("99.99"));

        item.prePersist();
        assertThat(item.getCreatedAt()).isNotNull();
        assertThat(item.getUpdatedAt()).isNotNull();

        item.preUpdate();
        assertThat(item.getUpdatedAt()).isNotNull();
    }
}
