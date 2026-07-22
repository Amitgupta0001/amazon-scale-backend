package com.amazonscale.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import  java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 100 , message = "Product name cannot exceed 100 characters") // added validation
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 1000,message = "Description cannot exceed 1000 characters")
    private String description;

    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

    @PositiveOrZero(message = "Stock cannot be negative" )
    private Integer stock;

    @NotBlank(message = "Brand is required")
    @Size(max = 100,message = "Brand cannot exceed 100 characters")
    private String brand;

}
