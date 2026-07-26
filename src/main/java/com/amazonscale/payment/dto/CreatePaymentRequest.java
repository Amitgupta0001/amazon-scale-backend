package com.amazonscale.payment.dto;

import com.amazonscale.payment.enums.PaymentGateway;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {

    @NotNull(message = "Order Id is required")
    private Long orderId;

    @NotNull(message = "Payment gateway is required")
    private PaymentGateway gateway;

}
