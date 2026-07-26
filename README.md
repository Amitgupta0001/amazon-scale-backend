# AmazonScale Backend

A production-grade, scalable e-commerce backend built using **Java 21** and **Spring Boot**, designed with clean architecture, modular development, and enterprise software engineering practices.

AmazonScale focuses on building an industry-level backend similar to platforms like Amazon by implementing secure authentication, scalable order processing, payment workflows, inventory management, and other core e-commerce services.

---

## Features Implemented

### User Management
- User Registration
- User Login
- JWT Authentication
- Role-Based Authorization
- User Profile Management
- Password Encryption using BCrypt

### Product Management
- Product CRUD Operations
- Category Management
- Product Availability Tracking

### Shopping Cart
- Add Products to Cart
- Update Cart Quantity
- Remove Products from Cart
- View Cart

### Order Management
- Place Orders
- View Order Details
- View User Orders
- Order Status Management

### Payment Management
- Payment Initiation
- Payment Verification
- Payment History
- Refund Processing
- Multiple Payment Gateway Support (Simulation)

### Inventory Management
- Inventory CRUD Operations
- Stock Quantity Management
- Product Availability Tracking
- Inventory Status Updates
---

## Planned Features

- Wishlist Management
- Coupon & Discount Engine
- Product Reviews & Ratings
- Notification Service (Email/SMS)
- Search & Filtering
- Redis Caching
- Event-Driven Architecture
- Docker Deployment
- Kubernetes Deployment
- CI/CD Pipeline
- API Rate Limiting
- Monitoring & Logging
- Admin Dashboard
- Analytics & Reporting

---

## Tech Stack

### Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- Jakarta Validation
- JWT Authentication

### Database

- PostgreSQL

### Documentation

- Swagger / OpenAPI

### Build Tool

- Maven

### Testing

- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc

### DevOps (Planned)

- Docker
- Redis
- GitHub Actions
- Kubernetes

---

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.amazonscale
│   │       ├── common
│   │       ├── security
│   │       ├── user
│   │       ├── product
│   │       ├── cart
│   │       ├── order
│   │       └── payment
│   └── resources
└── test
```

---

## Current Modules

| Module         | Status      |
|----------------|-------------|
| Authentication | ✅ Completed |
| User           | ✅ Completed |
| Product        | ✅ Completed |
| Cart           | ✅ Completed |
| Order          | ✅ Completed |
| Payment        | ✅ Completed |
| Inventory      | ✅ Completed |
| Wishlist       | 🚧 Planned  |
| Coupon         | 🚧 Planned  |
| Reviews        | 🚧 Planned  |
| Notifications  | 🚧 Planned  |

---

## API Documentation

After running the application, Swagger UI is available at:

```
http://localhost:8080/swagger-ui/index.html
```

---

## Running the Project

### Clone Repository

```bash
git clone https://github.com/Amitgupta0001/amazon-scale-backend.git
cd amazon-scale-backend
```

### Configure Database

Update your PostgreSQL credentials in:

```
src/main/resources/application.properties
```

### Build

```bash
mvn clean install
```

### Run

```bash
mvn spring-boot:run
```

---

## Testing

Run all tests using:

```bash
mvn clean test
```

The project includes:

- Unit Tests
- Service Tests
- Controller Tests
- Exception Handler Tests
- DTO Tests
- Entity Tests
- Mapper Tests

---

## Design Principles

- Layered Architecture
- Package-by-Feature Structure
- SOLID Principles
- Dependency Injection
- DTO Pattern
- Repository Pattern
- Global Exception Handling
- Bean Validation
- Clean Code Practices
- Comprehensive Unit Testing

---

## Roadmap

- Complete Wishlist Module
- Integrate Redis Cache
- Integrate Payment Gateway
- Build Notification Service
- Dockerize Application
- Configure CI/CD Pipeline
- Deploy to Cloud
- Add Monitoring & Metrics

---

## License

This project is developed for educational purposes to demonstrate enterprise-level backend development using Spring Boot.