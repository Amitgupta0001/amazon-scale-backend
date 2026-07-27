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

class CreateCategoryRequestTest {

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
        CreateCategoryRequest request = new CreateCategoryRequest();

        // Act
        request.setName("Electronics");
        request.setDescription("Gadgets and devices");
        request.setImageUrl("https://example.com/electronics.jpg");
        request.setParentCategoryId(1L);

        // Assert
        assertThat(request.getName()).isEqualTo("Electronics");
        assertThat(request.getDescription()).isEqualTo("Gadgets and devices");
        assertThat(request.getImageUrl()).isEqualTo("https://example.com/electronics.jpg");
        assertThat(request.getParentCategoryId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should pass validation when name is valid and optional fields are present")
    void shouldPassValidationWithValidFields() {
        // Arrange
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Home & Kitchen");
        request.setDescription("Household appliances");
        request.setImageUrl("https://example.com/home.jpg");
        request.setParentCategoryId(null);

        // Act
        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when name is blank")
    void shouldFailValidationWhenNameIsBlank() {
        // Arrange
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("");

        // Act
        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    @DisplayName("Should fail validation when field size limits are exceeded")
    void shouldFailValidationWhenFieldSizeExceeded() {
        // Arrange
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("A".repeat(201)); // > 200
        request.setDescription("B".repeat(1001)); // > 1000
        request.setImageUrl("C".repeat(501)); // > 500

        // Act
        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(3);
    }
}
