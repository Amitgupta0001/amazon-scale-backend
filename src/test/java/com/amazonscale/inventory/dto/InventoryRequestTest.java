package com.amazonscale.inventory.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryRequestTest {

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
        InventoryRequest request = new InventoryRequest();

        // Act
        request.setProductId(100L);
        request.setQuantity(50);
        request.setWarehouseLocation("Warehouse A");
        request.setLowStockThreshold(10);

        // Assert
        assertThat(request.getProductId()).isEqualTo(100L);
        assertThat(request.getQuantity()).isEqualTo(50);
        assertThat(request.getWarehouseLocation()).isEqualTo("Warehouse A");
        assertThat(request.getLowStockThreshold()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should pass validation when all mandatory fields are valid")
    void shouldPassValidationWithValidFields() {
        // Arrange
        InventoryRequest request = new InventoryRequest();
        request.setProductId(1L);
        request.setQuantity(100);
        request.setWarehouseLocation("Main Warehouse");
        request.setLowStockThreshold(15);

        // Act
        Set<ConstraintViolation<InventoryRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when required fields are null or blank")
    void shouldFailValidationWithNullOrBlankFields() {
        // Arrange
        InventoryRequest request = new InventoryRequest();
        request.setWarehouseLocation(""); // Blank

        // Act
        Set<ConstraintViolation<InventoryRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(4); // productId, quantity, warehouseLocation, lowStockThreshold
    }

    @Test
    @DisplayName("Should fail validation when negative numbers are provided for quantity or lowStockThreshold")
    void shouldFailValidationWithNegativeQuantityOrThreshold() {
        // Arrange
        InventoryRequest request = new InventoryRequest();
        request.setProductId(1L);
        request.setQuantity(-5);
        request.setWarehouseLocation("Warehouse B");
        request.setLowStockThreshold(-1);

        // Act
        Set<ConstraintViolation<InventoryRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(2);
    }
}
