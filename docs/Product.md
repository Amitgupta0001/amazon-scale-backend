# Product Module Specification

---

## 1. Overview
The **Product Module** manages physical item catalog listings, descriptions, price attributes, category taxonomy associations, stock availability, ratings, discount pricing, search specifications, live autocomplete suggestions, and paginated product search results for **AmazonScale**.

---

## 2. Purpose
Provides enterprise catalog search, filtering, sorting, pagination, and administrative CRUD capabilities for managing products across the platform.

---

## 3. Architecture
Located under `com.amazonscale.product`, leveraging clean layer separation across controllers, services, repositories, specifications, DTOs, and mappers.

---

## 4. Package Structure
```
com.amazonscale.product
├── controller
│   └── ProductController.java
├── dto
│   ├── ProductRequest.java
│   ├── ProductResponse.java
│   └── SearchSuggestionResponse.java
├── entity
│   └── Product.java
├── exception
│   ├── ProductInactiveException.java
│   ├── ProductNotFoundException.java
│   └── ProductUnavailableException.java
├── mapper
│   └── ProductMapper.java
├── repository
│   ├── ProductRepository.java
│   └── specification
│       └── ProductSpecification.java
└── service
    ├── ProductService.java
    └── impl
        └── ProductServiceImpl.java
```

---

## 5. Components
- **`ProductController`**: Exposes `/api/v1/products` search, suggestion, and CRUD REST endpoints.
- **`ProductServiceImpl`**: Enforces catalog business logic, dynamic search execution, category validation, and autocomplete suggestion collation.
- **`ProductRepository`**: Performs JPA specification execution (`JpaSpecificationExecutor<Product>`) and projection queries against the `products` table.
- **`ProductSpecification`**: Builds type-safe dynamic JPA Criteria API predicates for search, brand, category, price bounds, stock availability, and featured flags.
- **`ProductMapper`**: Maps between `ProductRequest`, `Product` entity, and `ProductResponse`.

---

## 6. Database Design
- **Table Name**: `products`
- **Primary Key**: `id` (`BIGINT`, Auto-Increment)
- **Columns**:
  - `id` BIGINT AUTO_INCREMENT PRIMARY KEY
  - `name` VARCHAR(200) NOT NULL
  - `description` TEXT NULL
  - `image_url` VARCHAR(1000) NOT NULL
  - `price` DECIMAL(10,2) NOT NULL
  - `original_price` DECIMAL(10,2) NULL
  - `discount_percentage` DECIMAL(5,2) DEFAULT 0.00
  - `stock` INT NOT NULL DEFAULT 0
  - `brand` VARCHAR(100) NOT NULL
  - `category_id` BIGINT NULL (FK to `categories.id`)
  - `rating` DECIMAL(3,2) DEFAULT 4.50
  - `review_count` INT DEFAULT 0
  - `sku` VARCHAR(100) UNIQUE NULL
  - `slug` VARCHAR(200) UNIQUE NULL
  - `status` VARCHAR(50) DEFAULT 'ACTIVE'
  - `featured` BOOLEAN NOT NULL DEFAULT FALSE
  - `thumbnail` VARCHAR(1000) NULL
  - `active` BOOLEAN NOT NULL DEFAULT TRUE
  - `created_by` VARCHAR(100) NULL
  - `updated_by` VARCHAR(100) NULL
  - `created_at` TIMESTAMP NOT NULL
  - `updated_at` TIMESTAMP NOT NULL
- **Collection Table**: `product_gallery_images` (`product_id` BIGINT, `image_url` VARCHAR(1000))
- **Indexes**: `idx_product_name`, `idx_product_brand`, `idx_product_price`, `idx_product_active`, `idx_product_featured`, `idx_product_category`

---

## 7. Entity Relationships
- `Product` N:1 `Category` (`JoinColumn(name = "category_id")`)
- `Product` 1:N Gallery Images (`@ElementCollection`)
- `Product` 1:1 `Inventory` (`mappedBy = "product"`)

---

## 8. DTOs
- **`ProductRequest`**: `name`, `description`, `imageUrl`, `price`, `originalPrice`, `discountPercentage`, `stock`, `brand`, `categoryId`, `rating`, `reviewCount`, `sku`, `slug`, `status`, `featured`, `thumbnail`, `galleryImages`.
- **`ProductResponse`**: `id`, `name`, `description`, `imageUrl`, `price`, `originalPrice`, `discountPercentage`, `stock`, `brand`, `active`, `categoryId`, `categoryName`, `rating`, `reviewCount`, `sku`, `slug`, `status`, `featured`, `thumbnail`, `galleryImages`, `createdAt`, `updatedAt`.
- **`SearchSuggestionResponse`**: `productNames`, `brands`, `categories`.
- **`PageResponse<T>`**: `content`, `page`, `size`, `totalElements`, `totalPages`, `first`, `last`, `numberOfElements`.

---

## 9. Repository Layer
- **`ProductRepository`**: Extends `JpaRepository<Product, Long>` and `JpaSpecificationExecutor<Product>`
  - `Optional<Product> findBySku(String sku)`
  - `Optional<Product> findBySlug(String slug)`
  - `List<String> findTopProductNames(String query, Pageable pageable)`
  - `List<String> findTopBrands(String query, Pageable pageable)`

---

## 10. Service Layer
- **`ProductService`**:
  - `ProductResponse createProduct(ProductRequest request)`
  - `ProductResponse getProduct(Long id)`
  - `List<ProductResponse> getAllProducts()` (unpaginated backward compatibility)
  - `PageResponse<ProductResponse> searchProducts(q, category, brand, minPrice, maxPrice, inStock, featured, active, Pageable pageable)`
  - `SearchSuggestionResponse getSearchSuggestions(String query)`
  - `ProductResponse updateProduct(Long id, ProductRequest request)`
  - `void deleteProduct(Long id)`

---

## 11. Controller Layer
- `GET /api/v1/products` -> `searchProducts()` -> HTTP `200 OK` (`PageResponse<ProductResponse>`)
- `GET /api/v1/products/all` -> `getAllProducts()` -> HTTP `200 OK` (`List<ProductResponse>`)
- `GET /api/v1/products/search/suggestions` -> `getSearchSuggestions()` -> HTTP `200 OK` (`SearchSuggestionResponse`)
- `GET /api/v1/products/{id}` -> `getProduct()` -> HTTP `200 OK` (`ProductResponse`)
- `POST /api/v1/products` -> `createProduct()` -> HTTP `201 Created` (`ProductResponse`)
- `PUT /api/v1/products/{id}` -> `updateProduct()` -> HTTP `200 OK` (`ProductResponse`)
- `DELETE /api/v1/products/{id}` -> `deleteProduct()` -> HTTP `204 No Content`

---

## 12. Business Rules
1. **Price Constraint**: Product prices must be positive (`BigDecimal > 0`).
2. **Stock Non-Negativity**: Stock counts cannot be negative.
3. **Category Validation**: If `categoryId` is supplied during product creation/update, the referenced category must exist.
4. **Active Flag Guard**: Products marked `active = false` cannot be purchased in cart/order checkouts (`ProductInactiveException`).

---

## 13. Security & Access Control
- `GET /api/v1/products/**` requests are configured with `permitAll()` in `SecurityConfig` to support guest browsing and public catalog discovery.
- Write/update operations (`POST`, `PUT`, `DELETE`) require a valid JWT bearer token.

---

## 14. Request Flow
Client Search Request -> `ProductController` -> `ProductServiceImpl` -> `ProductSpecification` -> `ProductRepository` -> `PageResponse<ProductResponse>`.
