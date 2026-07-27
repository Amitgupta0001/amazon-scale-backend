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
- **Pagination Strategy**: List retrieval endpoints (`/api/v1/products`, `/api/v1/inventory`, `/api/v1/categories`, `/api/v1/orders`, `/api/v1/wishlists`) currently return JSON arrays.

---

## Global Headers & Authentication

### 1. Authentication Header
All protected endpoints require a valid JSON Web Token (JWT) supplied in the standard HTTP `Authorization` header:

```http
Authorization: Bearer <JWT_TOKEN>
```

### 2. User Context Header (Payment Module)
The Payment module currently enforces a custom identity header alongside the JWT token:

```http
X-User-Id: <USER_ID>
```

---

## Standard Error Response Contract

All error responses (except Bean Validation errors) return a standardized JSON structure produced by `GlobalExceptionHandler`:

```json
{
  "timestamp": "2026-07-28T00:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id : 99",
  "path": "/api/v1/products/99"
}
```

### Bean Validation Error Format (`400 Bad Request`)
When JSR-303 validation annotations (`@NotBlank`, `@Positive`, `@Email`, etc.) fail on request bodies, the API returns a map of field names to error messages:

```json
{
  "email": "Invalid email format",
  "password": "Password is required"
}
```

---

## API Summary Matrix

| Category | Endpoint Path | Method | Auth Required | Description |
|----------|---------------|--------|---------------|-------------|
| **Auth** | `/api/v1/auth/register` | `POST` | Public | Register new user account |
| **Auth** | `/api/v1/auth/login` | `POST` | Public | Authenticate user & issue JWT |
| **Product** | `/api/v1/products` | `POST` | Bearer JWT | Create catalog product |
| **Product** | `/api/v1/products/{id}` | `GET` | Bearer JWT | Fetch single product by ID |
| **Product** | `/api/v1/products` | `GET` | Bearer JWT | Fetch all products |
| **Product** | `/api/v1/products/{id}` | `PUT` | Bearer JWT | Update product details |
| **Product** | `/api/v1/products/{id}` | `DELETE` | Bearer JWT | Delete product from catalog |
| **Inventory** | `/api/v1/inventory` | `POST` | Bearer JWT | Create warehouse inventory record |
| **Inventory** | `/api/v1/inventory/{id}` | `GET` | Bearer JWT | Get inventory record by ID |
| **Inventory** | `/api/v1/inventory` | `GET` | Bearer JWT | Get all inventory records |
| **Inventory** | `/api/v1/inventory/product/{productId}` | `GET` | Bearer JWT | Get inventory by Product ID |
| **Inventory** | `/api/v1/inventory/{id}` | `PUT` | Bearer JWT | Update warehouse stock quantity |
| **Inventory** | `/api/v1/inventory/{id}` | `DELETE` | Bearer JWT | Delete warehouse inventory record |
| **Category** | `/api/v1/categories` | `POST` | Bearer JWT | Create product category |
| **Category** | `/api/v1/categories/{id}` | `GET` | Bearer JWT | Get category details by ID |
| **Category** | `/api/v1/categories` | `GET` | Bearer JWT | Get all product categories |
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

### 1. Auth & User Endpoints

#### `POST /api/v1/auth/register`
Creates a new customer account.

- **Auth Required:** No
- **Request Body (`UserRequest`):**
  ```json
  {
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "securePassword123"
  }
  ```
- **Validation Rules:** `firstName` (max 50, required), `lastName` (max 50, required), `email` (valid format, max 100, required), `password` (8-100 chars, required).
- **Response `201 Created` (`UserResponse`):**
  ```json
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "role": "CUSTOMER",
    "enabled": true,
    "createdAt": "2026-07-28T00:30:00"
  }
  ```
- **Errors:** `409 Conflict` (Email already registered), `400 Bad Request` (Validation error).

---

#### `POST /api/v1/auth/login`
Authenticates credentials and returns a JWT access token.

- **Auth Required:** No
- **Request Body (`LoginRequest`):**
  ```json
  {
    "email": "john.doe@example.com",
    "password": "securePassword123"
  }
  ```
- **Response `200 OK` (`LoginResponse`):**
  ```json
  {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer"
  }
  ```
- **Errors:** `401 Unauthorized` (Invalid credentials), `400 Bad Request` (Validation error).

---

### 2. Product Catalog Endpoints

#### `POST /api/v1/products`
Adds a new product to the catalog.

- **Auth Required:** Bearer JWT
- **Request Body (`ProductRequest`):**
  ```json
  {
    "name": "Wireless Noise Cancelling Headphones",
    "description": "Premium over-ear headphones with 30-hour battery life",
    "imageUrl": "https://images.example.com/headphones.jpg",
    "price": 299.99,
    "stock": 50,
    "brand": "AudioScale"
  }
  ```
- **Response `201 Created` (`ProductResponse`)**

---

#### `GET /api/v1/products/{id}`
Fetches product details by ID.

- **Auth Required:** Bearer JWT
- **Response `200 OK` (`ProductResponse`)**
- **Errors:** `404 Not Found`.

---

#### `GET /api/v1/products`
Retrieves all products in catalog.

- **Auth Required:** Bearer JWT
- **Response `200 OK` (`List<ProductResponse>`)**

---

#### `PUT /api/v1/products/{id}`
Updates existing product details.

- **Auth Required:** Bearer JWT
- **Response `200 OK` (`ProductResponse`)**
- **Errors:** `404 Not Found`, `400 Bad Request`.

---

#### `DELETE /api/v1/products/{id}`
Deletes product record.

- **Auth Required:** Bearer JWT
- **Response `204 No Content`**

---

### 3. Inventory Endpoints

#### `POST /api/v1/inventory`
Creates a warehouse inventory record bound to a product.

- **Auth Required:** Bearer JWT
- **Request Body (`InventoryRequest`):**
  ```json
  {
    "productId": 10,
    "quantity": 100,
    "warehouseLocation": "WH-BLR-A12",
    "lowStockThreshold": 15
  }
  ```
- **Response `201 Created` (`InventoryResponse`)**

---

#### `GET /api/v1/inventory/{id}`
Fetch inventory record by ID.

- **Response `200 OK` (`InventoryResponse`)**

---

#### `GET /api/v1/inventory/product/{productId}`
Fetch inventory record by associated product ID.

- **Response `200 OK` (`InventoryResponse`)**

---

#### `PUT /api/v1/inventory/{id}`
Update warehouse inventory quantity, location, and threshold.

- **Response `200 OK` (`InventoryResponse`)**

---

#### `DELETE /api/v1/inventory/{id}`
Deletes inventory record.

- **Response `204 No Content`**

---

### 4. Category Endpoints

#### `POST /api/v1/categories`
Creates a product category.

- **Request Body (`CreateCategoryRequest`):**
  ```json
  {
    "name": "Electronics",
    "description": "Gadgets, audio, and personal devices",
    "imageUrl": "https://images.example.com/electronics.jpg",
    "parentCategoryId": null
  }
  ```
- **Response `201 Created` (`CategoryResponse`)**

---

#### `PUT /api/v1/categories/{id}`
Updates category details and hierarchy.

- **Response `200 OK` (`CategoryResponse`)**

---

### 5. Cart Endpoints

#### `GET /api/v1/cart`
Fetches the active authenticated user's shopping cart.

- **Auth Required:** Bearer JWT
- **Response `200 OK` (`CartResponse`)**

---

#### `POST /api/v1/cart/items`
Adds a product item to the user's cart.

- **Request Body (`AddToCartRequest`):**
  ```json
  {
    "productId": 10,
    "quantity": 2
  }
  ```
- **Response `200 OK` (`CartResponse`)**

---

#### `PUT /api/v1/cart/items/{itemId}`
Updates quantity of a specific line item in the cart.

- **Response `200 OK` (`CartResponse`)**

---

#### `DELETE /api/v1/cart/items/{itemId}`
Removes line item from cart.

- **Response `200 OK` (`CartResponse`)**

---

#### `DELETE /api/v1/cart`
Clears all items from user's shopping cart.

- **Response `204 No Content`**

---

### 6. Order Endpoints

#### `POST /api/v1/orders`
Converts user's cart into a firm order and deducts product inventory stock.

- **Auth Required:** Bearer JWT
- **Query Parameter:** `userId` (Long, required)
- **Request Body (`CreateOrderRequest`):**
  ```json
  {
    "shippingAddress": "123 Tech Park, Electronic City, Bengaluru",
    "paymentMethod": "UPI"
  }
  ```
- **Response `201 Created` (`OrderResponse`)**

---

#### `GET /api/v1/orders/{id}`
Fetch single order by ID.

- **Response `200 OK` (`OrderResponse`)**

---

#### `GET /api/v1/orders/user/{userId}`
Fetch all orders placed by specified user.

- **Response `200 OK` (`List<OrderResponse>`)**

---

#### `POST /api/v1/orders/{id}/cancel`
Cancels order and restores product inventory stock.

- **Response `200 OK` (`OrderResponse`)**

---

#### `PUT /api/v1/orders/{id}/status`
Transitions order status following state machine rules.

- **Query Parameter:** `status` (`OrderStatus`, required)
- **Response `200 OK` (`OrderResponse`)**

---

### 7. Payment Endpoints

#### `POST /api/v1/payments/process`
Initiates a payment transaction for an existing order.

- **Auth Required:** Bearer JWT + `X-User-Id` Header
- **Request Body (`CreatePaymentRequest`):**
  ```json
  {
    "orderId": 1001,
    "paymentMethod": "UPI",
    "amount": 707.97,
    "transactionId": "TXN_UPI_987654321"
  }
  ```
- **Response `201 Created` (`PaymentResponse`)**

---

#### `GET /api/v1/payments/{id}`
Fetches payment record by ID.

- **Response `200 OK` (`PaymentResponse`)**

---

#### `GET /api/v1/payments/order/{orderId}`
Fetches payment record associated with specified order ID.

- **Response `200 OK` (`PaymentResponse`)**

---

#### `POST /api/v1/payments/{id}/refund`
Executes a refund for a `COMPLETED` payment transaction.

- **Response `200 OK` (`PaymentResponse`)**

---

### 8. Wishlist Endpoints

#### `POST /api/v1/wishlists`
Creates a custom wishlist.

- **Auth Required:** Bearer JWT
- **Request Body (`CreateWishlistRequest`):**
  ```json
  {
    "name": "Tech Gifts",
    "description": "Gadgets I want for the holidays",
    "type": "CUSTOM"
  }
  ```
- **Response `201 Created` (`WishlistResponse`)**

---

#### `GET /api/v1/wishlists`
Fetches all wishlists for authenticated user.

- **Auth Required:** Bearer JWT
- **Response `200 OK` (`UserWishlistsResponse`)**

---

#### `GET /api/v1/wishlists/{id}`
Fetches specific wishlist details by ID.

- **Auth Required:** Bearer JWT
- **Response `200 OK` (`WishlistResponse`)**

---

#### `POST /api/v1/wishlists/{id}/items`
Adds a product to a wishlist.

- **Auth Required:** Bearer JWT
- **Request Body (`AddToWishlistRequest`):**
  ```json
  {
    "productId": 10,
    "note": "Prefer black color variant",
    "priority": "HIGH"
  }
  ```
- **Response `200 OK` (`WishlistResponse`)**

---

#### `DELETE /api/v1/wishlists/items/{itemId}`
Removes an item from a wishlist.

- **Auth Required:** Bearer JWT
- **Response `204 No Content`**
