# AmazonScale Inventory Module Recommendations

---

## 1. Lack of Optimistic Locking on Warehouse Quantities

### Issue
The `Inventory` entity lacks a `@Version` column for JPA optimistic locking during concurrent stock deduction.

### Evidence
`Inventory.java` contains `quantity` and `reservedQuantity` without a `@Version Long version` attribute.

### Why It Matters
High concurrent order placements could cause race conditions (lost updates) when updating stock quantities simultaneously.

### Recommended Solution
Annotate `Inventory` entity with `@Version` or execute atomic SQL updates (`UPDATE inventory SET quantity = quantity - :qty WHERE product_id = :id AND quantity >= :qty`).

### Priority
High

### Impact
Critical for inventory reservation correctness under load.

### Estimated Effort
2 hours
