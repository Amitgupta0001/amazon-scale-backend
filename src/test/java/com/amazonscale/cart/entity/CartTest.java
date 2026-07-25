package com.amazonscale.cart.entity;

import com.amazonscale.user.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class CartTest {

    @Test
    void testCartGettersSettersPrePersistPreUpdate() {
        User user = User.builder().id(1L).email("test@example.com").build();
        Cart cart = Cart.builder()
                .id(100L)
                .user(user)
                .cartItems(new ArrayList<>())
                .build();

        assertThat(cart.getId()).isEqualTo(100L);
        assertThat(cart.getUser().getEmail()).isEqualTo("test@example.com");

        cart.prePersist();
        assertThat(cart.getCreatedAt()).isNotNull();
        assertThat(cart.getUpdatedAt()).isNotNull();

        LocalDateTime originalUpdatedAt = cart.getUpdatedAt();
        cart.preUpdate();
        assertThat(cart.getUpdatedAt()).isNotNull();
    }
}
