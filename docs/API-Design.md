# AmazonScale REST API Specification

---

## Related Documentation

- [Documentation Index](README.md)
- [Architecture Overview](Architecture.md)
- [Security Architecture](Security.md)
- [Database Schema Specification](Database-Schema.md)
- [Common Module](Common.md)
- [API Design Recommendations](recommendations/API-Design-Recommendations.md)

---

## Overview & REST Conventions

This document specifies the RESTful API design for the **AmazonScale** enterprise e-commerce platform. All APIs adhere strictly to modern REST conventions:

- **JSON Format**: All request and response bodies utilize UTF-8 encoded `application/json`.
- **Resource-Oriented Nouns**: Plural nouns represent domain resource collections (e.g. `/api/v1/products`, `/api/v1/orders`, `/api/v1/categories`, `/api/v1/wishlists`).
- **HTTP Verbs**:
  - `GET`: Idempotent read operations without side effects.
  - `POST`: Non-idempotent creation operations and complex actions.
  - `PUT`: Idempotent full replacements or state updates.
  - `PATCH`: Partial state transitions.
  - `DELETE`: Idempotent resource removal operations.
- **API Versioning Strategy**: Explicit URI path versioning prefixed with `/api/v1` guarantees backwards compatibility during future major iterations.
- **Pagination & Search Strategy**: Catalog endpoints (`/api/v1/products`) support dynamic JPA specification filtering, multi-column sorting, and `PageResponse<T>` pagination wrappers.

---

## Global Headers & Authentication

### 1. Authentication Header
Protected write endpoints require a valid JSON Web Token (JWT) supplied in the standard HTTP `Authorization` header:

```http
Authorization: Bearer <JWT_TOKEN>
```

*Note: `GET /api/v1/products/**` and `GET /api/v1/categories/**` endpoints are publicly accessible (`permitAll()`) for guest catalog discovery.*

### 2. User Context Header (Payment Module)
The Payment module enforces a custom identity header alongside the JWT token:

```http
X-User-Id: <USER_ID>
```

---

## Standard Error Response Contract

All error responses return a standardized JSON structure produced by `GlobalExceptionHandler`:

```json
{
  "timestamp": "2026-08-06T20:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id : 99",
  "path": "/api/v1/products/99"
}
```

---

## API Summary Matrix

| Category | Endpoint Path | Method | Auth Required | Description |
|----------|---------------|--------|---------------|-------------|
| **Auth** | `/api/v1/auth/register` | `POST` | Public | Register new user account |
| **Auth** | `/api/v1/auth/login` | `POST` | Public | Authenticate user & issue JWT |
| **Product** | `/api/v1/products` | `GET` | Public | Search, filter, sort, and paginate catalog |
| **Product** | `/api/v1/products/search/suggestions` | `GET` | Public | Live search autocomplete suggestions |
| **Product** | `/api/v1/products/all` | `GET` | Public | Fetch all products (unpaginated) |
| **Product** | `/api/v1/products/{id}` | `GET` | Public | Fetch single product by ID |
| **Product** | `/api/v1/products` | `POST` | Bearer JWT | Create catalog product |
| **Product** | `/api/v1/products/{id}` | `PUT` | Bearer JWT | Update product details |
| **Product** | `/api/v1/products/{id}` | `DELETE` | Bearer JWT | Delete product from catalog |
| **Inventory** | `/api/v1/inventory` | `POST` | Bearer JWT | Create warehouse inventory record |
| **Inventory** | `/api/v1/inventory/{id}` | `GET` | Bearer JWT | Get inventory record by ID |
| **Inventory** | `/api/v1/inventory` | `GET` | Bearer JWT | Get all inventory records |
| **Inventory** | `/api/v1/inventory/product/{productId}` | `GET` | Bearer JWT | Get inventory by Product ID |
| **Inventory** | `/api/v1/inventory/{id}` | `PUT` | Bearer JWT | Update warehouse stock quantity |
| **Inventory** | `/api/v1/inventory/{id}` | `DELETE` | Bearer JWT | Delete warehouse inventory record |
| **Category** | `/api/v1/categories` | `POST` | Bearer JWT | Create product category |
| **Category** | `/api/v1/categories/{id}` | `GET` | Public | Get category details by ID |
| **Category** | `/api/v1/categories` | `GET` | Public | Get all product categories |
| **Category** | `/api/v1/categories/{id}` | `PUT` | Bearer JWT | Update category details |
| **Category** | `/api/v1/categories/{id}` | `DELETE` | Bearer JWT | Delete category by ID |
| **Cart** | `/api/v1/cart` | `GET` | Bearer JWT | Fetch active user's cart |
| **Cart** | `/api/v1/cart/items` | `POST` | Bearer JWT | Add product item to cart |
| **Cart** | `/api/v1/cart/items/{itemId}` | `PUT` | Bearer JWT | Update cart item quantity |
| **Cart** | `/api/v1/cart/items/{itemId}` | `DELETE` | Bearer JWT | Remove single item from cart |
| **Cart** | `/api/v1/cart` | `DELETE` | Bearer JWT | Clear all items from cart |
| **Order** | `/api/v1/orders` | `POST` | Bearer JWT | Checkout cart & create order |
| **Order** | `/api/v1/orders/{id}` | `GET` | Bearer JWT | Fetch order details by ID |
| **Order** | `/api/v1/orders/user/{userId}` | `GET` | Bearer JWT | Fetch all orders for user |
| **Order** | `/api/v1/orders/{id}/cancel` | `POST` | Bearer JWT | Cancel order & restore stock |
| **Order** | `/api/v1/orders/{id}/status` | `PUT` | Bearer JWT | Transition order status |
| **Payment** | `/api/v1/payments/process` | `POST` | Bearer JWT + `X-User-Id` | Process order payment |
| **Payment** | `/api/v1/payments/{id}` | `GET` | Bearer JWT + `X-User-Id` | Fetch payment record by ID |
| **Payment** | `/api/v1/payments/order/{orderId}` | `GET` | Bearer JWT + `X-User-Id` | Fetch payment by Order ID |
| **Payment** | `/api/v1/payments/{id}/refund` | `POST` | Bearer JWT + `X-User-Id` | Process payment refund |
| **Wishlist** | `/api/v1/wishlists` | `POST` | Bearer JWT | Create new wishlist |
| **Wishlist** | `/api/v1/wishlists` | `GET` | Bearer JWT | Fetch user wishlists |
| **Wishlist** | `/api/v1/wishlists/{id}` | `GET` | Bearer JWT | Fetch wishlist by ID |
| **Wishlist** | `/api/v1/wishlists/{id}` | `PUT` | Bearer JWT | Update wishlist details |
| **Wishlist** | `/api/v1/wishlists/{id}` | `DELETE` | Bearer JWT | Delete custom wishlist |
| **Wishlist** | `/api/v1/wishlists/{id}/items` | `POST` | Bearer JWT | Add item to wishlist |
| **Wishlist** | `/api/v1/wishlists/items/{itemId}` | `DELETE` | Bearer JWT | Remove item from wishlist |

---

## Detailed Endpoint Specifications

### 1. Product Catalog & Search Endpoints

#### `GET /api/v1/products`
Searches, filters, sorts, and paginates products dynamically.

- **Auth Required:** No (`permitAll()`)
- **Query Parameters:**
  - `q` (String): Keyword matching name, brand, or description.
  - `category` (String): Category ID or name filter.
  - `brand` (String): Brand equality filter.
  - `minPrice` (BigDecimal): Lower bound price limit.
  - `maxPrice` (BigDecimal): Upper bound price limit.
  - `inStock` (Boolean): Stock availability flag (`stock > 0`).
  - `featured` (Boolean): Featured product flag.
  - `active` (Boolean): Active status flag (default `true`).
  - `page` (Integer): Zero-based page index (default `0`).
  - `size` (Integer): Page size (default `12`).
  - `sort` (String): Field and direction (e.g. `price,asc`, `price,desc`, `createdAt,desc`).
- **Response `200 OK` (`PageResponse<ProductResponse>`):**
  ```json
  {
    "content": [
      {
        "id": 1,
        "name": "Smartphone Pro",
        "description": "Next-gen flagship smartphone",
        "imageUrl": "https://example.com/phone.jpg",
        "price": 899.99,
        "originalPrice": 999.99,
        "discountPercentage": 10.00,
        "stock": 35,
        "brand": "TechScale",
        "active": true,
        "categoryId": 3,
        "categoryName": "Electronics",
        "rating": 4.70,
        "reviewCount": 89,
        "sku": "SKU-PHONE-99",
        "slug": "smartphone-pro",
        "status": "ACTIVE",
        "featured": true,
        "thumbnail": "https://example.com/phone-thumb.jpg",
        "galleryImages": ["https://example.com/phone-1.jpg"],
        "createdAt": "2026-08-06T20:00:00",
        "updatedAt": "2026-08-06T20:00:00"
      }
    ],
    "page": 0,
    "size": 12,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true,
    "numberOfElements": 1
  }
  ```

---

#### `GET /api/v1/products/search/suggestions`
Retrieves autocomplete suggestions matching a search term fragment.

- **Auth Required:** No (`permitAll()`)
- **Query Parameter:** `q` (String, default `""`)
- **Response `200 OK` (`SearchSuggestionResponse`):**
  ```json
  {
    "productNames": ["Smartphone Pro", "Smart Speaker"],
    "brands": ["SmartTech"],
    "categories": ["Smart Home"]
  }
  ```
