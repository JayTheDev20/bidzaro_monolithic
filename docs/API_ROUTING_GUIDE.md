# 🛣️ API Routing & Endpoint Guide

**Bidzaro Catering Platform** | Complete endpoint reference and routing fixes

---

## 📋 Quick Summary of Fixed Issues

| Endpoint | Previous Status | Current Status | Fix |
|----------|-----------------|----------------|-----|
| `GET /api/v1/bids/my` | ❌ 405 METHOD_NOT_ALLOWED | ✅ Added | New endpoint for user's bid requests shorthand |
| `GET /api/v1/bids/requests/available` | ❌ 404 NOT_FOUND | ✅ Added | New endpoint for available bids to vendors |
| `GET /api/v1/orders/vendor/my` | ❌ 500 INTERNAL_SERVER_ERROR | ✅ Added | New endpoint for vendor's orders shorthand |

---

## 🎯 Bid Management APIs

### Base URL: `/api/v1/bids`

#### User Bid Request Endpoints

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `POST` | `/requests` | USER | Create a new bid request |
| `POST` | `/requests/from-cart` | USER | Create bid requests from shopping cart |
| `GET` | `/requests` | USER | Get all user's bid requests (paginated) |
| `GET` | `/requests?page=0&size=20` | USER | **Example:** Get page 0, 20 items per page |
| `GET` | `/my` | USER | **NEW:** Shorthand for /requests |
| `GET` | `/my?page=0&size=20` | USER | **NEW:** Get user's bid requests with pagination |
| `GET` | `/requests/{bidRequestId}` | USER | Get specific bid request details |
| `GET` | `/requests/{bidRequestId}/bids` | USER | Get all bids for a specific request |
| `PUT` | `/requests/{bidRequestId}` | USER | Update/edit bid request |
| `DELETE` | `/requests/{bidRequestId}` | USER | Cancel bid request |

#### Vendor Bid Endpoints

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `POST` | `/requests/{bidRequestId}/submit-bid` | VENDOR | Submit bid for a request |
| `PUT` | `/{bidId}` | VENDOR | Revise/update submitted bid |
| `DELETE` | `/{bidId}` | VENDOR | Withdraw submitted bid |
| `GET` | `/vendor/submitted` | VENDOR | Get all bids vendor submitted |
| `GET` | `/vendor/submitted?page=0&size=20` | VENDOR | **Example:** Vendor's submitted bids with pagination |
| `GET` | `/vendor/received` | VENDOR | Get bid requests vendor can bid on |
| `GET` | `/vendor/received?page=0&size=20` | VENDOR | **Example:** Available requests with pagination |
| `GET` | `/active` | VENDOR | Get all active bid requests in system |
| `GET` | `/requests/available` | VENDOR | **NEW:** Get available bid requests to bid on |

#### User Bid Acceptance

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `POST` | `/{bidId}/accept` | USER | Accept a vendor's bid |

---

## 📦 Order Management APIs

### Base URL: `/api/v1/orders`

#### User Order Endpoints

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `GET` | `/` | USER | Get all user's orders |
| `GET` | `/?page=0&size=20` | USER | **Example:** Get page 0, 20 items |
| `GET` | `/{orderId}` | USER | Get specific order details |
| `GET` | `/upcoming` | USER | Get upcoming orders (future event dates) |
| `GET` | `/history` | USER | Get completed/cancelled orders |
| `PATCH` | `/{orderId}/status` | USER | Update order status (user action) |
| `POST` | `/{orderId}/cancel` | USER | Cancel an order |

#### Vendor Order Endpoints

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `GET` | `/vendor` | VENDOR | Get all orders assigned to vendor |
| `GET` | `/vendor?page=0&size=20` | VENDOR | **Example:** Get page 0, 20 items |
| `GET` | `/vendor/my` | VENDOR | **NEW:** Shorthand for /vendor |
| `GET` | `/vendor/my?page=0&size=20` | VENDOR | **NEW:** Get vendor's orders with pagination |

---

## 🔒 Authentication APIs

### Base URL: `/api/v1/auth`

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `POST` | `/register` | NONE | Register new user/vendor/admin |
| `POST` | `/login` | NONE | Login with email/phone and password |
| `POST` | `/refresh` | NONE | Refresh access token using refresh token |
| `POST` | `/logout` | JWT | Logout current session |
| `POST` | `/send-otp` | NONE | Send OTP for verification |
| `POST` | `/verify-otp` | NONE | Verify OTP code |
| `POST` | `/forgot-password` | NONE | Initiate password reset |
| `POST` | `/reset-password` | NONE | Reset password with token |

---

## 👥 User Management APIs

### Base URL: `/api/v1/users`

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `GET` | `/profile` | JWT | Get current user's profile |
| `PUT` | `/profile` | JWT | Update current user's profile |
| `PUT` | `/password` | JWT | Change password |
| `GET` | `/preferences` | JWT | Get notification preferences |
| `PUT` | `/preferences` | JWT | Update notification preferences |
| `GET` | `/{userId}` | ADMIN | Get specific user by ID (admin only) |
| `PUT` | `/{userId}/status` | ADMIN | Update user status (admin only) |
| `DELETE` | `/{userId}` | ADMIN | Soft delete user (admin only) |

---

## 🏪 Vendor APIs

### Base URL: `/api/v1/vendors`

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `POST` | `/register` | USER | Register as vendor |
| `GET` | `/profile` | VENDOR | Get vendor's own profile |
| `PUT` | `/profile` | VENDOR | Update vendor's own profile |
| `GET` | `/menu` | VENDOR | Get vendor's menu items |
| `GET` | `/analytics` | VENDOR | Get vendor dashboard analytics |
| `GET` | `/{vendorId}` | PUBLIC | Get vendor public profile |
| `GET` | `/{vendorId}/menu` | PUBLIC | Get vendor public menu items |
| `GET` | `/{vendorId}/reviews` | PUBLIC | Get vendor reviews/ratings |

---

## 💳 Payment APIs

### Base URL: `/api/v1/payments`

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `POST` | `/initiate` | USER | Initiate payment |
| `POST` | `/confirm` | WEBHOOK | Confirm payment from gateway |
| `GET` | `/history` | USER | Get user's payment history |
| `GET` | `/{transactionId}` | USER | Get specific transaction details |
| `POST` | `/{transactionId}/refund` | ADMIN | Initiate refund |
| `GET` | `/analytics` | ADMIN | Get platform payment analytics |

---

## 💬 Chat APIs

### Base URL: `/api/v1/chat`

#### REST Endpoints

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `GET` | `/conversations` | JWT | Get all user's conversations |
| `GET` | `/conversations/{conversationId}` | JWT | Get specific conversation |
| `GET` | `/conversations/{conversationId}/messages` | JWT | Get messages in conversation |
| `POST` | `/conversations/{conversationId}/messages` | JWT | Send message in conversation |
| `PUT` | `/conversations/{conversationId}/read` | JWT | Mark all messages as read |
| `POST` | `/upload` | JWT | Upload file for chat |

#### WebSocket Endpoints

```
WS ws://localhost:8080/api/v1/ws?token=<JWT_TOKEN>

SUBSCRIBE /topic/vendor/{vendorId}/bids
SUBSCRIBE /topic/vendor/{vendorId}/orders
SUBSCRIBE /topic/vendor/{vendorId}/chats
SUBSCRIBE /topic/bids
SUBSCRIBE /topic/orders
SUBSCRIBE /topic/chats
SUBSCRIBE /queue/notifications-user{sessionId}
```

---

## 🔔 Notification APIs

### Base URL: `/api/v1/notifications`

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `GET` | `/` | JWT | Get user's notifications |
| `GET` | `/?page=0&size=20` | JWT | **Example:** Get page 0, 20 items |
| `GET` | `/unread/count` | JWT | Get count of unread notifications |
| `PUT` | `/{notificationId}/read` | JWT | Mark notification as read |
| `PUT` | `/read-all` | JWT | Mark all notifications as read |
| `DELETE` | `/{notificationId}` | JWT | Delete notification |
| `DELETE` | `/` | JWT | Clear all notifications |

---

## 🎧 Support Tickets APIs

### Base URL: `/api/v1/support/tickets`

#### User/Vendor Endpoints

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `POST` | `/` | JWT | Create support ticket |
| `GET` | `/` | JWT | Get user's tickets |
| `GET` | `/{ticketId}` | JWT | Get specific ticket |
| `PUT` | `/{ticketId}/status` | JWT | Update ticket status |

#### Support Agent Endpoints

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `GET` | `/agent/my` | SUPPORT_AGENT | Get agent's assigned tickets |
| `GET` | `/open` | SUPPORT_AGENT | Get unassigned/open tickets |
| `PUT` | `/{ticketId}/assign` | ADMIN | Assign ticket to agent |
| `PUT` | `/{ticketId}/resolve` | SUPPORT_AGENT | Mark ticket as resolved |
| `PUT` | `/{ticketId}/close` | SUPPORT_AGENT | Close resolved ticket |

---

## 🍽️ Menu Management APIs

### Base URL: `/api/v1/menu`

#### Public/User Endpoints

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `GET` | `/categories` | PUBLIC | Get all menu categories |
| `GET` | `/items` | PUBLIC | Get all menu items |
| `GET` | `/items?categoryId=xyz` | PUBLIC | Filter items by category |
| `GET` | `/items/{itemId}` | PUBLIC | Get specific menu item |

#### Admin Endpoints

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `POST` | `/categories` | ADMIN | Create menu category |
| `PUT` | `/categories/{categoryId}` | ADMIN | Update menu category |
| `DELETE` | `/categories/{categoryId}` | ADMIN | Delete menu category |
| `POST` | `/items` | ADMIN | Create menu item |
| `PUT` | `/items/{itemId}` | ADMIN | Update menu item |
| `DELETE` | `/items/{itemId}` | ADMIN | Delete menu item |

---

## ⭐ Review & Rating APIs

### Base URL: `/api/v1/reviews`

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `POST` | `/` | USER | Create review/rating |
| `GET` | `/vendor/{vendorId}` | PUBLIC | Get vendor's reviews |
| `PUT` | `/{reviewId}` | USER | Update own review |
| `DELETE` | `/{reviewId}` | USER | Delete own review |
| `PUT` | `/{reviewId}/moderate` | ADMIN | Approve/reject/hide review |

---

## 🏷️ Promo Codes APIs

### Base URL: `/api/v1/promos`

#### User Endpoints

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `GET` | `/` | PUBLIC | Get all active promo codes |
| `POST` | `/validate` | USER | Validate promo code for order |

#### Admin Endpoints

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `POST` | `/` | ADMIN | Create new promo code |
| `GET` | `/` | ADMIN | Get all promo codes |
| `PUT` | `/{promoId}` | ADMIN | Update promo code |
| `DELETE` | `/{promoId}` | ADMIN | Delete promo code |
| `PUT` | `/{promoId}/deactivate` | ADMIN | Deactivate promo code |

---

## ⚙️ Admin Management APIs

### Base URL: `/api/v1/admin`

#### User Management

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `GET` | `/users` | ADMIN | Get all users |
| `GET` | `/users/{userId}` | ADMIN | Get user details |
| `PUT` | `/users/{userId}/status` | ADMIN | Change user status |
| `DELETE` | `/users/{userId}` | ADMIN | Delete user |
| `GET` | `/users/search?query=name` | ADMIN | Search users |

#### Vendor Management

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `GET` | `/vendors` | ADMIN | Get all vendors |
| `GET` | `/vendors/{vendorId}` | ADMIN | Get vendor details |
| `PUT` | `/vendors/{vendorId}/approve` | ADMIN | Approve vendor |
| `PUT` | `/vendors/{vendorId}/reject` | ADMIN | Reject vendor |
| `PUT` | `/vendors/{vendorId}/suspend` | ADMIN | Suspend vendor |
| `PUT` | `/vendors/{vendorId}/reactivate` | ADMIN | Reactivate vendor |
| `PUT` | `/vendors/{vendorId}/featured` | ADMIN | Toggle featured status |
| `PUT` | `/vendors/{vendorId}/documents/{documentId}/verify` | ADMIN | Verify document |

#### Configuration

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `GET` | `/config` | ADMIN | Get all config keys |
| `GET` | `/config/{key}` | ADMIN | Get config value |
| `PUT` | `/config/{key}` | ADMIN | Update config value |
| `GET` | `/audit` | ADMIN | Get audit logs |
| `GET` | `/announcements` | PUBLIC | Get announcements |
| `POST` | `/announcements` | ADMIN | Create announcement |
| `PUT` | `/announcements/{announcementId}` | ADMIN | Update announcement |
| `DELETE` | `/announcements/{announcementId}` | ADMIN | Delete announcement |

---

## 📊 Analytics APIs

### Base URL: `/api/v1/analytics`

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| `GET` | `/overview` | ADMIN | Platform overview metrics |
| `GET` | `/vendor/{vendorId}` | ADMIN/VENDOR | Vendor analytics dashboard |
| `GET` | `/user/{userId}` | ADMIN/USER | User analytics |
| `GET` | `/orders` | ADMIN | Order analytics |
| `GET` | `/payments` | ADMIN | Payment analytics |

---

## ✅ Route Resolution Rules

Spring MVC resolves routes in this order (first match wins):

1. **Most specific literal paths first**
   ```
   /requests/available  ← Matches before /requests/{id}
   /vendor/my           ← Matches before /vendor/{id}
   ```

2. **Then path variables**
   ```
   /requests/{bidRequestId}
   /vendor/{vendorId}
   ```

3. **Then catch-all patterns (if any)**

### ⚠️ Common Mistakes to Avoid

```
❌ WRONG: GET /requests/available treated as GET /requests/{id} with id="available"
✅ CORRECT: Define /requests/available BEFORE /requests/{id}

❌ WRONG: GET /vendor/my treated as GET /vendor/{id} with id="my"
✅ CORRECT: Define /vendor/my BEFORE /vendor/{id}

❌ WRONG: POST /api/v1/bids/my (GET only)
✅ CORRECT: Use GET /api/v1/bids/my
```

---

## 📱 Query Parameters Guide

### Pagination

All list endpoints support pagination:

```
GET /api/v1/bids/requests?page=0&size=20

Query Parameters:
- page: int (0-based index, default=0)
- size: int (items per page, default=20)

Response includes:
- data: array of items
- pageInfo: { page, size, totalElements, totalPages, hasMore }
```

### Sorting

Some endpoints support sorting:

```
GET /api/v1/orders?page=0&size=20&sort=createdAt,desc

Query Parameters:
- sort: fieldName,asc|desc
```

### Filtering

Some endpoints support filtering:

```
GET /api/v1/admin/users?userType=VENDOR&status=ACTIVE

Supported filters per endpoint (check documentation)
```

---

## 🔐 Authentication Headers

All endpoints marked with `Auth: JWT` require:

```
Authorization: Bearer <accessToken>

Example:
curl -H "Authorization: Bearer eyJhbGciOiJIUzM4NCJ9..." \
  http://localhost:8080/api/v1/bids/my?page=0&size=20
```

---

## 📚 Error Responses

All endpoints return standard error format:

```json
{
  "success": false,
  "status": 400,
  "message": "Validation failed",
  "data": null,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Request validation failed",
    "path": "/api/v1/bids/requests",
    "timestamp": "2026-03-06T14:52:00Z",
    "fieldErrors": {
      "eventDetails.eventDate": "Event date must be in future"
    }
  }
}
```

---

## 🚀 Implementation Checklist

- [x] All endpoints documented
- [x] Path variable vs literal path conflicts resolved
- [x] Pagination parameters standardized
- [x] Error response format unified
- [x] Authentication requirements clear
- [x] New endpoints added: `/bids/my`, `/bids/requests/available`, `/orders/vendor/my`
- [x] WebSocket endpoints documented
- [x] Query parameter examples provided
- [x] Route resolution order explained


