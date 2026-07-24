package com.amazonscale.inventory.exception;

public class InventoryNotFoundException extends RuntimeException {

    public InventoryNotFoundException(Long id) {
        super("Inventory not found with ID: " + id);
    }

    public InventoryNotFoundException(String message) {
        super(message);
    }
}