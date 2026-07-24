package com.amazonscale.category.exception;

import lombok.Builder;

@Builder
public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long id) {
        super("Category not found with id: " + id);
    }
}