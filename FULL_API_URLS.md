# Full API URLs (Catering Platform)

Base URL (example):

- http://localhost:8080/api/v1

Note: to call any endpoint below, prepend the base URL above. The entries show the full URL for a representative set of endpoints across controllers (including query parameters where applicable).

---

## Authentication

- POST http://localhost:8080/api/v1/auth/register
  - Description: Register a new user (customer) account.
  - Request JSON example:
    ```json
    {
      "name": "Alice User",
      "email": "alice@example.com",
      "phone": "+919876543210",
      "password": "StrongPassword123",
      "role": "CUSTOMER"  // CUSTOMER | VENDOR | ADMIN (role may be ignored if separate vendor/admin flows exist)
    }
    ```
  - Successful response example (201 Created):
    ```json
    {
      "id": "user_60f5a3d2c9d4f8123a4b9e0f",
      "name": "Alice User",
      "email": "alice@example.com",
      "phone": "+919876543210",
      "roles": ["CUSTOMER"],
      "createdAt": "2026-01-09T04:00:00Z"
    }
    ```
  - Common error responses:
    - 400 Bad Request: validation errors (missing/invalid fields)
    - 409 Conflict: email or phone already registered

- POST http://localhost:8080/api/v1/auth/login
- POST http://localhost:8080/api/v1/auth/refresh-token
- POST http://localhost:8080/api/v1/auth/logout
- POST http://localhost:8080/api/v1/auth/logout-all
- POST http://localhost:8080/api/v1/auth/send-otp
- POST http://localhost:8080/api/v1/auth/verify-otp
- POST http://localhost:8080/api/v1/auth/forgot-password
- POST http://localhost:8080/api/v1/auth/reset-password
- POST http://localhost:8080/api/v1/auth/change-password
- GET  http://localhost:8080/api/v1/auth/me

## Vendor Registration (Separate Flow)

- POST http://localhost:8080/api/v1/vendors
  - Description: Submit vendor registration. This is commonly a public endpoint that creates a vendor record in PENDING state which admins review and approve.
  - Request JSON example (multipart or JSON depending on implementation):
    ```json
    {
      "businessName": "Tasty Catering Co.",
      "contactName": "Ramesh Kumar",
      "email": "vendor@example.com",
      "phone": "+919812345678",
      "city": "Mumbai",
      "cuisines": ["North Indian", "Continental"],
      "address": "123 Market Road",
      "documents": [
        {"type": "GST", "fileId": "<uploaded-file-id>"},
        {"type": "FSSAI", "fileId": "<uploaded-file-id>"}
      ]
    }
    ```
  - Successful response example (201 Created):
    ```json
    {
      "vendorId": "vendor_60f5b3e4a1d2c9123a4b9f10",
      "status": "PENDING",
      "message": "Vendor registration submitted. Admin will review and approve."
    }
    ```
  - Common error responses:
    - 400 Bad Request: missing required fields
    - 409 Conflict: vendor email or phone already used

- GET  http://localhost:8080/api/v1/vendors/me
  - Requires Authorization: Bearer <access-token>
  - Successful response example (200 OK):
    ```json
    {
      "vendorId": "vendor_60f5b3e4a1d2c9123a4b9f10",
      "businessName": "Tasty Catering Co.",
      "contactName": "Ramesh Kumar",
      "email": "vendor@example.com",
      "phone": "+919812345678",
      "city": "Mumbai",
      "status": "APPROVED",
      "cuisines": ["North Indian", "Continental"],
      "createdAt": "2026-01-08T10:00:00Z"
    }
    ```

## Admin Registration / Creation

Note: Typically admin creation is restricted (not open to public). Admins are either seeded at deploy time or created by other admins.

- POST http://localhost:8080/api/v1/admin/users
  - Description: Create a new admin user (Admin-only endpoint).
  - Requires Authorization: Bearer <admin-access-token>
  - Request JSON example:
    ```json
    {
      "name": "Admin Person",
      "email": "admin@example.com",
      "phone": "+911234567890",
      "password": "AdminStrongPass!",
      "roles": ["ADMIN"]
    }
    ```
  - Successful response example (201 Created):
    ```json
    {
      "id": "user_60f5d4f5b2e3f0123a4ba010",
      "name": "Admin Person",
      "email": "admin@example.com",
      "roles": ["ADMIN"],
      "createdAt": "2026-01-08T11:00:00Z"
    }
    ```
  - Common error responses:
    - 403 Forbidden: if caller is not an admin
    - 400 Bad Request, 409 Conflict: validation/duplicate

## Admin

- GET  http://localhost:8080/api/v1/admin/dashboard
- GET  http://localhost:8080/api/v1/admin/users?page=0&size=20&status=ACTIVE&sortBy=createdAt&sortDir=desc
- PATCH http://localhost:8080/api/v1/admin/users/{userId}/status?status=SUSPENDED&reason=Violation
- GET  http://localhost:8080/api/v1/admin/vendors/pending?page=0&size=20
- POST http://localhost:8080/api/v1/admin/vendors/{vendorId}/approve
- POST http://localhost:8080/api/v1/admin/vendors/{vendorId}/reject?reason=Incomplete%20documentation
- GET  http://localhost:8080/api/v1/admin/orders?page=0&size=20&status=CONFIRMED
- GET  http://localhost:8080/api/v1/admin/platform-config?country=India
- PUT  http://localhost:8080/api/v1/admin/platform-config?country=India
- GET  http://localhost:8080/api/v1/admin/audit-logs?page=0&size=50
- GET  http://localhost:8080/api/v1/admin/announcements?page=0&size=20
- POST http://localhost:8080/api/v1/admin/announcements
- DELETE http://localhost:8080/api/v1/admin/announcements/{announcementId}

## Bids

- POST http://localhost:8080/api/v1/bids/requests
- GET  http://localhost:8080/api/v1/bids/requests?page=0&size=20
- GET  http://localhost:8080/api/v1/bids/requests/{bidRequestId}
- DELETE http://localhost:8080/api/v1/bids/requests/{bidRequestId}
- GET  http://localhost:8080/api/v1/bids/requests/{bidRequestId}/bids
- POST http://localhost:8080/api/v1/bids/{bidId}/accept
- POST http://localhost:8080/api/v1/bids/requests/{bidRequestId}/submit-bid
- PUT  http://localhost:8080/api/v1/bids/{bidId}?reason=Price%20adjustment
- DELETE http://localhost:8080/api/v1/bids/{bidId}
- GET  http://localhost:8080/api/v1/bids/vendor/submitted?page=0&size=20
- GET  http://localhost:8080/api/v1/bids/vendor/received?page=0&size=20
- GET  http://localhost:8080/api/v1/bids/active?page=0&size=20

## Cart

- GET  http://localhost:8080/api/v1/cart
- GET  http://localhost:8080/api/v1/cart/grouped
- POST http://localhost:8080/api/v1/cart/items?vendorItemId={vendorItemId}&quantity={quantity}
- PUT  http://localhost:8080/api/v1/cart/items/{cartItemId}?quantity={quantity}
- DELETE http://localhost:8080/api/v1/cart/items/{cartItemId}
- DELETE http://localhost:8080/api/v1/cart
- GET  http://localhost:8080/api/v1/cart/count
- GET  http://localhost:8080/api/v1/cart/total

## Menu

- GET  http://localhost:8080/api/v1/menu/categories
- GET  http://localhost:8080/api/v1/menu/categories/{categoryId}
- GET  http://localhost:8080/api/v1/menu/items?page=0&size=20
- GET  http://localhost:8080/api/v1/menu/items/{itemId}
- GET  http://localhost:8080/api/v1/menu/items/category/{categoryId}?page=0&size=20
- GET  http://localhost:8080/api/v1/menu/items/search?query={q}&page=0&size=20
- GET  http://localhost:8080/api/v1/menu/items/popular
- GET  http://localhost:8080/api/v1/menu/vendor-items?vendorId={vendorId}&page=0&size=20
- GET  http://localhost:8080/api/v1/menu/vendor-items/{vendorItemId}
- POST http://localhost:8080/api/v1/menu/vendor-items
- PUT  http://localhost:8080/api/v1/menu/vendor-items/{vendorItemId}
- PATCH http://localhost:8080/api/v1/menu/vendor-items/{vendorItemId}/availability?isAvailable={true|false}&reason={reason}
- DELETE http://localhost:8080/api/v1/menu/vendor-items/{vendorItemId}

## Orders

- POST http://localhost:8080/api/v1/orders?bidRequestId={bidRequestId}
- GET  http://localhost:8080/api/v1/orders?page=0&size=20
- GET  http://localhost:8080/api/v1/orders/{orderId}
- PATCH http://localhost:8080/api/v1/orders/{orderId}/status?status={STATUS}
- POST http://localhost:8080/api/v1/orders/{orderId}/cancel?reason={reason}
- GET  http://localhost:8080/api/v1/orders/upcoming?page=0&size=20
- GET  http://localhost:8080/api/v1/orders/history?page=0&size=20
- GET  http://localhost:8080/api/v1/orders/vendor?page=0&size=20

## Payments

- POST http://localhost:8080/api/v1/payments/initiate?orderId={orderId}&paymentType={TYPE}&amount={amount}
- POST http://localhost:8080/api/v1/payments/verify?gatewayOrderId={gid}&gatewayPaymentId={pid}&signature={sig}
- GET  http://localhost:8080/api/v1/payments/transactions?page=0&size=20
- GET  http://localhost:8080/api/v1/payments/transactions/{transactionId}
- GET  http://localhost:8080/api/v1/payments/order/{orderId}
- POST http://localhost:8080/api/v1/payments/webhook/razorpay (raw webhook payload)

## Vendors

- POST http://localhost:8080/api/v1/vendors
- GET  http://localhost:8080/api/v1/vendors?page=0&size=20&city={city}&cuisine={cuisine}
- GET  http://localhost:8080/api/v1/vendors/{vendorId}
- GET  http://localhost:8080/api/v1/vendors/me
- PUT  http://localhost:8080/api/v1/vendors/{vendorId}
- GET  http://localhost:8080/api/v1/vendors/search?query={q}&city={city}&rating={rating}&page=0&size=20
- GET  http://localhost:8080/api/v1/vendors/admin/pending?page=0&size=20 (Admin)
- POST http://localhost:8080/api/v1/vendors/{vendorId}/approve (Admin)
- POST http://localhost:8080/api/v1/vendors/{vendorId}/reject?reason={reason} (Admin)

## Users

- GET  http://localhost:8080/api/v1/users/profile
- PUT  http://localhost:8080/api/v1/users/profile
- PATCH http://localhost:8080/api/v1/users/profile-picture?imageUrl={url}
- GET  http://localhost:8080/api/v1/users/notification-preferences
- PUT  http://localhost:8080/api/v1/users/notification-preferences
- GET  http://localhost:8080/api/v1/users/addresses
- GET  http://localhost:8080/api/v1/users/addresses/{addressId}
- POST http://localhost:8080/api/v1/users/addresses
- PUT  http://localhost:8080/api/v1/users/addresses/{addressId}
- DELETE http://localhost:8080/api/v1/users/addresses/{addressId}
- PATCH http://localhost:8080/api/v1/users/addresses/{addressId}/default

## Reviews

- POST http://localhost:8080/api/v1/reviews
- GET  http://localhost:8080/api/v1/reviews/{reviewId}
- GET  http://localhost:8080/api/v1/reviews/vendor/{vendorId}?page=0&size=20
- GET  http://localhost:8080/api/v1/reviews/my?page=0&size=20
- POST http://localhost:8080/api/v1/reviews/{reviewId}/vendor-response?responseText={text}
- POST http://localhost:8080/api/v1/reviews/{reviewId}/helpful
- POST http://localhost:8080/api/v1/reviews/{reviewId}/report?reason={reason}
- PUT  http://localhost:8080/api/v1/reviews/{reviewId}
- DELETE http://localhost:8080/api/v1/reviews/{reviewId}

## Promo Codes

- GET  http://localhost:8080/api/v1/promos/available
- POST http://localhost:8080/api/v1/promos/apply
- POST http://localhost:8080/api/v1/promos (Admin)
- GET  http://localhost:8080/api/v1/promos?page=0&size=20 (Admin)
- GET  http://localhost:8080/api/v1/promos/{promoCodeId} (Admin)
- PUT  http://localhost:8080/api/v1/promos/{promoCodeId} (Admin)
- DELETE http://localhost:8080/api/v1/promos/{promoCodeId} (Admin)

## Notifications

- GET  http://localhost:8080/api/v1/notifications?page=0&size=20
- GET  http://localhost:8080/api/v1/notifications/unread?page=0&size=20
- GET  http://localhost:8080/api/v1/notifications/count
- PATCH http://localhost:8080/api/v1/notifications/{notificationId}/read
- PATCH http://localhost:8080/api/v1/notifications/read-all

## Loyalty

- GET  http://localhost:8080/api/v1/loyalty/balance
- GET  http://localhost:8080/api/v1/loyalty/transactions?page=0&size=20
- GET  http://localhost:8080/api/v1/loyalty/calculate?orderAmount={amount}

## Referral

- GET  http://localhost:8080/api/v1/referral/code
- GET  http://localhost:8080/api/v1/referral/stats
- GET  http://localhost:8080/api/v1/referral/validate/{code}

## Support

- POST http://localhost:8080/api/v1/support/tickets
- GET  http://localhost:8080/api/v1/support/tickets?page=0&size=20
- GET  http://localhost:8080/api/v1/support/tickets/{ticketId}
- GET  http://localhost:8080/api/v1/support/admin/tickets?page=0&size=20 (Support/Admin)
- GET  http://localhost:8080/api/v1/support/admin/my-tickets?page=0&size=20 (Support/Admin)
- POST http://localhost:8080/api/v1/support/admin/tickets/{ticketId}/assign?agentId={agentId}
- PATCH http://localhost:8080/api/v1/support/admin/tickets/{ticketId}/status?status={status}
- POST http://localhost:8080/api/v1/support/admin/tickets/{ticketId}/resolve?resolutionNotes={notes}
- POST http://localhost:8080/api/v1/support/tickets/{ticketId}/messages
- POST http://localhost:8080/api/v1/support/tickets/{ticketId}/rate?rating={rating}&feedback={text}

## Analytics

- GET  http://localhost:8080/api/v1/analytics/overview (Admin)
- GET  http://localhost:8080/api/v1/analytics/revenue?period=month (Admin)
- GET  http://localhost:8080/api/v1/analytics/users (Admin)
- GET  http://localhost:8080/api/v1/analytics/vendors (Admin)
- GET  http://localhost:8080/api/v1/analytics/orders?period=month (Admin)
- GET  http://localhost:8080/api/v1/analytics/reports?reportType=REVENUE&startDate=2026-01-01&endDate=2026-01-31 (Admin)
- GET  http://localhost:8080/api/v1/analytics/user/dashboard
- GET  http://localhost:8080/api/v1/analytics/vendor/dashboard (Vendor)

## File Upload

- POST http://localhost:8080/api/v1/upload/image (multipart/form-data)
- POST http://localhost:8080/api/v1/upload/document (multipart/form-data)
- GET  http://localhost:8080/api/v1/upload/{fileId}
- GET  http://localhost:8080/api/v1/upload/entity/{entityType}/{entityId}
- DELETE http://localhost:8080/api/v1/upload/{fileId}

## Wishlist

- GET  http://localhost:8080/api/v1/wishlist?page=0&size=20
- POST http://localhost:8080/api/v1/wishlist/items?vendorItemId={vendorItemId}
- DELETE http://localhost:8080/api/v1/wishlist/items/{wishlistItemId}
- DELETE http://localhost:8080/api/v1/wishlist
- GET  http://localhost:8080/api/v1/wishlist/check/{vendorItemId}
- GET  http://localhost:8080/api/v1/wishlist/count

## Chat

- GET  http://localhost:8080/api/v1/chat/conversations?page=0&size=20
- GET  http://localhost:8080/api/v1/chat/conversations/{conversationId}
- POST http://localhost:8080/api/v1/chat/conversations?otherUserId={otherUserId}&type=USER_VENDOR
- GET  http://localhost:8080/api/v1/chat/conversations/{conversationId}/messages?page=0&size=50
- POST http://localhost:8080/api/v1/chat/messages?conversationId={conversationId}&content={content}&messageType=TEXT
- PATCH http://localhost:8080/api/v1/chat/conversations/{conversationId}/read
- DELETE http://localhost:8080/api/v1/chat/messages/{messageId}

---

If you'd like, I can now:

- Expand this into a CSV or machine-readable list (one endpoint per row).
- Update `API_DOCUMENTATION.md` in-place to replace the relative paths with these full URLs (if you prefer every endpoint entry to show the full URL).
- Add a short README with copy-paste curl examples for every major flow (register -> login -> create bid -> submit bid -> accept bid -> create order -> initiate payment).

Tell me which of the above you'd prefer next and I'll implement it.
