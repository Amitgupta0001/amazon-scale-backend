# AmazonScale Database Recommendations

---

## 1. Missing Database Migration Tooling (Flyway / Liquibase)

### Issue
The project relies on JPA/Hibernate `ddl-auto` for schema creation without database migration scripts.

### Evidence
No migration scripts found under `src/main/resources/db/migration`. `application.yml` uses `hibernate.ddl-auto: update`.

### Why It Matters
`ddl-auto: update` is unsafe for production environments as it cannot handle complex column renames, data migrations, or index management safely.

### Recommended Solution
Integrate Flyway or Liquibase for version-controlled SQL schema migrations.

### Priority
High

### Impact
Critical for production reliability and deployment safety.

### Estimated Effort
4 hours

---

## 2. Lack of Custom Database Indexes on Foreign Key Columns

### Issue
Some foreign key columns rely solely on implicit database index generation.

### Evidence
Foreign keys like `order_items.product_id` and `cart_items.product_id` should have explicit B-tree indexes documented and enforced across environments.

### Why It Matters
Without explicit indexing, JOIN operations and lookups by product ID suffer performance degradation as tables grow.

### Recommended Solution
Add explicit `@Index` annotations to `@Table` definitions in entities.

### Priority
Medium

### Impact
High for query execution performance.

### Estimated Effort
2 hours

---

## 3. Absence of Soft Delete Mechanism

### Issue
Product and Order deletions issue physical `DELETE` queries.

### Evidence
`ProductRepository` and `OrderRepository` perform standard `JpaRepository.deleteById()`.

### Why It Matters
Physical deletes remove historical transaction data needed for audit trails, financial reporting, and customer history.

### Recommended Solution
Implement `@SQLDelete` and `@Where(clause = "deleted = false")` annotations for soft deletion.

### Priority
Medium

### Impact
High for data retention and audit compliance.

### Estimated Effort
3 hours
