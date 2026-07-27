package com.amazonscale.wishlists.mapper;

import com.amazonscale.product.entity.Product;
import com.amazonscale.wishlists.dto.response.WishlistItemResponse;
import com.amazonscale.wishlists.dto.response.WishlistResponse;
import com.amazonscale.wishlists.dto.response.WishlistSummaryResponse;
import com.amazonscale.wishlists.entity.Wishlist;
import com.amazonscale.wishlists.entity.WishlistItem;
import com.amazonscale.wishlists.enums.WishlistPriority;
import com.amazonscale.wishlists.enums.WishlistType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WishlistMapperTest {

    @Test
    @DisplayName("Should map WishlistItem entity to WishlistItemResponse DTO")
    void shouldMapWishlistItemToWishlistItemResponse() {
        // Arrange
        Wishlist wishlist = Wishlist.builder().id(1L).name("My List").build();
        Product product = Product.builder()
                .id(10L)
                .name("Headphones")
                .imageUrl("http://img.jpg")
                .price(new BigDecimal("199.99"))
                .brand("Sony")
                .stock(5)
                .build();
        LocalDateTime now = LocalDateTime.now();

        WishlistItem item = WishlistItem.builder()
                .id(100L)
                .wishlist(wishlist)
                .product(product)
                .priority(WishlistPriority.HIGH)
                .note("Gift")
                .createdAt(now)
                .build();

        // Act
        WishlistItemResponse response = WishlistMapper.toWishlistItemResponse(item);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getWishlistItemId()).isEqualTo(100L);
        assertThat(response.getWishlistId()).isEqualTo(1L);
        assertThat(response.getWishlistName()).isEqualTo("My List");
        assertThat(response.getProductId()).isEqualTo(10L);
        assertThat(response.getProductName()).isEqualTo("Headphones");
        assertThat(response.getThumbnailUrl()).isEqualTo("http://img.jpg");
        assertThat(response.getPrice()).isEqualTo(new BigDecimal("199.99"));
        assertThat(response.getBrand()).isEqualTo("Sony");
        assertThat(response.getInStock()).isTrue();
        assertThat(response.getAvailableQuantity()).isEqualTo(5);
        assertThat(response.getPriority()).isEqualTo(WishlistPriority.HIGH);
        assertThat(response.getNote()).isEqualTo("Gift");
        assertThat(response.getAddedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should map Wishlist entity to WishlistSummaryResponse DTO")
    void shouldMapWishlistToWishlistSummaryResponse() {
        // Arrange
        Wishlist wishlist = Wishlist.builder()
                .id(1L)
                .name("Books")
                .description("Reading list")
                .type(WishlistType.CUSTOM)
                .isDefault(false)
                .items(List.of(new WishlistItem()))
                .build();

        // Act
        WishlistSummaryResponse summary = WishlistMapper.toWishlistSummaryResponse(wishlist);

        // Assert
        assertThat(summary).isNotNull();
        assertThat(summary.getWishlistId()).isEqualTo(1L);
        assertThat(summary.getWishlistName()).isEqualTo("Books");
        assertThat(summary.getDescription()).isEqualTo("Reading list");
        assertThat(summary.getType()).isEqualTo(WishlistType.CUSTOM);
        assertThat(summary.getIsDefault()).isFalse();
        assertThat(summary.getTotalItems()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should map Wishlist to WishlistResponse with pagination details")
    void shouldMapWishlistToWishlistResponse() {
        // Arrange
        Wishlist wishlist = Wishlist.builder()
                .id(1L)
                .name("Books")
                .description("Desc")
                .isDefault(true)
                .build();

        WishlistItemResponse itemResponse = WishlistItemResponse.builder().wishlistItemId(100L).build();

        // Act
        WishlistResponse response = WishlistMapper.toWishlistResponse(
                wishlist, List.of(itemResponse), 1, 0, 1, false, false
        );

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getWishlistId()).isEqualTo(1L);
        assertThat(response.getItems()).containsExactly(itemResponse);
        assertThat(response.getTotalItems()).isEqualTo(1);
        assertThat(response.getCurrentPage()).isEqualTo(0);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getHasNext()).isFalse();
        assertThat(response.getHasPrevious()).isFalse();
    }

    @Test
    @DisplayName("Should instantiate private constructor via reflection for coverage")
    void shouldInstantiatePrivateConstructorForCoverage() throws Exception {
        // Arrange
        Constructor<WishlistMapper> constructor = WishlistMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // Act
        WishlistMapper instance = constructor.newInstance();

        // Assert
        assertThat(instance).isNotNull();
    }
}
