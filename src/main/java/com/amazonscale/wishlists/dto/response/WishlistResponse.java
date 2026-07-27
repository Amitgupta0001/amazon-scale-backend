package com.amazonscale.wishlists.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponse {

    private Long wishlistId;
    private String wishlistName;
    private String description;
    private Boolean isDefault;
    private List<WishlistItemResponse> items;
    private Integer totalItems;
    //pagination
    private Integer currentPage;
    private Integer totalPages;
    private Boolean hasNext;
    private Boolean hasPrevious;
}