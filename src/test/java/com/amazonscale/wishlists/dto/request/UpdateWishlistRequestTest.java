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

class UpdateWishlistRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should build UpdateWishlistRequest using Builder and verify getters/setters")
    void shouldBuildUpdateWishlistRequestAndVerifyGettersSetters() {
        // Act
        UpdateWishlistRequest request = UpdateWishlistRequest.builder()
                .name("Updated Electronics")
                .description("Updated description")
                .build();

        // Assert
        assertThat(request.getName()).isEqualTo("Updated Electronics");
        assertThat(request.getDescription()).isEqualTo("Updated description");

        // Act - Setters
        request.setName("New Name");
        request.setDescription("New Description");

        // Assert
        assertThat(request.getName()).isEqualTo("New Name");
        assertThat(request.getDescription()).isEqualTo("New Description");
    }

    @Test
    @DisplayName("Should pass validation with valid name and description")
    void shouldPassValidationWithValidFields() {
        // Arrange
        UpdateWishlistRequest request = UpdateWishlistRequest.builder()
                .name("Valid Name")
                .build();

        // Act
        Set<ConstraintViolation<UpdateWishlistRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when name is blank")
    void shouldFailValidationWhenNameIsBlank() {
        // Arrange
        UpdateWishlistRequest request = UpdateWishlistRequest.builder().name("").build();

        // Act
        Set<ConstraintViolation<UpdateWishlistRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(1);
    }
}
