package com.amazonscale.product.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductResponseTest {

    @Test
    @DisplayName("Should build ProductResponse using Builder and verify getters and setters")
    void shouldBuildProductResponseAndVerifyGettersSetters() {
        // Arrange & Act
        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("Headphones")
                .imageUrl("https://example.com/headphones.jpg")
                .description("Noise cancelling")
                .price(new BigDecimal("199.99"))
                .stock(30)
                .brand("AudioBrand")
                .active(true)
                .build();

        // Assert
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Headphones");
        assertThat(response.getImageUrl()).isEqualTo("https://example.com/headphones.jpg");
        assertThat(response.getDescription()).isEqualTo("Noise cancelling");
        assertThat(response.getPrice()).isEqualTo(new BigDecimal("199.99"));
        assertThat(response.getStock()).isEqualTo(30);
        assertThat(response.getBrand()).isEqualTo("AudioBrand");
        assertThat(response.getActive()).isTrue();

        // Act - Setters test
        response.setId(2L);
        response.setName("Speaker");
        response.setImageUrl("https://example.com/speaker.jpg");
        response.setDescription("Bluetooth speaker");
        response.setPrice(new BigDecimal("99.99"));
        response.setStock(15);
        response.setBrand("SoundBrand");
        response.setActive(false);

        // Assert
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getName()).isEqualTo("Speaker");
        assertThat(response.getImageUrl()).isEqualTo("https://example.com/speaker.jpg");
        assertThat(response.getDescription()).isEqualTo("Bluetooth speaker");
        assertThat(response.getPrice()).isEqualTo(new BigDecimal("99.99"));
        assertThat(response.getStock()).isEqualTo(15);
        assertThat(response.getBrand()).isEqualTo("SoundBrand");
        assertThat(response.getActive()).isFalse();
    }

    @Test
    @DisplayName("Should initialize constructors (no-args and all-args)")
    void shouldInitializeConstructors() {
        // Arrange & Act
        ProductResponse empty = new ProductResponse();
        ProductResponse full = new ProductResponse(
                5L, "Phone", "https://example.com/phone.jpg", "Smartphone", new BigDecimal("599.99"), 20, "MobileBrand", true
        );

        // Assert
        assertThat(empty.getId()).isNull();
        assertThat(full.getId()).isEqualTo(5L);
        assertThat(full.getName()).isEqualTo("Phone");
        assertThat(full.getImageUrl()).isEqualTo("https://example.com/phone.jpg");
        assertThat(full.getDescription()).isEqualTo("Smartphone");
        assertThat(full.getPrice()).isEqualTo(new BigDecimal("599.99"));
        assertThat(full.getStock()).isEqualTo(20);
        assertThat(full.getBrand()).isEqualTo("MobileBrand");
        assertThat(full.getActive()).isTrue();
    }
}