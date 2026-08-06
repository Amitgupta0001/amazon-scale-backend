package com.amazonscale.product.service.impl;

import com.amazonscale.category.entity.Category;
import com.amazonscale.category.exception.CategoryNotFoundException;
import com.amazonscale.category.repository.CategoryRepository;
import com.amazonscale.common.response.PageResponse;
import com.amazonscale.product.dto.ProductRequest;
import com.amazonscale.product.dto.ProductResponse;
import com.amazonscale.product.dto.SearchSuggestionResponse;
import com.amazonscale.product.entity.Product;
import com.amazonscale.product.exception.ProductNotFoundException;
import com.amazonscale.product.mapper.ProductMapper;
import com.amazonscale.product.repository.ProductRepository;
import com.amazonscale.product.repository.specification.ProductSpecification;
import com.amazonscale.product.service.ProductService;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    // CREATE PRODUCT
    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = ProductMapper.toEntity(request);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));
            product.setCategory(category);
        }

        Product savedProduct = repository.save(product);
        return ProductMapper.toResponse(savedProduct);
    }

    // GET PRODUCT BY ID
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductMapper.toResponse(product);
    }

    // GET ALL PRODUCTS (UNPAGINATED BACKWARD COMPATIBILITY)
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return repository.findAll().stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    // SEARCH & FILTER PRODUCTS (PAGINATED & SORTED)
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> searchProducts(
            String q,
            String category,
            String brand,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            Boolean featured,
            Boolean active,
            Pageable pageable
    ) {
        Specification<Product> spec = ProductSpecification.buildSpecification(
                q, category, brand, minPrice, maxPrice, inStock, featured, active
        );

        Page<ProductResponse> page = repository.findAll(spec, pageable)
                .map(ProductMapper::toResponse);

        return PageResponse.from(page);
    }

    // GET SEARCH SUGGESTIONS / AUTOCOMPLETE
    @Override
    @Transactional(readOnly = true)
    public SearchSuggestionResponse getSearchSuggestions(String query) {
        if (query == null || query.trim().isEmpty()) {
            return SearchSuggestionResponse.builder()
                    .productNames(new ArrayList<>())
                    .brands(new ArrayList<>())
                    .categories(new ArrayList<>())
                    .build();
        }

        String searchTerm = query.trim();
        Pageable limit = PageRequest.of(0, 5);

        List<String> productNames = repository.findTopProductNames(searchTerm, limit);
        List<String> brands = repository.findTopBrands(searchTerm, limit);
        List<String> categories = categoryRepository.findTopCategoryNames(searchTerm, limit);

        return SearchSuggestionResponse.builder()
                .productNames(productNames)
                .brands(brands)
                .categories(categories)
                .build();
    }

    // UPDATE PRODUCT
    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setPrice(request.getPrice());
        if (request.getOriginalPrice() != null) product.setOriginalPrice(request.getOriginalPrice());
        if (request.getDiscountPercentage() != null) product.setDiscountPercentage(request.getDiscountPercentage());
        product.setBrand(request.getBrand());
        product.setStock(request.getStock());
        if (request.getRating() != null) product.setRating(request.getRating());
        if (request.getReviewCount() != null) product.setReviewCount(request.getReviewCount());
        if (request.getSku() != null) product.setSku(request.getSku());
        if (request.getSlug() != null) product.setSlug(request.getSlug());
        if (request.getStatus() != null) product.setStatus(request.getStatus());
        if (request.getFeatured() != null) product.setFeatured(request.getFeatured());
        if (request.getThumbnail() != null) product.setThumbnail(request.getThumbnail());
        if (request.getGalleryImages() != null) product.setGalleryImages(request.getGalleryImages());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));
            product.setCategory(category);
        }

        Product updated = repository.save(product);
        return ProductMapper.toResponse(updated);
    }

    // DELETE PRODUCT
    @Override
    public void deleteProduct(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        repository.delete(product);
    }
}
