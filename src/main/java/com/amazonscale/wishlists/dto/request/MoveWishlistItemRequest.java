package com.amazonscale.wishlists.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoveWishlistItemRequest {

    @NotNull(message = "Source wishlist ID is required.")
    private Long sourceWishlistId;

    @NotNull(message = "Destination wishlist ID is required.")
    private Long destinationWishlistId;

    @NotNull(message = "Product ID is required.")
    private Long productId;
}