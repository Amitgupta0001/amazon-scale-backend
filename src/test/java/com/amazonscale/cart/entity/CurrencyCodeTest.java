package com.amazonscale.cart.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CurrencyCodeTest {

    @Test
    void testCurrencyCodeValues() {
        CurrencyCode[] codes = CurrencyCode.values();
        assertThat(codes).contains(CurrencyCode.INR, CurrencyCode.USD, CurrencyCode.EUR);
        assertThat(CurrencyCode.valueOf("INR")).isEqualTo(CurrencyCode.INR);
    }
}
