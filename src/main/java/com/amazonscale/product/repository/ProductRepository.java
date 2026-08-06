package com.amazonscale.product.repository;

import com.amazonscale.product.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySku(String sku);

    Optional<Product> findBySlug(String slug);

    @Query("SELECT DISTINCT p.name FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY p.name ASC")
    List<String> findTopProductNames(@Param("query") String query, Pageable pageable);

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY p.brand ASC")
    List<String> findTopBrands(@Param("query") String query, Pageable pageable);
}
