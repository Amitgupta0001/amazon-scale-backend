package com.amazonscale.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

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
        LoginRequest request = new LoginRequest();

        // Act
        request.setEmail("user@example.com");
        request.setPassword("secret123");

        // Assert
        assertThat(request.getEmail()).isEqualTo("user@example.com");
        assertThat(request.getPassword()).isEqualTo("secret123");
    }

    @Test
    @DisplayName("Should build LoginRequest using Builder pattern")
    void shouldBuildLoginRequestSuccessfully() {
        // Act
        LoginRequest request = LoginRequest.builder()
                .email("user@example.com")
                .password("secret123")
                .build();

        // Assert
        assertThat(request).isNotNull();
        assertThat(request.getEmail()).isEqualTo("user@example.com");
        assertThat(request.getPassword()).isEqualTo("secret123");
    }

    @Test
    @DisplayName("Should pass validation when email and password are valid")
    void shouldPassValidationWithValidFields() {
        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("valid@example.com")
                .password("password123")
                .build();

        // Act
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when email is blank or invalid format")
    void shouldFailValidationWithInvalidEmail() {
        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("invalid-email")
                .password("password123")
                .build();

        // Act
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    @DisplayName("Should fail validation when password is blank")
    void shouldFailValidationWithBlankPassword() {
        // Arrange
        LoginRequest request = LoginRequest.builder()
                .email("valid@example.com")
                .password("")
                .build();

        // Act
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }
}