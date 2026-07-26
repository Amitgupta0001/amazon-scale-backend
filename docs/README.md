# AmazonScale Documentation Index

---

## Overview

Welcome to the **AmazonScale** technical documentation hub. AmazonScale is a high-performance, modular enterprise e-commerce platform built with **Java 21** and **Spring Boot 4.0.7**. 

This index provides a comprehensive directory, reading order, and quick-reference map for software engineers, software architects, and system administrators working on or integrating with the AmazonScale codebase.

---

## Suggested Onboarding Path

For new developers and technical architects, we recommend following the onboarding path below to build a progressive understanding of system architecture, security models, data contracts, and domain business rules:

```mermaid
graph TD
    A[1. Architecture.md] --> B[2. Security.md]
    B --> C[3. Database-Schema.md]
    C --> D[4. Common.md]
    D --> E[5. User.md]
    E --> F[6. Product.md]
    F --> G[7. Category.md]
    G --> H[8. Inventory.md]
    H --> I[9. Cart.md]
    I --> J[10. Order.md]
    J --> K[11. Payment.md]
    K --> L[12. API-Design.md]
```

---

## Documentation Map

Below is a complete directory of all module and system documentation files available in the `docs/` directory:

| Document | Category | Short Description |
|----------|----------|-------------------|
| [**Architecture.md**](Architecture.md) | Core System | High-level system architecture, package structure, layering, deployment design, transaction boundaries, and key state machines. |
| [**Security.md**](Security.md) | Infrastructure | Authentication and authorization design, JWT lifecycle, Spring Security filter chain, password encoding, and security context handling. |
| [**Database-Schema.md**](Database-Schema.md) | Data Layer | Relational database schema, ER diagrams, foreign key relationships, indexing strategy, and database normalization summary. |
| [**Common.md**](Common.md) | Infrastructure | Centralized exception handling (`@RestControllerAdvice`), standard `ErrorResponse` DTO, and OpenAPI / Swagger 3 configuration. |
| [**User.md**](User.md) | Domain Module | User registration, authentication endpoints, BCrypt password hashing, role definitions (`ADMIN`, `SELLER`, `CUSTOMER`), and user identity management. |
| [**Product.md**](Product.md) | Domain Module | Product catalog CRUD management, active status filtering, pricing models, stock management, and product DTO mappings. |
| [**Category.md**](Category.md) | Domain Module | Hierarchical product category taxonomy, parent-child tree relationships, self-parenting hierarchy loop protection, and category uniqueness rules. |
| [**Inventory.md**](Inventory.md) | Domain Module | Warehouse stock management, 1-to-1 product inventory records, reserved stock calculation, low stock thresholds, and stock deletion guards. |
| [**Cart.md**](Cart.md) | Domain Module | User shopping cart state management, line item addition, quantity aggregation, real-time stock validation, and subtotal/total calculations. |
| [**Order.md**](Order.md) | Domain Module | Order placement engine, tax (18% GST) and shipping fee rules, order fulfillment state machine, stock deduction, and order cancellation. |
| [**Payment.md**](Payment.md) | Domain Module | Payment transaction processing, mock payment gateway integrations, transaction verification, refund state machine, and ownership validation. |
| [**API-Design.md**](API-Design.md) | REST API | Complete REST API specification, standard headers, request/response contracts, validation rules, HTTP status codes, and endpoint summary matrix. |

---

## Reading Order Rationale

1. **System & Foundation (`Architecture`, `Security`, `Database Schema`, `Common`)**: Understand global design patterns, stateless JWT authentication, entity relationships, and cross-cutting error handling.
2. **Identity & Catalog (`User`, `Product`, `Category`, `Inventory`)**: Learn foundational domain models that define user identities, catalog items, categories, and physical warehouse inventory.
3. **Transactional Engine (`Cart`, `Order`, `Payment`)**: Master the stateful checkout pipeline from cart aggregation through order state machine transitions and payment handling.
4. **API Specification (`API-Design`)**: Reference standard HTTP contracts, payloads, query parameters, headers, and status code behaviors across all public and protected REST endpoints.
