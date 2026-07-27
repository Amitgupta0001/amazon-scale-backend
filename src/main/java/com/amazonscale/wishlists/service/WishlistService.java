package com.amazonscale.wishlists.service;

import com.amazonscale.wishlists.dto.request.AddToWishlistRequest;
import com.amazonscale.wishlists.dto.request.CreateWishlistRequest;
import com.amazonscale.wishlists.dto.request.MoveWishlistItemRequest;
import com.amazonscale.wishlists.dto.request.UpdateWishlistRequest;
import com.amazonscale.wishlists.dto.response.WishlistItemResponse;
import com.amazonscale.wishlists.dto.response.WishlistResponse;
import com.amazonscale.wishlists.dto.response.WishlistSummaryResponse;

import java.util.List;

public interface WishlistService {

    WishlistResponse createWishlist(CreateWishlistRequest request);
    WishlistResponse getWishlist(Long wishlistId, int page, int size);
    List<WishlistSummaryResponse> getUserWishlists();
    WishlistItemResponse addItem(AddToWishlistRequest request);
    WishlistItemResponse moveItem(MoveWishlistItemRequest request);
    WishlistResponse updateWishlist(Long wishlistId,UpdateWishlistRequest request);
    void removeItem(Long wishlistId, Long productId);
    void deleteWishlist(Long wishlistId);
    void clearWishlist(Long wishlistId);
}
