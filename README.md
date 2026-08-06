# AmazonScale Backend

A production-grade, enterprise-scale e-commerce backend built with **Java 21** and **Spring Boot 4.0.7**, designed following clean architecture, package-by-feature modular design, dynamic JPA specification search pipelines, and robust security practices.

AmazonScale implements core e-commerce services including JWT-based authentication, user profile management, hierarchical product taxonomy, advanced product search/filtering/sorting/pagination, inventory tracking, cart aggregation, wishlist management, tax/shipping-calculated order workflows, and payment gateway simulation.

---

## 📚 Technical Documentation Hub

Comprehensive, read-only system and architectural documentation is available in the [`docs/`](docs/) directory:

- 🏗️ **[Architecture Guide](docs/Architecture.md)** — High-level system architecture, package structure, layering, and request lifecycles.
- 🔐 **[Security Architecture](docs/Security.md)** — JWT authentication flow, Spring Security filter chain, public GET permissions for catalog search, RBAC, and password encoding.
- 🗄️ **[Database Schema Design](docs/Database-Schema.md)** — Relational database schema, ER diagrams, foreign key relationships, B-Tree indexes, and gallery collections.
- 🔌 **[REST API Specification](docs/API-Design.md)** — Comprehensive API documentation, search query parameters, request/response contracts, and HTTP status codes.
- 📂 **[Module Specifications](docs/README.md)** — Detailed domain specifications for [User](docs/User.md), [Product](docs/Product.md), [Category](docs/Category.md), [Inventory](docs/Inventory.md), [Cart](docs/Cart.md), [Wishlist](docs/Wishlist.md), [Order](docs/Order.md), and [Payment](docs/Payment.md).
- 💡 **[Architectural Recommendations](docs/recommendations/)** — Documented technical debt and optimization blueprints.

---

## Features Implemented

### 🔐 User & Security Management
- User registration and authentication via JWT
- Role-Based Access Control (`ROLE_CUSTOMER`, `ROLE_SELLER`, `ROLE_ADMIN`)
- BCrypt password encryption
- Centralized security filter chain with custom JWT validation filters
- Public read access for guest catalog browsing and searching (`GET /api/v1/products/**`, `GET /api/v1/categories/**`)
- User profile retrieval and management

### 🛍️ Advanced Search, Filtering, Sorting & Pagination (Phase 7)
- **Dynamic Multi-Criteria Search**: Multi-field partial matching across product names, brands, descriptions, and categories via JPA Criteria Specifications (`ProductSpecification`)
- **Server-Side Filtering**: Dynamic query predicates supporting category, brand, price range (`minPrice`/`maxPrice`), stock availability (`inStock`), featured status (`featured`), and active status (`active`)
- **Flexible Sorting**: Multi-column sorting powered by Spring Data `Sort` (e.g. `price,asc`, `price,desc`, `createdAt,desc`, `name,asc`, `rating,desc`)
- **Database-Level Pagination**: Page-based result slicing utilizing Spring Data `Pageable` wrapped in standardized `PageResponse<T>` payloads
- **Live Search Autocomplete Suggestions**: Sub-10ms projection queries retrieving distinct top matching product titles, brand names, and categories (`GET /api/v1/products/search/suggestions`)

### 🏷️ Category & Product Catalog
- Extended product entity modeling with Category relationship (`@ManyToOne`), rating metrics, review counts, original price, discount percentages, SKU, slug, featured flag, thumbnail, and gallery image collections (`@ElementCollection`)
- Hierarchical category taxonomy (parent-child relationships with self-parenting protection)
- Unpaginated backward-compatible product endpoints (`GET /api/v1/products/all`) alongside paginated catalog endpoints
- Product stock availability guards and active status indicators

### 📦 Inventory Management
- 1-to-1 warehouse inventory tracking per product
- Reserved stock calculations and reorder thresholds
- Stock level updates with deletion safeguards for active products

### 🛒 Shopping Cart Engine
- Real-time cart state management per user
- Dynamic line item addition, update, and removal
- Live inventory check during cart updates
- Automatic cart subtotal and total calculations

### ❤️ Wishlist Management
- Multiple custom wishlists per customer with a designated default wishlist
- Priority setting and item notes per wishlist entry
- Protection against duplicate items per wishlist

### 📋 Order Fulfillment Pipeline
- Transactional order creation from user cart
- Automated tax (18% GST) and flat-rate shipping calculation
- State-machine-based order status progression (`PENDING`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`)
- Stock deduction upon order creation and restoration on order cancellation

### 💳 Payment Processing
- Simulated payment gateway integration (`CREDIT_CARD`, `DEBIT_CARD`, `UPI`, `NET_BANKING`, `PAYPAL`)
- Payment verification and transaction history
- Refund processing pipeline tied to order status

---

## Module Implementation Matrix

| Module | Package | Documentation | Status |
| :--- | :--- | :--- | :---: |
| **Authentication & Security** | `com.amazonscale.security` | [Security.md](docs/Security.md) | ✅ Completed |
| **User Management** | `com.amazonscale.user` | [User.md](docs/User.md) | ✅ Completed |
| **Category Taxonomy** | `com.amazonscale.category` | [Category.md](docs/Category.md) | ✅ Completed |
| **Product Catalog & Search** | `com.amazonscale.product` | [Product.md](docs/Product.md) | ✅ Completed (Phase 7 Enhanced) |
| **Inventory Management** | `com.amazonscale.inventory` | [Inventory.md](docs/Inventory.md) | ✅ Completed |
| **Shopping Cart** | `com.amazonscale.cart` | [Cart.md](docs/Cart.md) | ✅ Completed |
| **Wishlist Service** | `com.amazonscale.wishlists` | [Wishlist.md](docs/Wishlist.md) | ✅ Completed |
| **Order Engine** | `com.amazonscale.order` | [Order.md](docs/Order.md) | ✅ Completed |
| **Payment Gateway** | `com.amazonscale.payment` | [Payment.md](docs/Payment.md) | ✅ Completed |
| **Common Infrastructure** | `com.amazonscale.common` | [Common.md](docs/Common.md) | ✅ Completed |
| **Coupon Engine** | — | — | 🚧 Planned |
| **Product Reviews** | — | — | 🚧 Planned |
| **Notification Service** | — | — | 🚧 Planned |

---

## 🛠️ Tech Stack & Prerequisites

- **Language:** Java 21 (LTS)
- **Framework:** Spring Boot 4.0.7
- **Security:** Spring Security, JJWT (`0.12.7`)
- **Data & Persistence:** Spring Data JPA, Hibernate, PostgreSQL (Prod/Dev), H2 Database (Testing)
- **Validation:** Jakarta Validation (`spring-boot-starter-validation`)
- **API Documentation:** Springdoc OpenAPI 3 / Swagger UI (`3.0.3`)
- **Containerization:** Docker & Docker Compose
- **Build Tool:** Apache Maven

---

## 📁 Repository Structure

```text
amazon-scale-backend/
├── docker/
│   ├── Dockerfile
│   └── docker-compose.yml
├── docs/
│   ├── Architecture.md
│   ├── Database-Schema.md
│   ├── API-Design.md
│   ├── Security.md
│   ├── recommendations/
│   └── [Module Docs...]
├── src/
│   ├── main/
│   │   ├── java/com/amazonscale/
│   │   │   ├── cart/
│   │   │   ├── category/
│   │   │   ├── common/
│   │   │   │   ├── exception/
│   │   │   │   └── response/ (PageResponse, ErrorResponse)
│   │   │   ├── config/
│   │   │   ├── inventory/
│   │   │   ├── order/
│   │   │   ├── payment/
│   │   │   ├── product/
│   │   │   │   ├── controller/ (ProductController)
│   │   │   │   ├── dto/ (ProductRequest, ProductResponse, SearchSuggestionResponse)
│   │   │   │   ├── entity/ (Product)
│   │   │   │   ├── mapper/ (ProductMapper)
│   │   │   │   ├── repository/
│   │   │   │   │   └── specification/ (ProductSpecification)
│   │   │   │   └── service/ (ProductService, ProductServiceImpl)
│   │   │   ├── security/
│   │   │   ├── user/
│   │   │   └── wishlists/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-test.properties
│   └── test/
└── pom.xml
```

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/Amitgupta0001/amazon-scale-backend.git
cd amazon-scale-backend
```

### 2. Run Locally with Maven

#### Configure PostgreSQL Database
Ensure PostgreSQL is running locally and update connection settings in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/amazonscale_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

#### Build & Execute

```bash
# Build the application
./mvnw clean install

# Run the Spring Boot service
./mvnw spring-boot:run
```

---

## 📖 Interactive API Documentation

Once the server is running, access Swagger UI to explore interactive endpoint documentation and test APIs:

```http
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON specification is served at:
```http
http://localhost:8080/v3/api-docs
```

---

## 🧪 Testing Strategy

The repository contains an extensive automated test suite covering units, integration, security filters, controller endpoints, dynamic JPA specifications, and domain service logic using **JUnit 5**, **Mockito**, **MockMvc**, and an **H2 in-memory database**.

Run the full test suite (406+ tests) with:

```bash
./mvnw clean test
```

---

## 📐 Design & Architectural Principles

- **Package-by-Feature Architecture:** Domain modules encapsulate their own entities, DTOs, controllers, services, specifications, and repositories.
- **Dynamic Predicate Construction:** Dynamic searches use decoupled JPA Criteria Specifications (`ProductSpecification`) avoiding native SQL string assembly.
- **Clean Separation of Concerns:** Layered request pipeline (`Controller` -> `Service` -> `Repository`).
- **Standardized Exception Handling:** `@RestControllerAdvice` converting all uncaught domain exceptions into uniform `ErrorResponse` payloads.
- **DTO Pattern & Page Wrapping:** Explicit mapping between persistence entities and REST payload data transfer objects wrapped in `PageResponse<T>`.
- **Stateless Authentication:** JWT token generation and validation with explicit public endpoints permitted for catalog discovery.

---

## 📄 License

This repository is developed for educational and portfolio demonstration purposes, showcasing enterprise backend design and Java/Spring Boot development standards.