package com.amazonscale.wishlists.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MoveWishlistItemRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should build MoveWishlistItemRequest using Builder and verify getters/setters")
    void shouldBuildMoveWishlistItemRequestAndVerifyGettersSetters() {
        // Act
        MoveWishlistItemRequest request = MoveWishlistItemRequest.builder()
                .sourceWishlistId(1L)
                .destinationWishlistId(2L)
                .productId(100L)
                .build();

        // Assert
        assertThat(request.getSourceWishlistId()).isEqualTo(1L);
        assertThat(request.getDestinationWishlistId()).isEqualTo(2L);
        assertThat(request.getProductId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("Should pass validation when all IDs are non-null")
    void shouldPassValidationWithValidFields() {
        // Arrange
        MoveWishlistItemRequest request = MoveWishlistItemRequest.builder()
                .sourceWishlistId(10L)
                .destinationWishlistId(20L)
                .productId(30L)
                .build();

        // Act
        Set<ConstraintViolation<MoveWishlistItemRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when any ID is null")
    void shouldFailValidationWhenFieldsAreNull() {
        // Arrange
        MoveWishlistItemRequest request = new MoveWishlistItemRequest();

        // Act
        Set<ConstraintViolation<MoveWishlistItemRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(3);
    }
}
