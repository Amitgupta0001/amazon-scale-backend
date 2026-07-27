package com.amazonscale.wishlists.dto.response;

import com.amazonscale.wishlists.enums.WishlistPriority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class WishlistItemResponseTest {

    @Test
    @DisplayName("Should build WishlistItemResponse using Builder and verify all getters/setters")
    void shouldBuildWishlistItemResponseAndVerifyGettersSetters() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        WishlistItemResponse response = WishlistItemResponse.builder()
                .wishlistItemId(10L)
                .wishlistId(1L)
                .wishlistName("Tech")
                .productId(100L)
                .productName("Smart Watch")
                .thumbnailUrl("http://img.jpg")
                .brand("Apple")
                .price(new BigDecimal("399.99"))
                .inStock(true)
                .availableQuantity(50)
                .priority(WishlistPriority.HIGH)
                .note("Wait for discount")
                .addedAt(now)
                .build();

        // Assert
        assertThat(response.getWishlistItemId()).isEqualTo(10L);
        assertThat(response.getWishlistId()).isEqualTo(1L);
        assertThat(response.getWishlistName()).isEqualTo("Tech");
        assertThat(response.getProductId()).isEqualTo(100L);
        assertThat(response.getProductName()).isEqualTo("Smart Watch");
        assertThat(response.getThumbnailUrl()).isEqualTo("http://img.jpg");
        assertThat(response.getBrand()).isEqualTo("Apple");
        assertThat(response.getPrice()).isEqualTo(new BigDecimal("399.99"));
        assertThat(response.getInStock()).isTrue();
        assertThat(response.getAvailableQuantity()).isEqualTo(50);
        assertThat(response.getPriority()).isEqualTo(WishlistPriority.HIGH);
        assertThat(response.getNote()).isEqualTo("Wait for discount");
        assertThat(response.getAddedAt()).isEqualTo(now);
    }
}
