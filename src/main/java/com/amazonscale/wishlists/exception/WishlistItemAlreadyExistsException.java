package com.amazonscale.wishlists.exception;

public class WishlistItemAlreadyExistsException extends RuntimeException {
    public WishlistItemAlreadyExistsException(String message) {
        super(message);
    }
}
