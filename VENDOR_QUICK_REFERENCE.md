# Vendor APIs - Quick Reference Guide

**Base URL:** `http://localhost:8080/api/v1`

---

## 🚀 Quick Start - Vendor Onboarding

### 1. User Registration
```bash
POST /auth/register
Content-Type: application/json

{
  "email": "vendor@example.com",
  "password": "SecurePass123!",
  "firstName": "Ramesh",
  "lastName": "Kumar",
  "phone": "+919876543210",
  "userType": "CUSTOMER"
}
```

**Response:** `201 Created`
```json
{
  "userId": "user_123",
  "accessToken": "eyJhbGc...",
  "refreshToken": "refresh_token..."
}
```

### 2. Register as Vendor
```bash
POST /vendors
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "businessName": "Delicious Catering Co.",
  "businessEmail": "contact@deliciouscatering.com",
  "businessPhone": "+919876543210",
  "businessType": "CATERING",
  "description": "Premium catering services",
  "businessAddress": {
    "streetAddress": "123 Main Street",
    "city": "Mumbai",
    "state": "Maharashtra",
    "postalCode": "400001"
  },
  "capacity": {
    "minGuests": 50,
    "maxGuests": 5000,
    "concurrentEvents": 5
  },
  "pricing": {
    "currency": "INR",
    "startingPricePerPlate": 500.00,
    "averagePricePerPlate": 750.00
  }
}
```

**Response:** `201 Created`
```json
{
  "vendorId": "vendor_123",
  "status": "PENDING_APPROVAL"
}
```

### 3. Admin Approves Vendor (Admin Only)
```bash
POST /vendors/admin/pending?page=0&size=20
Authorization: Bearer {adminToken}
```

Then:
```bash
POST /vendors/{vendorId}/approve
Authorization: Bearer {adminToken}
```

**Response:** `200 OK`
```json
{
  "vendorId": "vendor_123",
  "status": "ACTIVE",
  "approvalStatus": "APPROVED"
}
```

---

## 📋 Menu Management

### Add Menu Items
```bash
POST /menu/vendor-items
Authorization: Bearer {vendorToken}
Content-Type: application/json

{
  "itemId": "item_123",
  "pricePerUnit": 150.00,
  "minOrderQuantity": 50
}
```

### Update Pricing
```bash
PUT /menu/vendor-items/{vendorItemId}
Authorization: Bearer {vendorToken}

{
  "pricePerUnit": 160.00,
  "minOrderQuantity": 50
}
```

### Toggle Availability
```bash
PATCH /menu/vendor-items/{vendorItemId}/availability?isAvailable=false&reason=Out of stock
Authorization: Bearer {vendorToken}
```

### Get Your Menu Items
```bash
GET /menu/vendor-items?vendorId={vendorId}&page=0&size=20
```

---

## 💰 Bidding System

### Create Bid Request (Customer)
```bash
POST /bids/requests
Authorization: Bearer {customerToken}

{
  "eventName": "Corporate Meeting Catering",
  "eventDate": "2026-06-15T18:00:00Z",
  "guestCount": 150,
  "budget": {
    "minBudget": 20000.00,
    "maxBudget": 50000.00,
    "currency": "INR"
  },
  "cuisinePreferences": ["North Indian", "Continental"]
}
```

### Get Available Bid Requests (Vendor)
```bash
GET /bids/vendor/received?page=0&size=20
Authorization: Bearer {vendorToken}
```

### Submit Bid (Vendor)
```bash
POST /bids/requests/{bidRequestId}/submit-bid
Authorization: Bearer {vendorToken}

{
  "quotedPrice": {
    "perPlate": 150.00,
    "totalAmount": 22500.00,
    "currency": "INR"
  },
  "staffProvided": {
    "chefs": 3,
    "servers": 5,
    "cleaners": 2
  }
}
```

### View Submitted Bids (Vendor)
```bash
GET /bids/vendor/submitted?page=0&size=20
Authorization: Bearer {vendorToken}
```

### Accept Bid (Customer)
```bash
POST /bids/{bidId}/accept
Authorization: Bearer {customerToken}
```

---

## 📦 Order Management

### Create Order from Bid
```bash
POST /orders?bidRequestId={bidRequestId}
Authorization: Bearer {customerToken}
```

**Response:** `201 Created`
```json
{
  "orderId": "order_789",
  "status": "PENDING_TOKEN_PAYMENT",
  "totalAmount": 22500.00,
  "tokenAmount": 5625.00
}
```

### Get My Orders (Customer)
```bash
GET /orders?page=0&size=20
Authorization: Bearer {customerToken}
```

### Get Vendor Orders
```bash
GET /orders/vendor?page=0&size=20
Authorization: Bearer {vendorToken}
```

### Update Order Status (Vendor)
```bash
PATCH /orders/{orderId}/status?status=IN_PROGRESS
Authorization: Bearer {vendorToken}
```

Status values: `CONFIRMED`, `IN_PROGRESS`, `DELIVERED`, `CANCELLED`

---

## 💳 Payment Processing

### Initiate Payment
```bash
POST /payments/initiate?orderId={orderId}&paymentType=TOKEN&amount=5625.00
Authorization: Bearer {customerToken}
```

**Response:** `200 OK`
```json
{
  "transactionId": "txn_123",
  "razorpayOrderId": "order_razorpay_123",
  "razorpayOrderKey": "rzp_test_key_123",
  "status": "INITIATED"
}
```

### Verify Payment
```bash
POST /payments/verify?gatewayOrderId={gid}&gatewayPaymentId={pid}&signature={sig}
Authorization: Bearer {customerToken}
```

### Get Payment History
```bash
GET /payments/transactions?page=0&size=20
Authorization: Bearer {customerToken}
```

---

## ⭐ Reviews & Ratings

### Create Review (Customer)
```bash
POST /reviews
Authorization: Bearer {customerToken}

{
  "orderId": "order_789",
  "vendorId": "vendor_123",
  "rating": 5,
  "foodQualityRating": 5,
  "serviceQualityRating": 4,
  "reviewText": "Excellent service!"
}
```

### Get Vendor Reviews
```bash
GET /reviews/vendor/{vendorId}?page=0&size=20
```

### Vendor Responds to Review
```bash
POST /reviews/{reviewId}/vendor-response?responseText=Thank you for your feedback
Authorization: Bearer {vendorToken}
```

---

## 🛒 Cart Management

### Add to Cart
```bash
POST /cart/items?vendorItemId={vendorItemId}&quantity=100
Authorization: Bearer {customerToken}
```

### Get Cart
```bash
GET /cart
Authorization: Bearer {customerToken}
```

### Get Cart Grouped by Vendor
```bash
GET /cart/grouped
Authorization: Bearer {customerToken}
```

### Get Cart Total
```bash
GET /cart/total
Authorization: Bearer {customerToken}
```

### Clear Cart
```bash
DELETE /cart
Authorization: Bearer {customerToken}
```

---

## 📊 Analytics & Dashboard

### Vendor Dashboard
```bash
GET /analytics/vendor/dashboard
Authorization: Bearer {vendorToken}
```

**Response:** `200 OK`
```json
{
  "vendorId": "vendor_123",
  "stats": {
    "totalOrders": 500,
    "completedOrders": 495,
    "totalRevenue": 1000000.00
  },
  "ratings": {
    "averageRating": 4.5,
    "totalReviews": 150
  }
}
```

### Admin Platform Overview
```bash
GET /analytics/overview
Authorization: Bearer {adminToken}
```

### Revenue Analytics
```bash
GET /analytics/revenue?period=month
Authorization: Bearer {adminToken}
```

---

## 📁 File Uploads

### Upload Vendor Logo
```bash
POST /upload/image
Authorization: Bearer {vendorToken}
Content-Type: multipart/form-data

Form Data:
- file: (binary image file, max 5MB)
- entityType: VENDOR
- entityId: {vendorId}
```

### Upload Business Documents
```bash
POST /upload/document
Authorization: Bearer {vendorToken}
Content-Type: multipart/form-data

Form Data:
- file: (binary PDF, max 10MB)
- entityType: VENDOR
- entityId: {vendorId}
```

### Get Files for Vendor
```bash
GET /upload/entity/VENDOR/{vendorId}
Authorization: Bearer {vendorToken}
```

---

## 🔐 Authentication

### Login
```bash
POST /auth/login
Content-Type: application/json

{
  "identifier": "vendor@example.com",
  "password": "SecurePass123!"
}
```

**Response:** `200 OK`
```json
{
  "userId": "user_123",
  "email": "vendor@example.com",
  "userType": "VENDOR",
  "accessToken": "eyJhbGc...",
  "refreshToken": "refresh_token...",
  "expiresIn": 900000
}
```

### Refresh Token
```bash
POST /auth/refresh-token
Content-Type: application/json

{
  "refreshToken": "{refreshToken}"
}
```

---

## 🔍 Search & Filter

### Search Vendors
```bash
GET /vendors/search?query=catering&city=Mumbai&cuisines=North Indian&rating=4.0&page=0&size=20
```

### Get All Vendors
```bash
GET /vendors?page=0&size=20&city=Mumbai&cuisine=North Indian&sortBy=createdAt&sortDir=desc
```

### Search Menu Items
```bash
GET /menu/items/search?query=paneer&page=0&size=20
```

---

## ❌ Common Error Responses

### 400 Bad Request
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    {
      "field": "businessName",
      "message": "Business name is required"
    }
  ]
}
```

### 401 Unauthorized
```json
{
  "success": false,
  "message": "Unauthorized - Authentication required"
}
```

### 403 Forbidden
```json
{
  "success": false,
  "message": "You don't have permission to perform this action"
}
```

### 404 Not Found
```json
{
  "success": false,
  "message": "Resource not found"
}
```

### 409 Conflict
```json
{
  "success": false,
  "message": "Email already registered"
}
```

---

## 📱 Postman Setup

1. **Create Environment:**
   - `baseUrl`: http://localhost:8080/api/v1
   - `accessToken`: (paste JWT token)
   - `vendorId`: (your vendor ID)
   - `orderId`: (current order ID)

2. **Use in Requests:**
   ```
   {{baseUrl}}/vendors/{{vendorId}}
   Authorization: Bearer {{accessToken}}
   ```

3. **Pre-request Script:**
   ```javascript
   // Auto-refresh token if expired
   if (pm.environment.get('token_expiry') < Date.now()) {
     // Call refresh endpoint
   }
   ```

---

## 📈 Workflow Examples

### Complete Order Flow
1. Customer: POST /bids/requests
2. Vendor: GET /bids/vendor/received
3. Vendor: POST /bids/requests/{bidRequestId}/submit-bid
4. Customer: GET /bids/requests/{bidRequestId}/bids
5. Customer: POST /bids/{bidId}/accept
6. Customer: POST /orders?bidRequestId={bidRequestId}
7. Customer: POST /payments/initiate
8. Vendor: GET /orders/vendor
9. Vendor: PATCH /orders/{orderId}/status
10. Customer: POST /reviews

### Vendor Setup Flow
1. POST /auth/register
2. POST /vendors
3. Admin: POST /vendors/{vendorId}/approve
4. POST /upload/image
5. POST /upload/document
6. POST /menu/vendor-items (multiple items)
7. GET /analytics/vendor/dashboard

---

## 🔗 API Relationships

```
VENDOR
  ├── MENU ITEMS
  │   └── Can bid on BID REQUESTS
  ├── BIDS
  │   └── Converted to ORDERS
  ├── ORDERS
  │   ├── Has PAYMENTS
  │   ├── Has REVIEWS (after completion)
  │   └── Shows in DASHBOARD
  └── ANALYTICS
      └── Tracks performance
```

---

## 💡 Tips & Tricks

1. **Always save tokens** from login/registration responses
2. **Use pagination** for large datasets
3. **Filter by status** to get specific records
4. **Update availability** before accepting orders
5. **Respond to reviews** to maintain customer relationships
6. **Check dashboard** regularly for performance metrics
7. **Upload documents** immediately for faster approval
8. **Sort by date** to see most recent items first

---

**Last Updated:** January 19, 2026

