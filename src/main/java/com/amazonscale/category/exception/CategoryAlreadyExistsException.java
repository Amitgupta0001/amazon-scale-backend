package com.amazonscale.category.exception;

import lombok.Builder;

@Builder
public class CategoryAlreadyExistsException extends RuntimeException {

    public CategoryAlreadyExistsException(String name) {
        super("Category already exists with name: " + name);
    }
}