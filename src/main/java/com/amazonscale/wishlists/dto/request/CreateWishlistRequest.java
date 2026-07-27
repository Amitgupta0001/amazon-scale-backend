package com.amazonscale.wishlists.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWishlistRequest {

    @NotBlank(message = "Wishlist name is required.")
    @Size(max = 100, message = "Wishlist name cannot exceed 100 characters.")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    private String description;
}