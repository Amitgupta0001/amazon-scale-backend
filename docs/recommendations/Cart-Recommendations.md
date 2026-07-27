# AmazonScale Cart Module Recommendations

---

## 1. Lack of Multi-Currency Exchange Conversion

### Issue
`CartItem.priceAtAddition` records item prices, but subtotal calculation relies on raw arithmetic addition assuming a single global currency.

### Evidence
`Cart.java` and `CartItem.java` store prices without currency conversion handlers.

### Why It Matters
Multi-region storefront expansion requires explicit currency code tracking and real-time exchange rates.

### Recommended Solution
Introduce explicit `CurrencyCode` propagation across cart items with dynamic rate converters.

### Priority
Low

### Impact
Medium for internationalization.

### Estimated Effort
3 hours
