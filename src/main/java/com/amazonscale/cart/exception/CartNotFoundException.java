package com.amazonscale.cart.exception;

public class CartNotFoundException extends RuntimeException {

    public CartNotFoundException(Long userId) {
        super("Cart not found with User Id: " + userId);
    }
}
