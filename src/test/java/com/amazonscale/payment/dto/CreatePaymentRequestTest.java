package com.amazonscale.payment.dto;

import com.amazonscale.payment.enums.PaymentGateway;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreatePaymentRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should build CreatePaymentRequest using Builder and verify getters/setters")
    void shouldBuildCreatePaymentRequestAndVerifyGettersSetters() {
        // Act
        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .orderId(100L)
                .gateway(PaymentGateway.RAZORPAY)
                .build();

        // Assert
        assertThat(request.getOrderId()).isEqualTo(100L);
        assertThat(request.getGateway()).isEqualTo(PaymentGateway.RAZORPAY);

        // Act - Setters
        request.setOrderId(200L);
        request.setGateway(PaymentGateway.STRIPE);

        // Assert
        assertThat(request.getOrderId()).isEqualTo(200L);
        assertThat(request.getGateway()).isEqualTo(PaymentGateway.STRIPE);
    }

    @Test
    @DisplayName("Should pass validation when all fields are present")
    void shouldPassValidationWithValidFields() {
        // Arrange
        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .orderId(10L)
                .gateway(PaymentGateway.PAYPAL)
                .build();

        // Act
        Set<ConstraintViolation<CreatePaymentRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when orderId or gateway is null")
    void shouldFailValidationWhenFieldsAreNull() {
        // Arrange
        CreatePaymentRequest request = new CreatePaymentRequest();

        // Act
        Set<ConstraintViolation<CreatePaymentRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(2);
    }
}
