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

class InventoryUpdateRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should build InventoryUpdateRequest using Builder pattern and verify getters/setters")
    void shouldBuildInventoryUpdateRequestAndVerifyGettersSetters() {
        // Act
        InventoryUpdateRequest request = InventoryUpdateRequest.builder()
                .quantity(75)
                .warehouseLocation("Warehouse C")
                .lowStockThreshold(20)
                .build();

        // Assert
        assertThat(request.getQuantity()).isEqualTo(75);
        assertThat(request.getWarehouseLocation()).isEqualTo("Warehouse C");
        assertThat(request.getLowStockThreshold()).isEqualTo(20);

        // Act - Setters
        request.setQuantity(80);
        request.setWarehouseLocation("Warehouse D");
        request.setLowStockThreshold(25);

        // Assert
        assertThat(request.getQuantity()).isEqualTo(80);
        assertThat(request.getWarehouseLocation()).isEqualTo("Warehouse D");
        assertThat(request.getLowStockThreshold()).isEqualTo(25);
    }

    @Test
    @DisplayName("Should pass validation with valid InventoryUpdateRequest")
    void shouldPassValidationWithValidFields() {
        // Arrange
        InventoryUpdateRequest request = InventoryUpdateRequest.builder()
                .quantity(30)
                .warehouseLocation("Location East")
                .lowStockThreshold(5)
                .build();

        // Act
        Set<ConstraintViolation<InventoryUpdateRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when quantity is negative or warehouse location is blank")
    void shouldFailValidationWithInvalidFields() {
        // Arrange
        InventoryUpdateRequest request = InventoryUpdateRequest.builder()
                .quantity(-10)
                .warehouseLocation("")
                .lowStockThreshold(5)
                .build();

        // Act
        Set<ConstraintViolation<InventoryUpdateRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).hasSize(2);
    }
}