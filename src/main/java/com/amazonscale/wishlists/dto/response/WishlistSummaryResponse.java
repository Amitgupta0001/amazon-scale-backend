package com.amazonscale.wishlists.dto.response;

import com.amazonscale.wishlists.enums.WishlistType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistSummaryResponse {

    private Long wishlistId;
    private String wishlistName;
    private String description;
    private WishlistType type;
    private Boolean isDefault;
    private Integer totalItems;
}