package com.amazonscale.wishlists.dto.response;

import com.amazonscale.wishlists.enums.WishlistType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WishlistSummaryResponseTest {

    @Test
    @DisplayName("Should build WishlistSummaryResponse using Builder and verify getters/setters")
    void shouldBuildWishlistSummaryResponseAndVerifyGettersSetters() {
        // Act
        WishlistSummaryResponse summary = WishlistSummaryResponse.builder()
                .wishlistId(1L)
                .wishlistName("Gadgets")
                .description("Cool devices")
                .type(WishlistType.CUSTOM)
                .isDefault(false)
                .totalItems(5)
                .build();

        // Assert
        assertThat(summary.getWishlistId()).isEqualTo(1L);
        assertThat(summary.getWishlistName()).isEqualTo("Gadgets");
        assertThat(summary.getDescription()).isEqualTo("Cool devices");
        assertThat(summary.getType()).isEqualTo(WishlistType.CUSTOM);
        assertThat(summary.getIsDefault()).isFalse();
        assertThat(summary.getTotalItems()).isEqualTo(5);
    }
}
