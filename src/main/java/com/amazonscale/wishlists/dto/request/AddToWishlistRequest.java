package com.amazonscale.wishlists.dto.request;

import com.amazonscale.wishlists.enums.WishlistPriority;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToWishlistRequest {

    @NotNull(message = "Wishlist ID is required.")
    private Long wishlistId;

    @NotNull(message = "Product ID is required.")
    private Long productId;

    @Builder.Default
    private WishlistPriority priority = WishlistPriority.MEDIUM;

    @Size(max = 500, message = "Note cannot exceed 500 characters.")
    private String note;

}
