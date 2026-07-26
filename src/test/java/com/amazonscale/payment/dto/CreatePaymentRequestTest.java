package com.amazonscale.payment.dto;

import com.amazonscale.payment.enums.PaymentGateway;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreatePaymentRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGettersAndSetters() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setOrderId(100L);
        request.setGateway(PaymentGateway.STRIPE);

        assertThat(request.getOrderId()).isEqualTo(100L);
        assertThat(request.getGateway()).isEqualTo(PaymentGateway.STRIPE);
    }

    @Test
    void testBuilderAndAllArgsConstructor() {
        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .orderId(200L)
                .gateway(PaymentGateway.RAZORPAY)
                .build();

        assertThat(request.getOrderId()).isEqualTo(200L);
        assertThat(request.getGateway()).isEqualTo(PaymentGateway.RAZORPAY);
    }

    @Test
    void testValidation_Success() {
        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .orderId(1L)
                .gateway(PaymentGateway.PAYPAL)
                .build();

        var violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidation_NullFields() {
        CreatePaymentRequest request = new CreatePaymentRequest();

        var violations = validator.validate(request);
        assertThat(violations).hasSize(2);
    }
}
