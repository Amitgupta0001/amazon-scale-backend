package com.amazonscale.product.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRequestTest {

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
        ProductRequest request = new ProductRequest();

        // Act
        request.setName("Headphones");
        request.setDescription("Noise cancelling headphones");
        request.setImageUrl("https://example.com/headphones.jpg");
        request.setPrice(new BigDecimal("199.99"));
        request.setStock(30);
        request.setBrand("AudioBrand");

        // Assert
        assertThat(request.getName()).isEqualTo("Headphones");
        assertThat(request.getDescription()).isEqualTo("Noise cancelling headphones");
        assertThat(request.getImageUrl()).isEqualTo("https://example.com/headphones.jpg");
        assertThat(request.getPrice()).isEqualTo(new BigDecimal("199.99"));
        assertThat(request.getStock()).isEqualTo(30);
        assertThat(request.getBrand()).isEqualTo("AudioBrand");
    }

    @Test
    @DisplayName("Should pass validation when all fields are valid")
    void shouldPassValidationWithValidFields() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Laptop");
        request.setDescription("High performance laptop");
        request.setImageUrl("https://example.com/laptop.jpg");
        request.setPrice(new BigDecimal("1299.99"));
        request.setStock(10);
        request.setBrand("TechBrand");

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when mandatory string fields are blank")
    void shouldFailValidationWithBlankFields() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("");
        request.setDescription("   ");
        request.setImageUrl("");
        request.setPrice(new BigDecimal("100.00"));
        request.setStock(5);
        request.setBrand("");

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(4);
    }

    @Test
    @DisplayName("Should fail validation when price is zero or negative")
    void shouldFailValidationWithInvalidPrice() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Book");
        request.setDescription("Interesting book");
        request.setImageUrl("https://example.com/book.jpg");
        request.setPrice(new BigDecimal("-10.00"));
        request.setStock(5);
        request.setBrand("PublishHouse");

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("price"));
    }

    @Test
    @DisplayName("Should fail validation when stock is negative")
    void shouldFailValidationWithNegativeStock() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Book");
        request.setDescription("Interesting book");
        request.setImageUrl("https://example.com/book.jpg");
        request.setPrice(new BigDecimal("15.00"));
        request.setStock(-1);
        request.setBrand("PublishHouse");

        // Act
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("stock"));
    }
}
