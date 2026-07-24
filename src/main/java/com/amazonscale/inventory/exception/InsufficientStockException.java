package com.amazonscale.inventory.exception;

import lombok.Builder;

@Builder
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }
}
