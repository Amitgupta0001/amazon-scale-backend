package com.amazonscale.product.entity;

import com.amazonscale.category.entity.Category;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_brand", columnList = "brand"),
        @Index(name = "idx_product_price", columnList = "price"),
        @Index(name = "idx_product_active", columnList = "active"),
        @Index(name = "idx_product_featured", columnList = "featured"),
        @Index(name = "idx_product_category", columnList = "category_id")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 1000)
    private String imageUrl;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false, length = 100)
    private String brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal rating = BigDecimal.valueOf(4.5);

    @Column
    @Builder.Default
    private Integer reviewCount = 0;

    @Column(unique = true, length = 100)
    private String sku;

    @Column(unique = true, length = 200)
    private String slug;

    @Column(length = 50)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(nullable = false)
    @Builder.Default
    private Boolean featured = false;

    @Column(length = 1000)
    private String thumbnail;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "product_gallery_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url", length = 1000)
    @Builder.Default
    private List<String> galleryImages = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(length = 100)
    private String createdBy;

    @Column(length = 100)
    private String updatedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (active == null) active = true;
        if (featured == null) featured = false;
        if (status == null) status = "ACTIVE";
        if (rating == null) rating = BigDecimal.valueOf(4.5);
        if (reviewCount == null) reviewCount = 0;
        if (discountPercentage == null) discountPercentage = BigDecimal.ZERO;
        if (thumbnail == null) thumbnail = imageUrl;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
