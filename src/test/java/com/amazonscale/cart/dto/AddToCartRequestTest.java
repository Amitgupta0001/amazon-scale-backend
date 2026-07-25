package com.amazonscale.cart.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddToCartRequestTest {

    @Test
    void testAddToCartRequestGettersSettersBuilder() {
        AddToCartRequest request = AddToCartRequest.builder()
                .productId(10L)
                .quantity(3)
                .build();

        assertThat(request.getProductId()).isEqualTo(10L);
        assertThat(request.getQuantity()).isEqualTo(3);

        request.setProductId(20L);
        request.setQuantity(5);

        assertThat(request.getProductId()).isEqualTo(20L);
        assertThat(request.getQuantity()).isEqualTo(5);
    }

    @Test
    void testNoArgsConstructorAndAllArgsConstructor() {
        AddToCartRequest emptyRequest = new AddToCartRequest();
        assertThat(emptyRequest.getProductId()).isNull();

        AddToCartRequest fullRequest = new AddToCartRequest(1L, 2);
        assertThat(fullRequest.getProductId()).isEqualTo(1L);
        assertThat(fullRequest.getQuantity()).isEqualTo(2);
    }
}
