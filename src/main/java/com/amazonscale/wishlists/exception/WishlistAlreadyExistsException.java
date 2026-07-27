package com.amazonscale.wishlists.exception;

public class WishlistAlreadyExistsException extends RuntimeException {
    public WishlistAlreadyExistsException(String message) {
        super(message);
    }
}
