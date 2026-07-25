package com.amazonscale.cart.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateCartItemRequestTest {

    @Test
    void testUpdateCartItemRequestGettersSettersBuilder() {
        UpdateCartItemRequest request = UpdateCartItemRequest.builder()
                .quantity(5)
                .build();

        assertThat(request.getQuantity()).isEqualTo(5);

        request.setQuantity(10);
        assertThat(request.getQuantity()).isEqualTo(10);
    }
}
