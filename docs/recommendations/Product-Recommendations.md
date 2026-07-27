# AmazonScale Product Module Recommendations

---

## 1. Dual Stock Counter Desynchronization

### Issue
Stock quantities exist on both `Product.stock` and `Inventory.quantity`.

### Evidence
`Product.java` contains `@Column private int stock`, while `Inventory.java` contains `@Column private Integer quantity`.

### Why It Matters
Updates to `Inventory` or `Product` may result in inconsistent stock states if one field is modified without updating the other.

### Recommended Solution
Eliminate `Product.stock` or calculate active stock dynamically from the `Inventory` aggregate.

### Priority
High

### Impact
High for catalog stock accuracy.

### Estimated Effort
3 hours
