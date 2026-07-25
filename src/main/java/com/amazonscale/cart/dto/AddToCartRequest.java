package com.amazonscale.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartRequest {

    @NotNull(message = "Product id is required")
    private Long productId;

    @NotNull(message = "Quantity is Required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}
