package com.amazonscale.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefundRequest {

    @NotBlank(message = "Refund reason is required.")
    @Size(max = 255, message = "Refund reason must not exceed 255 characters.")
    private String reason;
}
