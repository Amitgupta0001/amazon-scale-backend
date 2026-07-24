package com.amazonscale.inventory.exception;

import lombok.Builder;

@Builder
public class InventoryAlreadyExistsException extends RuntimeException {
    public InventoryAlreadyExistsException(Long productId) {
        super("Inventory already exists for product ID: " + productId);
    }
}