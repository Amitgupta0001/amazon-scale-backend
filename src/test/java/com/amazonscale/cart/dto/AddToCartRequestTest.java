package com.amazonscale.cart.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AddToCartRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should build AddToCartRequest using Builder and verify getters and setters")
    void shouldBuildAddToCartRequestAndVerifyGettersSetters() {
        // Act
        AddToCartRequest request = AddToCartRequest.builder()
                .productId(101L)
                .quantity(2)
                .build();

        // Assert
        assertThat(request.getProductId()).isEqualTo(101L);
        assertThat(request.getQuantity()).isEqualTo(2);

        // Act - Setters
        request.setProductId(202L);
        request.setQuantity(5);

        // Assert
        assertThat(request.getProductId()).isEqualTo(202L);
        assertThat(request.getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should pass validation when fields are valid")
    void shouldPassValidationWithValidFields() {
        // Arrange
        AddToCartRequest request = AddToCartRequest.builder()
                .productId(1L)
                .quantity(1)
                .build();

        // Act
        Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when productId or quantity is null")
    void shouldFailValidationWhenNullFields() {
        // Arrange
        AddToCartRequest request = new AddToCartRequest();

        // Act
        Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(2);
    }

    @Test
    @DisplayName("Should fail validation when quantity is zero or negative")
    void shouldFailValidationWhenQuantityZeroOrNegative() {
        // Arrange
        AddToCartRequest request = AddToCartRequest.builder()
                .productId(1L)
                .quantity(0)
                .build();

        // Act
        Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("quantity");
    }
}
