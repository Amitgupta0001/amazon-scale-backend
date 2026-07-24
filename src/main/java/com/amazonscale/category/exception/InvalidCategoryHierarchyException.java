package com.amazonscale.category.exception;

import lombok.Builder;

@Builder
public class InvalidCategoryHierarchyException extends RuntimeException {

    public InvalidCategoryHierarchyException() {
        super("A category cannot be its own parent.");
    }
}