# AmazonScale User Module Recommendations

---

## 1. Hardcoded Default Role Assignment

### Issue
User registration in `UserServiceImpl.register()` hardcodes the user role to `Role.CUSTOMER`.

### Evidence
In `UserServiceImpl.java`, `user.setRole(Role.CUSTOMER)` is assigned regardless of incoming DTO data.

### Why It Matters
Admin or Seller registration paths are not currently accessible via API endpoints.

### Recommended Solution
Create administrative user provisioning endpoints or support role selection guarded by authorization checks.

### Priority
Medium

### Impact
Medium for administrative onboarding.

### Estimated Effort
2 hours

---

## 2. Lack of Profile Management Endpoints

### Issue
The `UserController` only exposes `POST /api/v1/auth/register`. There are no endpoints to view, update, or deactivate user profiles.

### Evidence
`UserController.java` contains only `register()` action.

### Why It Matters
Authenticated users cannot view their account details or update profile information (first name, last name, password).

### Recommended Solution
Implement `GET /api/v1/users/me`, `PUT /api/v1/users/me`, and `PATCH /api/v1/users/me/deactivate`.

### Priority
High

### Impact
High for essential user functionality.

### Estimated Effort
4 hours
