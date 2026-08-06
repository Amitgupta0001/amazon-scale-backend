package com.amazonscale.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal discountPercentage;
    private Integer stock;
    private String brand;
    private Boolean active;
    private Long categoryId;
    private String categoryName;
    private BigDecimal rating;
    private Integer reviewCount;
    private String sku;
    private String slug;
    private String status;
    private Boolean featured;
    private String thumbnail;
    private List<String> galleryImages;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
