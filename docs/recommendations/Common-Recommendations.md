# AmazonScale Common Module Recommendations

---

## 1. Missing Trace ID / Correlation ID in Error Response

### Issue
`ErrorResponse` captures timestamp, status, error code, message, and request path, but omits a unique distributed trace ID (e.g. UUID).

### Evidence
`ErrorResponse.java` contains fields `timestamp`, `status`, `error`, `message`, and `path`.

### Why It Matters
Troubleshooting production errors across application logs is challenging without a unique correlation ID per request.

### Recommended Solution
Add a `traceId` field to `ErrorResponse` populated via MDC or `ServletRequest` attributes.

### Priority
Medium

### Impact
High for production observability.

### Estimated Effort
2 hours
