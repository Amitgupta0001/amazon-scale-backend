package com.amazonscale.payment.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RefundRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testGettersSettersAndBuilder() {
        RefundRequest request = RefundRequest.builder()
                .reason("Damaged product")
                .build();

        assertThat(request.getReason()).isEqualTo("Damaged product");

        request.setReason("Wrong item received");
        assertThat(request.getReason()).isEqualTo("Wrong item received");
    }

    @Test
    void testValidation_Success() {
        RefundRequest request = new RefundRequest("Item defective");
        var violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidation_BlankReason() {
        RefundRequest request = new RefundRequest("");
        var violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void testValidation_ExceedsMaxLength() {
        String longReason = "A".repeat(256);
        RefundRequest request = new RefundRequest(longReason);
        var violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }
}
