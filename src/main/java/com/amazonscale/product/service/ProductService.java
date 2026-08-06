package com.amazonscale.product.service;

import com.amazonscale.common.response.PageResponse;
import com.amazonscale.product.dto.ProductRequest;
import com.amazonscale.product.dto.ProductResponse;
import com.amazonscale.product.dto.SearchSuggestionResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProduct(Long id);

    List<ProductResponse> getAllProducts();

    PageResponse<ProductResponse> searchProducts(
            String q,
            String category,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            Boolean featured,
            Boolean active,
            Pageable pageable
    );

    SearchSuggestionResponse getSearchSuggestions(String query);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);
}
