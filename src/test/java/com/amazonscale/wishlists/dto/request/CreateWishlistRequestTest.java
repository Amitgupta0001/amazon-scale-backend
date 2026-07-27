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

class CreateWishlistRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should build CreateWishlistRequest using Builder and verify getters/setters")
    void shouldBuildCreateWishlistRequestAndVerifyGettersSetters() {
        // Act
        CreateWishlistRequest request = CreateWishlistRequest.builder()
                .name("Birthday Gifts")
                .description("Items I want for my birthday")
                .build();

        // Assert
        assertThat(request.getName()).isEqualTo("Birthday Gifts");
        assertThat(request.getDescription()).isEqualTo("Items I want for my birthday");

        // Act - Setters
        request.setName("Holiday Tech");
        request.setDescription("Tech gadgets for holidays");

        // Assert
        assertThat(request.getName()).isEqualTo("Holiday Tech");
        assertThat(request.getDescription()).isEqualTo("Tech gadgets for holidays");
    }

    @Test
    @DisplayName("Should pass validation with valid name and description")
    void shouldPassValidationWithValidFields() {
        // Arrange
        CreateWishlistRequest request = CreateWishlistRequest.builder()
                .name("Favorites")
                .description("Short description")
                .build();

        // Act
        Set<ConstraintViolation<CreateWishlistRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when name is blank or exceeds 100 chars")
    void shouldFailValidationWhenNameIsBlankOrTooLong() {
        // Arrange - Blank name
        CreateWishlistRequest blankReq = CreateWishlistRequest.builder().name("   ").build();
        // Arrange - Long name
        CreateWishlistRequest longReq = CreateWishlistRequest.builder().name("a".repeat(101)).build();

        // Act & Assert
        assertThat(validator.validate(blankReq)).hasSize(1);
        assertThat(validator.validate(longReq)).hasSize(1);
    }
}
