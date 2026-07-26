package com.amazonscale.payment.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentGatewayTest {

    @Test
    void testEnumValues() {
        assertThat(PaymentGateway.valueOf("STRIPE")).isEqualTo(PaymentGateway.STRIPE);
        assertThat(PaymentGateway.valueOf("RAZORPAY")).isEqualTo(PaymentGateway.RAZORPAY);
        assertThat(PaymentGateway.valueOf("PHONEPAY")).isEqualTo(PaymentGateway.PHONEPAY);
        assertThat(PaymentGateway.valueOf("BHARATPAY")).isEqualTo(PaymentGateway.BHARATPAY);
        assertThat(PaymentGateway.valueOf("PAYPAL")).isEqualTo(PaymentGateway.PAYPAL);
        assertThat(PaymentGateway.valueOf("COD")).isEqualTo(PaymentGateway.COD);
        assertThat(PaymentGateway.values()).hasSize(6);
    }
}
