# AmazonScale Category Module Recommendations

---

## 1. Deep Hierarchical Loop Detection

### Issue
Category parent-child assignment validates `parentCategoryId != categoryId`, but does not walk up multi-level ancestor chains to prevent indirect cyclic references (e.g., A -> B -> C -> A).

### Evidence
`CategoryServiceImpl.java` checks immediate equality between category ID and parent category ID, but does not traverse higher-level ancestors.

### Why It Matters
Indirect cycles produce infinite loops during category tree serialization or breadcrumb generation.

### Recommended Solution
Implement recursive ancestor lookup before saving category parent updates to ensure clean DAG structures.

### Priority
Medium

### Impact
High for catalog taxonomy integrity.

### Estimated Effort
2 hours
