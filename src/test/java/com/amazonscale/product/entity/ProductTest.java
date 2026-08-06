package com.amazonscale.product.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    @DisplayName("Should correctly set and get fields using Product Builder and getters/setters")
    void shouldBuildProductAndVerifyGettersSetters() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        Product product = Product.builder()
                .id(10L)
                .name("Camera")
                .description("DSLR Camera")
                .imageUrl("https://example.com/camera.jpg")
                .price(new BigDecimal("999.99"))
                .stock(5)
                .brand("CamBrand")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertThat(product.getId()).isEqualTo(10L);
        assertThat(product.getName()).isEqualTo("Camera");
        assertThat(product.getDescription()).isEqualTo("DSLR Camera");
        assertThat(product.getImageUrl()).isEqualTo("https://example.com/camera.jpg");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("999.99"));
        assertThat(product.getStock()).isEqualTo(5);
        assertThat(product.getBrand()).isEqualTo("CamBrand");
        assertThat(product.getActive()).isTrue();
        assertThat(product.getCreatedAt()).isEqualTo(now);
        assertThat(product.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should populate timestamps automatically on @PrePersist (prePersist) and @PreUpdate (preUpdate)")
    void shouldPopulateTimestampsOnPrePersistAndPreUpdate() {
        // Arrange
        Product product = new Product();

        // Act - Simulating PrePersist
        product.prePersist();

        // Assert
        assertThat(product.getCreatedAt()).isNotNull();
        assertThat(product.getUpdatedAt()).isNotNull();

        LocalDateTime initialUpdatedAt = product.getUpdatedAt();

        // Act - Simulating PreUpdate
        product.preUpdate();

        // Assert
        assertThat(product.getUpdatedAt()).isNotNull();
        assertThat(product.getUpdatedAt()).isAfterOrEqualTo(initialUpdatedAt);
    }

    @Test
    @DisplayName("Should verify constructors (no-args and all-args builder)")
    void shouldVerifyConstructors() {
        // Arrange & Act
        Product emptyProduct = new Product();
        Product fullProduct = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("Gaming Laptop")
                .imageUrl("https://example.com/laptop.jpg")
                .price(new BigDecimal("1499.99"))
                .stock(10)
                .brand("GameBrand")
                .active(true)
                .build();

        // Assert
        assertThat(emptyProduct.getId()).isNull();
        assertThat(fullProduct.getId()).isEqualTo(1L);
        assertThat(fullProduct.getName()).isEqualTo("Laptop");
        assertThat(fullProduct.getImageUrl()).isEqualTo("https://example.com/laptop.jpg");
        assertThat(fullProduct.getPrice()).isEqualTo(new BigDecimal("1499.99"));
        assertThat(fullProduct.getStock()).isEqualTo(10);
        assertThat(fullProduct.getBrand()).isEqualTo("GameBrand");
        assertThat(fullProduct.getActive()).isTrue();
    }
}