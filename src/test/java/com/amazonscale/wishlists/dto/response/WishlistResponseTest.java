package com.amazonscale.wishlists.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WishlistResponseTest {

    @Test
    @DisplayName("Should build WishlistResponse using Builder and verify all getters/setters")
    void shouldBuildWishlistResponseAndVerifyGettersSetters() {
        // Arrange
        WishlistItemResponse item = WishlistItemResponse.builder().wishlistItemId(10L).build();

        // Act
        WishlistResponse response = WishlistResponse.builder()
                .wishlistId(1L)
                .wishlistName("Main Wishlist")
                .description("My main items")
                .isDefault(true)
                .items(List.of(item))
                .totalItems(1)
                .currentPage(0)
                .totalPages(1)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        // Assert
        assertThat(response.getWishlistId()).isEqualTo(1L);
        assertThat(response.getWishlistName()).isEqualTo("Main Wishlist");
        assertThat(response.getDescription()).isEqualTo("My main items");
        assertThat(response.getIsDefault()).isTrue();
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getTotalItems()).isEqualTo(1);
        assertThat(response.getCurrentPage()).isEqualTo(0);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getHasNext()).isFalse();
        assertThat(response.getHasPrevious()).isFalse();
    }
}
