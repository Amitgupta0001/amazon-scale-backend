package com.amazonscale.category.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateCategoryRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should correctly set and get fields using getters and setters")
    void shouldSetAndGetFieldsCorrectly() {
        // Arrange
        UpdateCategoryRequest request = new UpdateCategoryRequest();

        // Act
        request.setName("Fashion");
        request.setDescription("Apparel and accessories");
        request.setImageUrl("https://example.com/fashion.jpg");
        request.setParentCategoryId(2L);

        // Assert
        assertThat(request.getName()).isEqualTo("Fashion");
        assertThat(request.getDescription()).isEqualTo("Apparel and accessories");
        assertThat(request.getImageUrl()).isEqualTo("https://example.com/fashion.jpg");
        assertThat(request.getParentCategoryId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should pass validation when name is valid")
    void shouldPassValidationWithValidName() {
        // Arrange
        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setName("Updated Electronics");

        // Act
        Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when name is blank")
    void shouldFailValidationWhenNameIsBlank() {
        // Arrange
        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setName("   ");

        // Act
        Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }
}
