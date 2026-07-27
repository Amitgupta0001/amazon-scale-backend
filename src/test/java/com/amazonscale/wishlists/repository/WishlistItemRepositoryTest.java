package com.amazonscale.wishlists.repository;

import com.amazonscale.wishlists.entity.WishlistItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WishlistItemRepositoryTest {

    @Mock
    private WishlistItemRepository wishlistItemRepository;

    @Test
    @DisplayName("Should mock WishlistItemRepository custom query methods")
    void shouldMockWishlistItemRepositoryQueries() {
        // Arrange
        WishlistItem item = WishlistItem.builder().id(100L).build();

        when(wishlistItemRepository.existsByWishlist_IdAndProduct_Id(1L, 10L)).thenReturn(true);
        when(wishlistItemRepository.findByWishlist_IdAndProduct_Id(1L, 10L)).thenReturn(Optional.of(item));
        when(wishlistItemRepository.countByWishlist_Id(1L)).thenReturn(1L);

        // Act
        boolean exists = wishlistItemRepository.existsByWishlist_IdAndProduct_Id(1L, 10L);
        Optional<WishlistItem> found = wishlistItemRepository.findByWishlist_IdAndProduct_Id(1L, 10L);
        long count = wishlistItemRepository.countByWishlist_Id(1L);

        // Assert
        assertThat(exists).isTrue();
        assertThat(found).isPresent();
        assertThat(count).isEqualTo(1L);
    }
}
