package com.amazonscale.wishlists.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWishlistsResponse {

    private List<WishlistSummaryResponse> wishlists;
    private Integer totalWishlists;
}