package com.amazonscale.wishlists.controller;

import com.amazonscale.wishlists.dto.request.AddToWishlistRequest;
import com.amazonscale.wishlists.dto.request.CreateWishlistRequest;
import com.amazonscale.wishlists.dto.request.MoveWishlistItemRequest;
import com.amazonscale.wishlists.dto.request.UpdateWishlistRequest;
import com.amazonscale.wishlists.dto.response.WishlistItemResponse;
import com.amazonscale.wishlists.dto.response.WishlistResponse;
import com.amazonscale.wishlists.dto.response.WishlistSummaryResponse;
import com.amazonscale.wishlists.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlists")
@RequiredArgsConstructor
@Tag(
        name = "Wishlist Management",
        description = "APIs for managing user wishlists and wishlist items."
)
public class WishlistController {

    private final WishlistService wishlistService;

    // Create a new custom wishlist.

    @Operation(
            summary = "Create Wishlist",
            description = "Creates a new custom wishlist for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Wishlist created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Wishlist already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WishlistResponse createWishlist(
            @Valid @RequestBody CreateWishlistRequest request) {
        return wishlistService.createWishlist(request);
    }

    //Get all wishlists of the authenticated user.
    @Operation(
            summary = "Get User Wishlists",
            description = "Returns all wishlists belonging to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wishlists retrieved successfully")
    })
    @GetMapping
    public List<WishlistSummaryResponse> getUserWishlists() {
        return wishlistService.getUserWishlists();
    }

    // Get a wishlist with paginated items.
    @Operation(
            summary = "Get Wishlist",
            description = "Returns a wishlist along with paginated wishlist items."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wishlist retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Wishlist not found")
    })
    @GetMapping("/{wishlistId}")
    public WishlistResponse getWishlist(
            @Parameter(description = "Wishlist ID")
            @PathVariable Long wishlistId,

            @Parameter(description = "Page number")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size) {

        return wishlistService.getWishlist(wishlistId, page, size);
    }

    // Update a wishlist.
    @Operation(
            summary = "Update Wishlist",
            description = "Updates wishlist name and description."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Wishlist updated successfully"),
            @ApiResponse(responseCode = "404", description = "Wishlist not found"),
            @ApiResponse(responseCode = "409", description = "Wishlist name already exists")
    })
    @PutMapping("/{wishlistId}")
    public WishlistResponse updateWishlist(
            @Parameter(description = "Wishlist ID")
            @PathVariable Long wishlistId,
            @Valid @RequestBody UpdateWishlistRequest request) {

        return wishlistService.updateWishlist(wishlistId, request);
    }


    // Delete a wishlist.
    @Operation(
            summary = "Delete Wishlist",
            description = "Deletes a custom wishlist."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Wishlist deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Wishlist not found"),
            @ApiResponse(responseCode = "400", description = "Default wishlist cannot be deleted")
    })
    @DeleteMapping("/{wishlistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWishlist(
            @Parameter(description = "Wishlist ID")
            @PathVariable Long wishlistId) {

        wishlistService.deleteWishlist(wishlistId);
    }

    // Add product to wishlist.
    @Operation(
            summary = "Add Product to Wishlist",
            description = "Adds a product to a wishlist."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product added successfully"),
            @ApiResponse(responseCode = "404", description = "Wishlist or Product not found"),
            @ApiResponse(responseCode = "409", description = "Product already exists in wishlist")
    })
    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public WishlistItemResponse addItem(
            @Valid @RequestBody AddToWishlistRequest request) {

        return wishlistService.addItem(request);
    }

    // Move product from one wishlist to another.
    @Operation(
            summary = "Move Wishlist Item",
            description = "Moves a product from one wishlist to another."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product moved successfully"),
            @ApiResponse(responseCode = "404", description = "Wishlist or Product not found"),
            @ApiResponse(responseCode = "409", description = "Product already exists in destination wishlist")
    })
    @PutMapping("/items/move")
    public WishlistItemResponse moveItem(
            @Valid @RequestBody MoveWishlistItemRequest request) {

        return wishlistService.moveItem(request);
    }

    // Remove product from wishlist.
    @Operation(
            summary = "Remove Wishlist Item",
            description = "Removes a product from a wishlist."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product removed successfully"),
            @ApiResponse(responseCode = "404", description = "Wishlist or Product not found")
    })
    @DeleteMapping("/{wishlistId}/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(
            @Parameter(description = "Wishlist ID")
            @PathVariable Long wishlistId,

            @Parameter(description = "Product ID")
            @PathVariable Long productId) {

        wishlistService.removeItem(wishlistId, productId);
    }


    // Remove all products from a wishlist.
    @Operation(
            summary = "Clear Wishlist",
            description = "Removes all products from a wishlist."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Wishlist cleared successfully"),
            @ApiResponse(responseCode = "404", description = "Wishlist not found")
    })
    @DeleteMapping("/{wishlistId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearWishlist(
            @Parameter(description = "Wishlist ID")
            @PathVariable Long wishlistId) {

        wishlistService.clearWishlist(wishlistId);
    }
}