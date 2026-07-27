# AmazonScale API Design Recommendations

---

## 1. Unpaginated Collection Endpoints

### Issue
Endpoints such as `GET /api/v1/products` and `GET /api/v1/categories` return unpaginated JSON arrays.

### Evidence
Controller return types are `List<ProductResponse>` rather than `Page<ProductResponse>`.

### Why It Matters
As catalog sizes scale to thousands of items, returning entire collections leads to memory bloat and API latency spikes.

### Recommended Solution
Introduce Spring Data `Pageable` parameters (`?page=0&size=20&sort=createdAt,desc`) and return standardized paged responses.

### Priority
High

### Impact
High for API responsiveness and scale.

### Estimated Effort
3 hours

---

## 2. Standardizing User Context Headers

### Issue
Payment endpoints use `X-User-Id` header while Cart endpoints extract identity from JWT bearer token.

### Evidence
`PaymentController` uses `@RequestHeader("X-User-Id")`, whereas `CartController` uses security principal injection.

### Why It Matters
Inconsistent API authentication conventions create confusion for API consumers.

### Recommended Solution
Standardize all protected endpoints to extract authenticated user identity directly from JWT security context.

### Priority
Medium

### Impact
Medium for API uniformity.

### Estimated Effort
2 hours
