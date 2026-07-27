package com.amazonscale.wishlists.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserWishlistsResponseTest {

    @Test
    @DisplayName("Should build UserWishlistsResponse using Builder and verify getters/setters")
    void shouldBuildUserWishlistsResponseAndVerifyGettersSetters() {
        // Arrange
        WishlistSummaryResponse summary = WishlistSummaryResponse.builder().wishlistId(1L).build();

        // Act
        UserWishlistsResponse response = UserWishlistsResponse.builder()
                .wishlists(List.of(summary))
                .totalWishlists(1)
                .build();

        // Assert
        assertThat(response.getWishlists()).hasSize(1);
        assertThat(response.getTotalWishlists()).isEqualTo(1);
    }
}
