package com.amazonscale.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryUpdateRequest {

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity cannot be negative")
    private Integer quantity;

    @NotBlank(message = "Warehouse location is required")
    @Size(max = 200,message = "Warehouse Location cannot exceed 200 characters")
    private String warehouseLocation;

    @NotNull(message = "Low Stock Threshold is required")
    @PositiveOrZero(message = "Low Stock Threshold cannot be negative")
    private Integer lowStockThreshold;
}
