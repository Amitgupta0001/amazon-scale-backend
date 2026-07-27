package com.amazonscale.user.dto;

import com.amazonscale.user.enums.Role;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should build UserRequest and verify all getters and setters")
    void shouldBuildUserRequestAndVerifyGettersSetters() {
        // Arrange & Act
        UserRequest request = UserRequest.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@example.com")
                .password("password123")
                .role(Role.ADMIN)
                .build();

        // Assert
        assertThat(request.getFirstName()).isEqualTo("Alice");
        assertThat(request.getLastName()).isEqualTo("Smith");
        assertThat(request.getEmail()).isEqualTo("alice@example.com");
        assertThat(request.getPassword()).isEqualTo("password123");
        assertThat(request.getRole()).isEqualTo(Role.ADMIN);

        // Act - Setters test
        request.setFirstName("Bob");
        request.setLastName("Jones");
        request.setEmail("bob@example.com");
        request.setPassword("newPassword123");
        request.setRole(Role.CUSTOMER);

        // Assert
        assertThat(request.getFirstName()).isEqualTo("Bob");
        assertThat(request.getLastName()).isEqualTo("Jones");
        assertThat(request.getEmail()).isEqualTo("bob@example.com");
        assertThat(request.getPassword()).isEqualTo("newPassword123");
        assertThat(request.getRole()).isEqualTo(Role.CUSTOMER);
    }

    @Test
    @DisplayName("Should pass validation with valid UserRequest fields")
    void shouldPassValidationWithValidFields() {
        // Arrange
        UserRequest request = UserRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("securePassword123")
                .role(Role.CUSTOMER)
                .build();

        // Act
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when firstName or lastName is blank")
    void shouldFailValidationWithBlankNames() {
        // Arrange
        UserRequest request = UserRequest.builder()
                .firstName("")
                .lastName("   ")
                .email("john@example.com")
                .password("password123")
                .build();

        // Act
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(2);
    }

    @Test
    @DisplayName("Should fail validation when password is shorter than 8 characters")
    void shouldFailValidationWithShortPassword() {
        // Arrange
        UserRequest request = UserRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("short")
                .build();

        // Act
        Set<ConstraintViolation<UserRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }
}