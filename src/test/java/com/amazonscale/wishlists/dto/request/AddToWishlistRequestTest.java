package com.amazonscale.wishlists.dto.request;

import com.amazonscale.wishlists.enums.WishlistPriority;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AddToWishlistRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should build AddToWishlistRequest using Builder and verify default priority and getters/setters")
    void shouldBuildAddToWishlistRequestAndVerifyGettersSetters() {
        // Act
        AddToWishlistRequest request = AddToWishlistRequest.builder()
                .wishlistId(1L)
                .productId(10L)
                .note("Must buy on sale")
                .build();

        // Assert
        assertThat(request.getWishlistId()).isEqualTo(1L);
        assertThat(request.getProductId()).isEqualTo(10L);
        assertThat(request.getPriority()).isEqualTo(WishlistPriority.MEDIUM); // Default
        assertThat(request.getNote()).isEqualTo("Must buy on sale");

        // Act - Setter
        request.setPriority(WishlistPriority.HIGH);

        // Assert
        assertThat(request.getPriority()).isEqualTo(WishlistPriority.HIGH);
    }

    @Test
    @DisplayName("Should pass validation with valid wishlistId and productId")
    void shouldPassValidationWithValidFields() {
        // Arrange
        AddToWishlistRequest request = AddToWishlistRequest.builder()
                .wishlistId(1L)
                .productId(2L)
                .build();

        // Act
        Set<ConstraintViolation<AddToWishlistRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when wishlistId or productId is null")
    void shouldFailValidationWhenIdsAreNull() {
        // Arrange
        AddToWishlistRequest request = new AddToWishlistRequest();

        // Act
        Set<ConstraintViolation<AddToWishlistRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(2);
    }
}
