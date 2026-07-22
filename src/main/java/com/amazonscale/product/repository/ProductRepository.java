package com.amazonscale.product.repository;

import com.amazonscale.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository
        extends JpaRepository<Product ,Long> {
    // it contains nearly 200+ methods

}
