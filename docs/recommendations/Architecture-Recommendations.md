# AmazonScale Architecture Recommendations

---

## 1. Product Image URL Mapping Discrepancy

### Issue
`ProductMapper.toResponse()` assigns `response.setImageUrl(product.getImageUrl())` correctly in the builder, but product creation/update DTO mapping has minor field discrepancies.

### Evidence
In `ProductMapper.java`, `toEntity` maps `imageUrl` from `ProductRequest.getImageUrl()`, but `ProductResponse` contains `imageUrl`.

### Why It Matters
Inconsistent field assignment in mappers can lead to `null` fields in REST responses despite data being persisted in the database.

### Recommended Solution
Ensure all mapper fields are unit-tested and explicitly mapped for both `toEntity` and `toResponse`.

### Priority
Medium

### Impact
High for catalog display integrity.

### Estimated Effort
1 hour

---

## 2. Dual Stock Quantity Maintenance

### Issue
Product stock quantity exists in both `Product.stock` and `Inventory.quantity`.

### Evidence
During order creation in `OrderServiceImpl.createOrder()`, stock is deducted from `Product.stock` directly as well as through `InventoryServiceImpl.deductStock()`.

### Why It Matters
Maintaining stock in two separate tables creates potential data desynchronization and race conditions under concurrent checkouts.

### Recommended Solution
Designate `Inventory` as the single source of truth for stock quantities, or issue atomic database queries (`UPDATE product SET stock = stock - :qty WHERE id = :id AND stock >= :qty`).

### Priority
High

### Impact
Critical for order inventory consistency.

### Estimated Effort
4 hours

---

## 3. Absence of Method Security Annotations

### Issue
`@EnableMethodSecurity` is missing from `SecurityConfig`.

### Evidence
`SecurityConfig.java` defines path-based authorization but does not annotate `@EnableMethodSecurity`.

### Why It Matters
Method-level security annotations like `@PreAuthorize("hasRole('ADMIN')")` on service or controller methods are ignored by Spring Security.

### Recommended Solution
Add `@EnableMethodSecurity(prePostEnabled = true)` to `SecurityConfig.java` and declare granular `@PreAuthorize` rules on administrative endpoints.

### Priority
High

### Impact
Critical for role-based authorization safety.

### Estimated Effort
2 hours

---

## 4. Payment Module User Context Header Mismatch

### Issue
`PaymentController` relies on an explicit `X-User-Id` HTTP header instead of deriving the identity from Spring Security's `SecurityContextHolder`.

### Evidence
`PaymentController.java` methods inspect `@RequestHeader("X-User-Id") Long userId`.

### Why It Matters
Clients can potentially forge the `X-User-Id` header if not properly sanitized, creating a security risk and inconsistent API design compared to `CartController`.

### Recommended Solution
Inject `Authentication` or `@AuthenticationPrincipal CustomUserDetails userDetails` in `PaymentController` to extract user identity securely.

### Priority
High

### Impact
High security and API consistency improvement.

### Estimated Effort
3 hours
