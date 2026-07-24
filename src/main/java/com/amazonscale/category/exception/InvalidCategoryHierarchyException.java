package com.amazonscale.category.exception;

public class InvalidCategoryHierarchyException extends RuntimeException {

    public InvalidCategoryHierarchyException() {
        super("A category cannot be its own parent.");
    }
}