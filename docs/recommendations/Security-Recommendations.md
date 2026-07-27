# AmazonScale Security Recommendations

---

## 1. Missing Method Security Enforcement

### Issue
Method-level security `@EnableMethodSecurity` is omitted from Spring Security configuration.

### Evidence
`SecurityConfig.java` does not contain `@EnableMethodSecurity`.

### Why It Matters
Service or controller level `@PreAuthorize` annotations are not evaluated, allowing unauthorized role access if HTTP path matchers are too permissive.

### Recommended Solution
Annotate `SecurityConfig` with `@EnableMethodSecurity`.

### Priority
High

### Impact
Critical for authorization safety.

### Estimated Effort
1 hour

---

## 2. Hardcoded / Permissive JWT Configuration Defaults

### Issue
JWT secret keys and expiration values depend on environment variables with fallback defaults in `application.yml`.

### Evidence
`application.yml` contains default JWT secret property configuration.

### Why It Matters
Using default secret keys in non-production environments risks security exposure if deployed unchanged.

### Recommended Solution
Enforce mandatory environment variable injection for `JWT_SECRET` with application startup failure validation if missing.

### Priority
High

### Impact
High for production security hardening.

### Estimated Effort
1 hour

---

## 3. Absence of Refresh Token Mechanism

### Issue
System uses short-lived or single long-lived access tokens without refresh tokens.

### Evidence
`JwtService.java` only generates access tokens.

### Why It Matters
Users must re-authenticate frequently if access token lifetime is short, or security risk increases if lifetime is long.

### Recommended Solution
Implement OAuth2-compliant refresh token rotation with database session revocation support.

### Priority
Medium

### Impact
High for user experience and session security.

### Estimated Effort
5 hours
