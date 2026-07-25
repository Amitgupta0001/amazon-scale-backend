package com.amazonscale.order.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentMethodTest {

    @Test
    void testEnumValues() {
        assertThat(PaymentMethod.values()).containsExactly(
                PaymentMethod.COD,
                PaymentMethod.UPI,
                PaymentMethod.CREDIT_CARD,
                PaymentMethod.DEBIT_CARD,
                PaymentMethod.NET_BANKING
        );
        assertThat(PaymentMethod.valueOf("COD")).isEqualTo(PaymentMethod.COD);
    }
}
