package com.amazonscale.order.dto;

import com.amazonscale.order.enums.PaymentMethod;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Should build CreateOrderRequest using Builder and verify getters/setters")
    void shouldBuildCreateOrderRequestAndVerifyGettersSetters() {
        // Act
        CreateOrderRequest request = CreateOrderRequest.builder()
                .shippingAddress("123 Main St, City")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .build();

        // Assert
        assertThat(request.getShippingAddress()).isEqualTo("123 Main St, City");
        assertThat(request.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);

        // Act - Setters
        request.setShippingAddress("456 Park Ave");
        request.setPaymentMethod(PaymentMethod.UPI);

        // Assert
        assertThat(request.getShippingAddress()).isEqualTo("456 Park Ave");
        assertThat(request.getPaymentMethod()).isEqualTo(PaymentMethod.UPI);
    }

    @Test
    @DisplayName("Should pass validation when all fields are valid")
    void shouldPassValidationWithValidFields() {
        // Arrange
        CreateOrderRequest request = CreateOrderRequest.builder()
                .shippingAddress("123 Street")
                .paymentMethod(PaymentMethod.COD)
                .build();

        // Act
        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when shippingAddress is blank or paymentMethod is null")
    void shouldFailValidationWhenInvalidFields() {
        // Arrange
        CreateOrderRequest request = CreateOrderRequest.builder()
                .shippingAddress("")
                .paymentMethod(null)
                .build();

        // Act
        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(2);
    }
}
