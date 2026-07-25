package com.amazonscale.order.dto;

import com.amazonscale.order.enums.PaymentMethod;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreateOrderRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testCreateOrderRequestValid() {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .shippingAddress("123 Street, City")
                .paymentMethod(PaymentMethod.UPI)
                .build();

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
        assertThat(request.getShippingAddress()).isEqualTo("123 Street, City");
        assertThat(request.getPaymentMethod()).isEqualTo(PaymentMethod.UPI);
    }

    @Test
    void testCreateOrderRequestValidationFailures() {
        CreateOrderRequest request = new CreateOrderRequest();

        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(2);
    }
}
