# AmazonScale Order Module Recommendations

---

## 1. Hardcoded Tax Rate (18% GST)

### Issue
Tax percentage is hardcoded to 18% inside `OrderServiceImpl.createOrder()`.

### Evidence
`OrderServiceImpl.java` computes tax as `subtotal.multiply(new BigDecimal("0.18"))`.

### Why It Matters
Tax calculation varies by product category, regional jurisdiction, and tax exemptions.

### Recommended Solution
Inject configurable tax calculation strategy beans (`TaxCalculationService`).

### Priority
Medium

### Impact
High for multi-jurisdiction commerce.

### Estimated Effort
3 hours
