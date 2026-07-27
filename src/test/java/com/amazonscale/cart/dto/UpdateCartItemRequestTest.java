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

class UpdateCartItemRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should build UpdateCartItemRequest using Builder pattern and verify getters/setters")
    void shouldBuildUpdateCartItemRequestAndVerifyGettersSetters() {
        // Act
        UpdateCartItemRequest request = UpdateCartItemRequest.builder()
                .quantity(3)
                .build();

        // Assert
        assertThat(request.getQuantity()).isEqualTo(3);

        // Act - Setter
        request.setQuantity(10);

        // Assert
        assertThat(request.getQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should pass validation with positive quantity")
    void shouldPassValidationWithPositiveQuantity() {
        // Arrange
        UpdateCartItemRequest request = UpdateCartItemRequest.builder()
                .quantity(1)
                .build();

        // Act
        Set<ConstraintViolation<UpdateCartItemRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when quantity is null or non-positive")
    void shouldFailValidationWhenQuantityInvalid() {
        // Arrange
        UpdateCartItemRequest nullReq = new UpdateCartItemRequest();
        UpdateCartItemRequest zeroReq = UpdateCartItemRequest.builder().quantity(0).build();

        // Act & Assert
        assertThat(validator.validate(nullReq)).hasSize(1);
        assertThat(validator.validate(zeroReq)).hasSize(1);
    }
}
