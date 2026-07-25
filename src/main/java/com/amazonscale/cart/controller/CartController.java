package com.amazonscale.cart.controller;

import com.amazonscale.cart.dto.AddToCartRequest;
import com.amazonscale.cart.dto.CartResponse;
import com.amazonscale.cart.dto.UpdateCartItemRequest;
import com.amazonscale.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Carts",
        description = "Cart Management APIs"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    //Add items to cart
    @Operation(
            summary = "Add item to cart",
            description = "Adds a product to the authenticated user's shopping cart."
    )
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToCart(
            @AuthenticationPrincipal final Long userId,
            @Valid
            @RequestBody final AddToCartRequest request){
        CartResponse cartResponse = cartService.addItemToCart(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(cartResponse);
    }

    //Get cart items
    @Operation(
            summary = "Get the current user's cart",
            description = "Retrieves the authenticated user's shopping cart."
    )
    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            @AuthenticationPrincipal final Long userId){
        CartResponse cartResponse = cartService.getCart(userId);

        return ResponseEntity.ok(cartResponse);
    }

    //Update quantity of an item already in the cart
    @Operation(
            summary = "Update quantity of a cart item",
            description = "Updates the quantity of a product in the authenticated user's cart."
    )
    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @AuthenticationPrincipal final Long userId,
            @PathVariable final Long productId,
            @Valid
            @RequestBody final UpdateCartItemRequest request){
        CartResponse cartResponse = cartService.updateCartItem(userId, productId, request);

        return ResponseEntity.ok(cartResponse);
    }

    //Remove a single item from the cart
    @Operation(
            summary = "Remove an item from cart",
            description = "Removes a product from the authenticated user's cart."
    )
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItemFromCart(
            @AuthenticationPrincipal final Long userId,
            @PathVariable final Long productId){
        cartService.removeCartItem(userId, productId);

        return ResponseEntity.noContent().build();
    }

    //Clear the entire cart
    @Operation(
            summary = "Clear all items from cart",
            description = "Clearing all items from the authenticated user's cart"
    )
    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @AuthenticationPrincipal final Long userId){
        cartService.clearCart(userId);

        return ResponseEntity.noContent().build();
    }
}