package com.amazonscale.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name cannot exceed 200 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotBlank(message = "Image is required")
    @Size(max = 1000, message = "Url cannot exceed 1000 characters")
    private String imageUrl;

    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

    private BigDecimal originalPrice;

    private BigDecimal discountPercentage;

    @PositiveOrZero(message = "Stock cannot be negative")
    private Integer stock;

    @NotBlank(message = "Brand is required")
    @Size(max = 100, message = "Brand cannot exceed 100 characters")
    private String brand;

    private Long categoryId;

    private BigDecimal rating;

    private Integer reviewCount;

    private String sku;

    private String slug;

    private String status;

    private Boolean featured;

    private String thumbnail;

    private List<String> galleryImages;
}
