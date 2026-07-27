package com.amazonscale.wishlists.mapper;

import com.amazonscale.product.entity.Product;
import com.amazonscale.wishlists.dto.response.WishlistItemResponse;
import com.amazonscale.wishlists.dto.response.WishlistResponse;
import com.amazonscale.wishlists.dto.response.WishlistSummaryResponse;
import com.amazonscale.wishlists.entity.Wishlist;
import com.amazonscale.wishlists.entity.WishlistItem;

import java.util.List;
import java.util.stream.Collectors;

public final class WishlistMapper {

    private WishlistMapper() {
    }

    public static WishlistItemResponse toWishlistItemResponse(WishlistItem item) {

        Product product = item.getProduct();

        return WishlistItemResponse.builder()
                .wishlistItemId(item.getId())
                .wishlistId(item.getWishlist().getId())
                .wishlistName(item.getWishlist().getName())
                .productId(product.getId())
                .productName(product.getName())
                .thumbnailUrl(product.getImageUrl())
                .price(product.getPrice())
                .brand(product.getBrand())
                .inStock(product.getStock() > 0)
                .availableQuantity(product.getStock())
                .priority(item.getPriority())
                .note(item.getNote())
                .addedAt(item.getCreatedAt())
                .build();
    }

    public static List<WishlistItemResponse> toWishlistItemResponses(List<WishlistItem> items) {
        return items.stream()
                .map(WishlistMapper::toWishlistItemResponse)
                .collect(Collectors.toList());
    }

    public static WishlistSummaryResponse toWishlistSummaryResponse(Wishlist wishlist) {

        return WishlistSummaryResponse.builder()
                .wishlistId(wishlist.getId())
                .wishlistName(wishlist.getName())
                .description(wishlist.getDescription())
                .type(wishlist.getType())
                .isDefault(wishlist.getIsDefault())
                .totalItems(wishlist.getItems() == null ? 0 : wishlist.getItems().size())
                .build();
    }

    public static List<WishlistSummaryResponse> toWishlistSummaryResponses(List<Wishlist> wishlists) {
        return wishlists.stream()
                .map(WishlistMapper::toWishlistSummaryResponse)
                .collect(Collectors.toList());
    }

    public static WishlistResponse toWishlistResponse(
            Wishlist wishlist,
            List<WishlistItemResponse> items,
            Integer totalItems,
            Integer currentPage,
            Integer totalPages,
            Boolean hasNext,
            Boolean hasPrevious
    ) {

        return WishlistResponse.builder()
                .wishlistId(wishlist.getId())
                .wishlistName(wishlist.getName())
                .description(wishlist.getDescription())
                .isDefault(wishlist.getIsDefault())
                .totalItems(totalItems)
                .items(items)
                .currentPage(currentPage)
                .totalPages(totalPages)
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .build();
    }
}