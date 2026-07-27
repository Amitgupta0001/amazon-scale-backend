package com.amazonscale.payment.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RefundRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should build RefundRequest using Builder and verify getters/setters")
    void shouldBuildRefundRequestAndVerifyGettersSetters() {
        // Act
        RefundRequest request = RefundRequest.builder()
                .reason("Defective product received")
                .build();

        // Assert
        assertThat(request.getReason()).isEqualTo("Defective product received");

        // Act - Setter
        request.setReason("Changed mind");

        // Assert
        assertThat(request.getReason()).isEqualTo("Changed mind");
    }

    @Test
    @DisplayName("Should pass validation with valid reason")
    void shouldPassValidationWithValidReason() {
        // Arrange
        RefundRequest request = RefundRequest.builder()
                .reason("Order cancelled by user")
                .build();

        // Act
        Set<ConstraintViolation<RefundRequest>> violations = validator.validate(request);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when reason is blank or exceeds 255 characters")
    void shouldFailValidationWhenReasonInvalid() {
        // Arrange - Blank
        RefundRequest blankReq = RefundRequest.builder().reason("   ").build();
        // Arrange - Exceeds max length
        String longReason = "a".repeat(256);
        RefundRequest longReq = RefundRequest.builder().reason(longReason).build();

        // Act & Assert
        assertThat(validator.validate(blankReq)).hasSize(1);
        assertThat(validator.validate(longReq)).hasSize(1);
    }
}
