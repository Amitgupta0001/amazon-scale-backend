# AmazonScale REST API Specification

---

## Related Documentation

- [Documentation Index](README.md)
- [Architecture Overview](Architecture.md)
- [Security Architecture](Security.md)
- [Database Schema Specification](Database-Schema.md)
- [Common Module](Common.md)

---

## Overview & REST Conventions

This document specifies the RESTful API design for the **AmazonScale** enterprise e-commerce platform. All APIs adhere strictly to modern REST conventions:

- **JSON Format**: All request and response bodies utilize UTF-8 encoded `application/json`.
- **Resource-Oriented Nouns**: Plural nouns represent domain resource collections (e.g. `/api/v1/products`, `/api/v1/orders`, `/api/v1/categories`).
- **HTTP Verbs**:
  - `GET`: Idempotent read operations without side effects.
  - `POST`: Non-idempotent creation operations and complex actions.
  - `PUT`: Idempotent full replacements or state updates.
  - `PATCH`: Partial state transitions (e.g. administrative order status updates).
  - `DELETE`: Idempotent resource removal operations.
- **API Versioning Strategy**: Explicit URI path versioning prefixed with `/api/v1` guarantees backwards compatibility during future major iterations.
- **Pagination Strategy**: List retrieval endpoints (`/api/v1/products`, `/api/v1/inventory`, `/api/v1/categories`, `/api/v1/orders`) currently return unpaginated JSON arrays. The production roadmap targets migration to Spring Data `Pageable` standard (`?page=0&size=20&sort=createdAt,desc`) with metadata wrappers.

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
  "timestamp": "2026-07-26T20:30:00",
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
    "createdAt": "2026-07-26T20:30:00"
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
- **Validation Rules:** `name` (max 100, required), `description` (max 1000, required), `imageUrl` (max 1000, required), `price` (`> 0`, required), `stock` (`>= 0`, required), `brand` (max 100, required).
- **Response `201 Created` (`ProductResponse`):**
  ```json
  {
    "id": 10,
    "name": "Wireless Noise Cancelling Headphones",
    "imageUrl": null,
    "description": "Premium over-ear headphones with 30-hour battery life",
    "price": 299.99,
    "stock": 50,
    "brand": "AudioScale",
    "active": true
  }
  ```

---

#### `GET /api/v1/products/{id}`
Fetches product details by ID.

- **Auth Required:** Bearer JWT
- **Response `200 OK` (`ProductResponse`):** Same schema as above.
- **Errors:** `404 Not Found` (Product ID does not exist).

---

#### `GET /api/v1/products`
Retrieves all products in catalog.

- **Auth Required:** Bearer JWT
- **Response `200 OK` (`List<ProductResponse>`):** Array of product objects.

---

#### `PUT /api/v1/products/{id}`
Updates existing product details.

- **Auth Required:** Bearer JWT
- **Request Body (`ProductRequest`):** Same as create request.
- **Response `200 OK` (`ProductResponse`):** Updated product object.
- **Errors:** `404 Not Found`, `400 Bad Request`.

---

#### `DELETE /api/v1/products/{id}`
Deletes product record.

- **Auth Required:** Bearer JWT
- **Response `204 No Content`**
- **Errors:** `404 Not Found`.

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
- **Response `201 Created` (`InventoryResponse`):**
  ```json
  {
    "id": 5,
    "productId": 10,
    "productName": "Wireless Noise Cancelling Headphones",
    "quantity": 100,
    "reservedQuantity": 0,
    "availableQuantity": 100,
    "warehouseLocation": "WH-BLR-A12",
    "lowStockThreshold": 15,
    "lowStock": null,
    "createdAt": "2026-07-26T20:30:00",
    "updatedAt": "2026-07-26T20:30:00"
  }
  ```
- **Errors:** `404 Not Found` (Product missing), `409 Conflict` (Inventory already exists for product).

---

#### `GET /api/v1/inventory/{id}`
Fetch inventory record by ID.

- **Response `200 OK` (`InventoryResponse`)**
- **Errors:** `404 Not Found`.

---

#### `GET /api/v1/inventory/product/{productId}`
Fetch inventory record by associated product ID.

- **Response `200 OK` (`InventoryResponse`)**
- **Errors:** `404 Not Found`.

---

#### `PUT /api/v1/inventory/{id}`
Update warehouse inventory quantity, location, and threshold.

- **Request Body (`InventoryUpdateRequest`):**
  ```json
  {
    "quantity": 120,
    "warehouseLocation": "WH-BLR-A14",
    "lowStockThreshold": 20
  }
  ```
- **Response `200 OK` (`InventoryResponse`)**
- **Errors:** `404 Not Found`, `400 Bad Request` (New quantity < reservedQuantity).

---

#### `DELETE /api/v1/inventory/{id}`
Deletes inventory record.

- **Response `204 No Content`**
- **Errors:** `404 Not Found`, `400 Bad Request` (Reserved quantity > 0).

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
- **Response `201 Created` (`CategoryResponse`):**
  ```json
  {
    "id": 2,
    "name": "Electronics",
    "description": "Gadgets, audio, and personal devices",
    "imageUrl": "https://images.example.com/electronics.jpg",
    "parentCategoryId": null,
    "createdAt": "2026-07-26T20:30:00",
    "updatedAt": "2026-07-26T20:30:00"
  }
  ```
- **Errors:** `409 Conflict` (Category name already exists), `404 Not Found` (Parent category not found).

---

#### `PUT /api/v1/categories/{id}`
Updates category details and hierarchy.

- **Request Body (`UpdateCategoryRequest`):** Same schema as create request.
- **Errors:** `409 Conflict`, `404 Not Found`, `400 Bad Request` (Self-parenting attempt: `id == parentCategoryId`).

---

### 5. Cart Endpoints

#### `GET /api/v1/cart`
Fetches the active authenticated user's shopping cart.

- **Auth Required:** Bearer JWT (`@AuthenticationPrincipal`)
- **Response `200 OK` (`CartResponse`):**
  ```json
  {
    "id": 1,
    "userId": 42,
    "items": [
      {
        "id": 101,
        "productId": 10,
        "productName": "Wireless Noise Cancelling Headphones",
        "quantity": 2,
        "unitPrice": 299.99,
        "totalPrice": 599.98
      }
    ],
    "totalPrice": 599.98,
    "totalItems": 2
  }
  ```

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
- **Validation Rules:** `productId` (required), `quantity` (`>= 1`, required).
- **Response `200 OK` (`CartResponse`)**
- **Errors:** `404 Not Found` (Product not found), `400 Bad Request` (Insufficient stock or inactive product).

---

#### `PUT /api/v1/cart/items/{itemId}`
Updates quantity of a specific line item in the cart.

- **Request Body (`UpdateCartItemRequest`):**
  ```json
  {
    "quantity": 3
  }
  ```
- **Response `200 OK` (`CartResponse`)**
- **Errors:** `404 Not Found` (Cart item not found), `400 Bad Request` (Quantity `< 1` or stock unavailable).

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
- **Allowed Payment Methods:** `COD`, `UPI`, `CREDIT_CARD`, `DEBIT_CARD`, `NET_BANKING`.
- **Response `201 Created` (`OrderResponse`):**
  ```json
  {
    "id": 1001,
    "userId": 42,
    "orderItems": [
      {
        "id": 501,
        "productId": 10,
        "productName": "Wireless Noise Cancelling Headphones",
        "quantity": 2,
        "unitPrice": 299.99,
        "totalPrice": 599.98
      }
    ],
    "subtotal": 599.98,
    "tax": 107.99,
    "shippingFee": 0.00,
    "discount": 0.00,
    "totalAmount": 707.97,
    "status": "PENDING",
    "paymentMethod": "UPI",
    "shippingAddress": "123 Tech Park, Electronic City, Bengaluru",
    "createdAt": "2026-07-26T20:30:00"
  }
  ```
- **Errors:** `400 Bad Request` (Empty cart, inactive product, insufficient stock), `404 Not Found`.

---

#### `GET /api/v1/orders/{id}`
Fetch single order by ID.

- **Query Parameter:** `userId` (Long, required)
- **Response `200 OK` (`OrderResponse`)**
- **Errors:** `404 Not Found` (Order missing or user mismatch).

---

#### `GET /api/v1/orders/user/{userId}`
Fetch all orders placed by specified user.

- **Response `200 OK` (`List<OrderResponse>`)**

---

#### `POST /api/v1/orders/{id}/cancel`
Cancels order (if status is `PENDING` or `CONFIRMED`) and restores product inventory stock.

- **Query Parameter:** `userId` (Long, required)
- **Response `200 OK` (`OrderResponse`)**
- **Errors:** `400 Bad Request` (Order is already `SHIPPED`, `DELIVERED`, or `CANCELLED`).

---

#### `PUT /api/v1/orders/{id}/status`
Transitions order status following state machine rules.

- **Query Parameter:** `status` (`OrderStatus`, required)
- **Valid Statuses:** `PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`.
- **Response `200 OK` (`OrderResponse`)**
- **Errors:** `400 Bad Request` (Illegal state machine transition).

---

### 7. Payment Endpoints

#### `POST /api/v1/payments/process`
Initiates a payment transaction for an existing order.

- **Auth Required:** Bearer JWT + `X-User-Id` Header
- **Request Body (`PaymentRequest`):**
  ```json
  {
    "orderId": 1001,
    "paymentMethod": "UPI",
    "amount": 707.97,
    "transactionId": "TXN_UPI_987654321"
  }
  ```
- **Response `201 Created` (`PaymentResponse`):**
  ```json
  {
    "id": 301,
    "orderId": 1001,
    "userId": 42,
    "paymentMethod": "UPI",
    "status": "COMPLETED",
    "amount": 707.97,
    "transactionId": "TXN_UPI_987654321",
    "paymentDate": "2026-07-26T20:30:00"
  }
  ```
- **Errors:** `400 Bad Request` (Amount mismatch, invalid payment method, inactive product), `404 Not Found` (Order not found), `402 Payment Required` (Payment processing failure simulation).

---

#### `GET /api/v1/payments/{id}`
Fetches payment record by ID.

- **Auth Required:** Bearer JWT + `X-User-Id` Header
- **Response `200 OK` (`PaymentResponse`)**

---

#### `GET /api/v1/payments/order/{orderId}`
Fetches payment record associated with specified order ID.

- **Auth Required:** Bearer JWT + `X-User-Id` Header
- **Response `200 OK` (`PaymentResponse`)**

---

#### `POST /api/v1/payments/{id}/refund`
Executes a refund for a `COMPLETED` payment transaction.

- **Auth Required:** Bearer JWT + `X-User-Id` Header
- **Response `200 OK` (`PaymentResponse`)**
- **Errors:** `400 Bad Request` (Payment is not `COMPLETED` or already refunded).
