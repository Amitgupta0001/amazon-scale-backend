package com.amazonscale.product.exception;

import lombok.Builder;

@Builder
public class ProductNotFoundException extends RuntimeException{

    public ProductNotFoundException(Long id){

        super("Product not found with id :"+id);
    }
}
