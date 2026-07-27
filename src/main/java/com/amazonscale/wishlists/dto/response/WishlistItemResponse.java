package com.amazonscale.wishlists.dto.response;

import com.amazonscale.wishlists.enums.WishlistPriority;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemResponse {

    // Wishlist Item
    private Long wishlistItemId;

    // Wishlist Details
    private Long wishlistId;
    private String wishlistName;

    // Product Details
    private Long productId;
    private String productName;
    private String thumbnailUrl;
    private String brand;

    // Pricing
    private BigDecimal price;

    // Inventory
    private Boolean inStock;
    private Integer availableQuantity;

    // Wishlist Metadata
    private WishlistPriority priority;
    private String note;
    private LocalDateTime addedAt;
}