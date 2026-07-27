package com.amazonscale.wishlists.repository;

import com.amazonscale.wishlists.entity.Wishlist;
import com.amazonscale.wishlists.enums.WishlistType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WishlistRepositoryTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Test
    @DisplayName("Should mock WishlistRepository custom query methods")
    void shouldMockWishlistRepositoryQueries() {
        // Arrange
        Wishlist wishlist = Wishlist.builder()
                .id(1L)
                .name("Default Wishlist")
                .type(WishlistType.DEFAULT)
                .isDefault(true)
                .build();

        when(wishlistRepository.findByIdAndUser_Id(1L, 10L)).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.existsByUser_IdAndNameIgnoreCase(10L, "default wishlist")).thenReturn(true);
        when(wishlistRepository.findAllByUser_IdOrderByCreatedAtDesc(10L)).thenReturn(List.of(wishlist));
        when(wishlistRepository.findAllByUser_IdAndType(10L, WishlistType.DEFAULT)).thenReturn(List.of(wishlist));
        when(wishlistRepository.countByUser_Id(10L)).thenReturn(1L);

        // Act
        Optional<Wishlist> foundByIdAndUser = wishlistRepository.findByIdAndUser_Id(1L, 10L);
        boolean existsByName = wishlistRepository.existsByUser_IdAndNameIgnoreCase(10L, "default wishlist");
        List<Wishlist> userWishlists = wishlistRepository.findAllByUser_IdOrderByCreatedAtDesc(10L);
        List<Wishlist> defaultWishlists = wishlistRepository.findAllByUser_IdAndType(10L, WishlistType.DEFAULT);
        long count = wishlistRepository.countByUser_Id(10L);

        // Assert
        assertThat(foundByIdAndUser).isPresent();
        assertThat(existsByName).isTrue();
        assertThat(userWishlists).hasSize(1);
        assertThat(defaultWishlists).hasSize(1);
        assertThat(count).isEqualTo(1L);
    }
}
