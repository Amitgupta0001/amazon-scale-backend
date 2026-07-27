package com.amazonscale.cart.entity;

import com.amazonscale.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CartItemTest {

    @Test
    @DisplayName("Should build CartItem entity using Builder and verify getters/setters")
    void shouldBuildCartItemAndVerifyGettersSetters() {
        // Arrange
        Cart cart = Cart.builder().id(1L).build();
        Product product = Product.builder().id(10L).name("Test Product").build();

        // Act
        CartItem item = CartItem.builder()
                .id(100L)
                .cart(cart)
                .product(product)
                .quantity(3)
                .priceAtAddition(new BigDecimal("19.99"))
                .build();

        // Assert
        assertThat(item.getId()).isEqualTo(100L);
        assertThat(item.getCart()).isEqualTo(cart);
        assertThat(item.getProduct()).isEqualTo(product);
        assertThat(item.getQuantity()).isEqualTo(3);
        assertThat(item.getPriceAtAddition()).isEqualTo(new BigDecimal("19.99"));
    }

    @Test
    @DisplayName("Should populate timestamps on @PrePersist and @PreUpdate")
    void shouldPopulateTimestampsOnPrePersistAndPreUpdate() {
        // Arrange
        CartItem item = new CartItem();

        // Act - PrePersist
        item.prePersist();

        // Assert
        assertThat(item.getCreatedAt()).isNotNull();
        assertThat(item.getUpdatedAt()).isNotNull();

        LocalDateTime initialUpdatedAt = item.getUpdatedAt();

        // Act - PreUpdate
        item.preUpdate();

        // Assert
        assertThat(item.getUpdatedAt()).isNotNull();
        assertThat(item.getUpdatedAt()).isAfterOrEqualTo(initialUpdatedAt);
    }
}
