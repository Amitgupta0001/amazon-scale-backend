package com.amazonscale.product.controller;

import com.amazonscale.common.response.PageResponse;
import com.amazonscale.product.dto.ProductRequest;
import com.amazonscale.product.dto.ProductResponse;
import com.amazonscale.product.dto.SearchSuggestionResponse;
import com.amazonscale.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Builder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Products", description = "Enterprise Product Search, Filtering, Sorting, and Management APIs")
@Builder
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // CREATE PRODUCT
    @Operation(summary = "Create a new product")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET PRODUCT BY ID
    @Operation(summary = "Get product using Id")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable final Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    // SEARCH & FILTER PRODUCTS (PAGINATED & SORTED)
    @Operation(summary = "Search, filter, sort, and paginate product catalog")
    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> searchProducts(
            @Parameter(description = "Keyword search query (matches name, brand, or description)")
            @RequestParam(required = false) String q,
            @Parameter(description = "Category name or Category ID filter")
            @RequestParam(required = false) String category,
            @Parameter(description = "Brand name filter")
            @RequestParam(required = false) String brand,
            @Parameter(description = "Minimum price limit")
            @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price limit")
            @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "In-stock availability filter (true = stock > 0)")
            @RequestParam(required = false) Boolean inStock,
            @Parameter(description = "Featured products filter")
            @RequestParam(required = false) Boolean featured,
            @Parameter(description = "Active status filter")
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<ProductResponse> response = productService.searchProducts(
                q, category, brand, minPrice, maxPrice, inStock, featured, active, pageable
        );
        return ResponseEntity.ok(response);
    }

    // GET ALL PRODUCTS (UNPAGINATED BACKWARD COMPATIBILITY)
    @Operation(summary = "Get all products unpaginated (Backward compatibility)")
    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // SEARCH AUTOCOMPLETE SUGGESTIONS
    @Operation(summary = "Get live search autocomplete suggestions")
    @GetMapping("/search/suggestions")
    public ResponseEntity<SearchSuggestionResponse> getSearchSuggestions(
            @Parameter(description = "Search query fragment")
            @RequestParam(required = false, defaultValue = "") String q
    ) {
        return ResponseEntity.ok(productService.getSearchSuggestions(q));
    }

    // UPDATE PRODUCT
    @Operation(summary = "Updating product using Id")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    // DELETE PRODUCT
    @Operation(summary = "Deleting product using Id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
