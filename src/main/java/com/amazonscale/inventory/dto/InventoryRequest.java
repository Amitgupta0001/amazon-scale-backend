package com.amazonscale.inventory.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryRequest {

    @NotNull(message = "Product Id is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity cannot be negative")
    private Integer quantity;

    @NotBlank(message = "Warehouse location is required")
    @Size(max = 200,message = "Warehouse location cannot exceed 200 characters")
    private String warehouseLocation;

    @NotNull(message = "Low stock threshold is required")
    @PositiveOrZero(message = "Low stock threshold cannot be negative")
    private Integer lowStockThreshold;
}
