# 🛡️ ADMIN API DOCUMENTATION
## Bidzaro Catering Platform — Complete Admin Guide

**Version:** 1.0.0
**Base URL:** `http://localhost:8080/api/v1`
**Target Audience:** Platform Administrators
**Authentication:** JWT Bearer Token (Role: ADMIN)

---

## 📋 Table of Contents

1. [Admin Authentication](#1-admin-authentication)
2. [Dashboard Statistics](#2-dashboard-statistics)
3. [User Management](#3-user-management)
4. [Vendor Management](#4-vendor-management)
5. [Order Management](#5-order-management)
6. [Bid Management](#6-bid-management)
7. [Menu Management (Master Catalog)](#7-menu-management-master-catalog)
8. [Payment & Transaction Management](#8-payment--transaction-management)
9. [Promo Code Management](#9-promo-code-management)
10. [Support Ticket Management](#10-support-ticket-management)
11. [Analytics & Reports](#11-analytics--reports)
12. [Platform Configuration](#12-platform-configuration)
13. [Announcements](#13-announcements)
14. [Audit Logs](#14-audit-logs)

---

## 🔐 Admin Auth Flow

```
Admin accounts are created directly in the database with userType: ADMIN
Login  →  POST /auth/login  →  Get accessToken
All admin endpoints require:  Authorization: Bearer {accessToken}
Most admin endpoints require:  Role ADMIN  (enforced server-side)
```

---

# 1. Admin Authentication

## 1.1 Admin Login

```http
POST /auth/login
Content-Type: application/json
```

### Request Payload
```json
{
  "identifier": "admin@bidzaro.com",
  "password": "AdminSecure@2026",
  "fcmToken": "firebase_admin_device_token",
  "deviceInfo": "Admin Dashboard - Chrome Browser"
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 604800,
    "user": {
      "userId": "admin-user-001",
      "email": "admin@bidzaro.com",
      "firstName": "Platform",
      "lastName": "Admin",
      "userType": "ADMIN",
      "status": "ACTIVE"
    }
  }
}
```

---

# 2. Dashboard Statistics

## 2.1 Get Full Dashboard Stats

**Use:** Get a complete real-time snapshot of platform health.

```http
GET /admin/dashboard/stats
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "userStats": {
      "totalUsers": 5420,
      "activeUsers": 4856,
      "newUsersToday": 42,
      "newUsersThisWeek": 287,
      "newUsersThisMonth": 1250
    },
    "vendorStats": {
      "totalVendors": 342,
      "activeVendors": 298,
      "pendingApproval": 32,
      "verifiedVendors": 287,
      "newVendorsThisMonth": 25
    },
    "orderStats": {
      "totalOrders": 12450,
      "pendingOrders": 145,
      "completedOrders": 12105,
      "cancelledOrders": 200,
      "ordersToday": 32,
      "ordersThisWeek": 285,
      "ordersThisMonth": 1450
    },
    "revenueStats": {
      "totalRevenue": 42500000,
      "revenueToday": 450000,
      "revenueThisWeek": 3250000,
      "revenueThisMonth": 12500000,
      "platformFees": 850000,
      "pendingPayouts": 1200000,
      "currency": "INR"
    },
    "bidStats": {
      "totalBidRequests": 23000,
      "activeBidRequests": 156,
      "totalBidsSubmitted": 85000,
      "acceptedBids": 21500,
      "averageBidsPerRequest": 3.7
    }
  }
}
```

---

# 3. User Management

## 3.1 Get All Users

```http
GET /admin/users?page=0&size=20&status=ACTIVE&userType=USER
Authorization: Bearer {accessToken}
```

### Query Parameters
| Param | Values | Description |
|-------|--------|-------------|
| `page` | integer | Page number (default 0) |
| `size` | integer | Items per page (default 20) |
| `status` | `ACTIVE`, `PENDING_VERIFICATION`, `SUSPENDED`, `DELETED` | Filter by status |
| `userType` | `USER`, `VENDOR`, `ADMIN`, `SUPPORT_AGENT` | Filter by type |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "email": "john.doe@gmail.com",
      "phone": "+917890123456",
      "firstName": "John",
      "lastName": "Doe",
      "fullName": "John Doe",
      "userType": "USER",
      "status": "ACTIVE",
      "country": "INDIA",
      "emailVerified": true,
      "phoneVerified": true,
      "lastLoginAt": "2026-02-24T10:30:45.123Z",
      "createdAt": "2026-01-10T08:00:00.000Z"
    },
    {
      "userId": "vendor-user-550e8400",
      "email": "owner@spicegarden.com",
      "userType": "VENDOR",
      "status": "ACTIVE",
      "createdAt": "2026-01-15T08:00:00.000Z"
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 5420,
    "totalPages": 272
  }
}
```

---

## 3.2 Get User by ID

```http
GET /admin/users/{userId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "john.doe@gmail.com",
    "phone": "+917890123456",
    "firstName": "John",
    "lastName": "Doe",
    "fullName": "John Doe",
    "profilePictureUrl": null,
    "dateOfBirth": "1992-06-15",
    "gender": "MALE",
    "userType": "USER",
    "status": "ACTIVE",
    "country": "INDIA",
    "preferredCurrency": "INR",
    "preferredLanguage": "en",
    "emailVerified": true,
    "phoneVerified": true,
    "twoFactorEnabled": false,
    "failedLoginAttempts": 0,
    "lastLoginAt": "2026-02-24T10:30:45.123Z",
    "createdAt": "2026-01-10T08:00:00.000Z",
    "updatedAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 3.3 Update User Status

**Use:** Activate, suspend, or delete a user account.

```http
PATCH /admin/users/{userId}/status
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "status": "SUSPENDED",
  "reason": "Violation of platform terms — fraudulent activity detected"
}
```

### Allowed Status Values
| Status | Effect |
|--------|--------|
| `ACTIVE` | User can login and use the platform |
| `SUSPENDED` | User cannot login, sees suspension message |
| `DELETED` | Soft deleted, account deactivated |

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "User status updated to SUSPENDED",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "SUSPENDED",
    "reason": "Violation of platform terms — fraudulent activity detected",
    "updatedBy": "admin-user-001",
    "updatedAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 3.4 Search Users

```http
GET /admin/users/search?query=john&page=0&size=20
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "fullName": "John Doe",
      "email": "john.doe@gmail.com",
      "userType": "USER",
      "status": "ACTIVE"
    }
  ]
}
```

---

## 3.5 Create Support Agent Account

**Use:** Create a new support agent user account.

```http
POST /admin/users/create-agent
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "email": "agent.priya@bidzaro.com",
  "phone": "+919876500001",
  "password": "AgentPass@2026",
  "firstName": "Priya",
  "lastName": "Sharma",
  "userType": "SUPPORT_AGENT",
  "country": "INDIA"
}
```

### Success Response `201 Created`
```json
{
  "success": true,
  "message": "Support agent account created",
  "data": {
    "userId": "agent-priya-001",
    "email": "agent.priya@bidzaro.com",
    "firstName": "Priya",
    "lastName": "Sharma",
    "userType": "SUPPORT_AGENT",
    "status": "ACTIVE",
    "createdAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

# 4. Vendor Management

## 4.1 Get All Vendors

```http
GET /admin/vendors?page=0&size=20&approvalStatus=PENDING&country=INDIA
Authorization: Bearer {accessToken}
```

### Query Parameters
| Param | Values | Description |
|-------|--------|-------------|
| `approvalStatus` | `PENDING`, `APPROVED`, `REJECTED`, `UNDER_REVIEW` | Filter by approval status |
| `status` | `ACTIVE`, `PENDING_APPROVAL`, `SUSPENDED`, `REJECTED` | Vendor status |
| `country` | `INDIA`, `USA` | Filter by country |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "vendorId": "vendor-12345-67890",
      "userId": "vendor-user-550e8400",
      "businessName": "Spice Garden Catering",
      "businessEmail": "info@spicegarden.com",
      "businessType": "CATERING",
      "country": "INDIA",
      "approvalStatus": "PENDING",
      "status": "PENDING_APPROVAL",
      "verified": false,
      "documents": [
        { "documentType": "BUSINESS_LICENSE", "verificationStatus": "PENDING" },
        { "documentType": "TAX_CERTIFICATE", "verificationStatus": "PENDING" }
      ],
      "createdAt": "2026-02-22T08:00:00.000Z"
    }
  ],
  "pageInfo": { "totalElements": 342, "totalPages": 18 }
}
```

---

## 4.2 Get Vendors Pending Approval

```http
GET /vendors/admin/pending?page=0&size=20
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "vendorId": "vendor-pending-001",
      "businessName": "New Star Catering",
      "businessEmail": "info@newstar.com",
      "businessType": "CATERING",
      "country": "INDIA",
      "approvalStatus": "PENDING",
      "ownerInfo": {
        "firstName": "Suresh",
        "lastName": "Patel",
        "phone": "+919876543211"
      },
      "businessAddress": {
        "city": "Mumbai",
        "state": "Maharashtra"
      },
      "documents": [
        {
          "documentType": "BUSINESS_LICENSE",
          "documentName": "FSSAI License",
          "documentUrl": "http://localhost:8080/uploads/documents/fssai.pdf",
          "verificationStatus": "PENDING"
        }
      ],
      "createdAt": "2026-02-20T10:00:00.000Z"
    }
  ],
  "pageInfo": { "totalElements": 32, "totalPages": 2 }
}
```

---

## 4.3 Approve Vendor

**Use:** Approve a vendor's registration. Triggers approval email to vendor.

```http
POST /vendors/{vendorId}/approve
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Vendor approved successfully",
  "data": {
    "vendorId": "vendor-pending-001",
    "businessName": "New Star Catering",
    "approvalStatus": "APPROVED",
    "status": "ACTIVE",
    "approvalDate": "2026-02-24T10:30:45.123Z",
    "approvedBy": "admin-user-001"
  }
}
```

---

## 4.4 Reject Vendor

**Use:** Reject vendor registration with a reason. Vendor gets notified via email.

```http
POST /vendors/{vendorId}/reject?reason=Documents+are+expired+or+invalid
Authorization: Bearer {accessToken}
```

### Query Parameters
| Param | Required | Description |
|-------|----------|-------------|
| `reason` | ✅ | Reason shown to vendor in rejection email |

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Vendor rejected",
  "data": {
    "vendorId": "vendor-pending-001",
    "businessName": "New Star Catering",
    "approvalStatus": "REJECTED",
    "rejectionReason": "Documents are expired or invalid",
    "rejectedBy": "admin-user-001",
    "rejectedAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 4.5 Suspend / Reactivate Vendor

```http
PATCH /admin/vendors/{vendorId}/status
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "status": "SUSPENDED",
  "reason": "Multiple customer complaints received about food quality"
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "vendorId": "vendor-12345-67890",
    "status": "SUSPENDED",
    "reason": "Multiple customer complaints received about food quality",
    "updatedAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 4.6 Toggle Vendor Featured Status

```http
PATCH /admin/vendors/{vendorId}/featured?featured=true
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "vendorId": "vendor-12345-67890",
    "businessName": "Spice Garden Catering",
    "featured": true,
    "message": "Vendor marked as featured"
  }
}
```

---

# 5. Order Management

## 5.1 Get All Orders

```http
GET /admin/orders?page=0&size=20&status=CONFIRMED
Authorization: Bearer {accessToken}
```

### Query Parameters
| Param | Values | Description |
|-------|--------|-------------|
| `status` | `CONFIRMED`, `IN_PREPARATION`, `DELIVERED`, `COMPLETED`, `CANCELLED` | Order status |
| `vendorId` | string | Filter by vendor |
| `userId` | string | Filter by customer |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "orderId": "order-54321-12345",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "customerEmail": "john.doe@gmail.com",
      "status": "CONFIRMED",
      "eventDetails": {
        "eventType": "WEDDING",
        "eventName": "Priya & Rahul Wedding",
        "eventDate": "2026-05-20",
        "numberOfGuests": 500
      },
      "vendorOrders": [
        {
          "vendorId": "vendor-12345-67890",
          "vendorName": "Spice Garden Catering",
          "totalAmount": 198000
        }
      ],
      "pricing": {
        "totalAmount": 202000,
        "platformFee": 4000,
        "currency": "INR"
      },
      "paymentDetails": {
        "paymentStatus": "TOKEN_PAID",
        "totalPaid": 50500,
        "balanceDue": 151500
      },
      "createdAt": "2026-02-25T11:00:00.000Z"
    }
  ],
  "pageInfo": { "totalElements": 12450, "totalPages": 623 }
}
```

---

## 5.2 Get Order Details

```http
GET /admin/orders/{orderId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "orderId": "order-54321-12345",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "bidRequestId": "breq-88990-77665",
    "status": "CONFIRMED",
    "confirmedAt": "2026-02-25T11:00:00.000Z",
    "eventDetails": {
      "eventType": "WEDDING",
      "eventDate": "2026-05-20",
      "numberOfGuests": 500,
      "venueAddress": { "city": "Bangalore", "state": "Karnataka" }
    },
    "vendorOrders": [
      {
        "vendorId": "vendor-12345-67890",
        "vendorName": "Spice Garden Catering",
        "vendorStatus": "ACCEPTED",
        "deliveryStatus": "PENDING",
        "totalAmount": 198000
      }
    ],
    "pricing": {
      "subtotal": 169900,
      "serviceCharges": 16990,
      "taxAmount": 16990,
      "platformFee": 3980,
      "totalAmount": 207860,
      "currency": "INR"
    },
    "paymentDetails": {
      "tokenAmount": 51965,
      "tokenPaid": true,
      "tokenPaidAt": "2026-02-25T11:05:00.000Z",
      "balanceDue": 155895,
      "paymentStatus": "TOKEN_PAID"
    }
  }
}
```

---

## 5.3 Override Order Status (Admin)

**Use:** Manually force-change an order status in exceptional cases.

```http
PATCH /admin/orders/{orderId}/status
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "status": "CANCELLED",
  "reason": "Vendor became unavailable due to emergency. Refund initiated."
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "orderId": "order-54321-12345",
    "status": "CANCELLED",
    "reason": "Vendor became unavailable due to emergency. Refund initiated.",
    "updatedBy": "admin-user-001",
    "updatedAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

# 6. Bid Management

## 6.1 Get All Bid Requests

```http
GET /admin/bids/requests?page=0&size=20&status=ACTIVE
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "bidRequestId": "breq-88990-77665",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "customerEmail": "john.doe@gmail.com",
      "status": "ACTIVE",
      "eventDetails": {
        "eventType": "WEDDING",
        "eventDate": "2026-05-20T18:00:00Z",
        "numberOfGuests": 500,
        "venueAddress": { "city": "Bangalore" }
      },
      "budget": { "estimatedBudget": 200000, "currency": "INR" },
      "competitivePeriod": {
        "endTime": "2026-02-27T10:30:45.123Z",
        "status": "ACTIVE"
      },
      "createdAt": "2026-02-24T10:30:45.123Z"
    }
  ],
  "pageInfo": { "totalElements": 23000, "totalPages": 1150 }
}
```

---

## 6.2 Get All Vendor Bids (with Filters)

```http
GET /admin/bids?page=0&size=20&status=PENDING&vendorId=vendor-12345-67890
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "bidId": "bid-vendor-spice-001",
      "bidRequestId": "breq-88990-77665",
      "vendorId": "vendor-12345-67890",
      "vendorName": "Spice Garden Catering",
      "status": "PENDING",
      "quotedPrice": {
        "totalAmount": 198000,
        "currency": "INR"
      },
      "submittedAt": "2026-02-24T10:30:45.123Z",
      "revisionCount": 1
    }
  ]
}
```

---

# 7. Menu Management (Master Catalog)

## 7.1 Create New Menu Category

```http
POST /admin/menu/categories
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "categoryName": "Live Counters",
  "categoryNameHindi": "लाइव काउंटर",
  "description": "Live cooking stations for events",
  "iconUrl": "http://localhost:8080/uploads/icons/live-counter.svg",
  "displayOrder": 8
}
```

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "categoryId": "cat-008",
    "categoryName": "Live Counters",
    "categoryNameHindi": "लाइव काउंटर",
    "status": "ACTIVE",
    "displayOrder": 8,
    "createdAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 7.2 Update Menu Category

```http
PUT /admin/menu/categories/{categoryId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "categoryName": "Live Cooking Stations",
  "description": "Live cooking and counter stations for events",
  "displayOrder": 5,
  "status": "ACTIVE"
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "categoryId": "cat-008",
    "categoryName": "Live Cooking Stations",
    "displayOrder": 5,
    "updatedAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 7.3 Create Master Menu Item

```http
POST /admin/menu-items
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "itemName": "Dal Makhani",
  "itemNameHindi": "दाल मखनी",
  "description": "Rich and creamy slow-cooked black lentils in butter and cream",
  "categoryId": "cat-002",
  "cuisineType": "North Indian",
  "foodType": "VEGETARIAN",
  "spiceLevel": "MILD",
  "dietaryTags": ["VEGETARIAN", "GLUTEN_FREE"],
  "allergens": ["DAIRY"],
  "nutritionalInfo": {
    "calories": 280,
    "proteinGrams": 12,
    "carbsGrams": 32,
    "fatGrams": 11,
    "servingSizeGrams": 200
  },
  "imageUrls": [
    "http://localhost:8080/uploads/images/dal-makhani-1.jpg"
  ],
  "isPopular": true
}
```

### Field Rules
| Field | Required | Values |
|-------|----------|--------|
| `itemName` | ✅ | Max 255 chars |
| `categoryId` | ✅ | Must exist |
| `foodType` | ✅ | `VEGETARIAN`, `NON_VEGETARIAN`, `VEGAN`, `JAIN` |
| `spiceLevel` | ❌ | `MILD`, `MEDIUM`, `HOT`, `EXTRA_HOT` |
| `dietaryTags` | ❌ | Array: `VEGETARIAN`, `VEGAN`, `GLUTEN_FREE`, `HALAL`, `JAIN` |

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "masterItemId": "item-150",
    "itemName": "Dal Makhani",
    "categoryId": "cat-002",
    "foodType": "VEGETARIAN",
    "isPopular": true,
    "status": "ACTIVE",
    "createdAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 7.4 Update Master Menu Item

```http
PUT /admin/menu-items/{masterItemId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "itemName": "Dal Makhani (Signature)",
  "description": "Updated: Rich and creamy slow-cooked black lentils",
  "isPopular": true,
  "dietaryTags": ["VEGETARIAN", "GLUTEN_FREE", "JAIN"]
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "masterItemId": "item-150",
    "itemName": "Dal Makhani (Signature)",
    "updatedAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 7.5 Delete / Disable Master Menu Item

```http
DELETE /admin/menu-items/{masterItemId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Menu item deactivated successfully"
}
```

---

# 8. Payment & Transaction Management

## 8.1 Get All Transactions

```http
GET /admin/transactions?page=0&size=20&status=SUCCESS&gateway=RAZORPAY
Authorization: Bearer {accessToken}
```

### Query Parameters
| Param | Values | Description |
|-------|--------|-------------|
| `status` | `PENDING`, `SUCCESS`, `FAILED`, `REFUNDED` | Transaction status |
| `gateway` | `RAZORPAY`, `STRIPE` | Payment gateway |
| `paymentType` | `TOKEN`, `FULL`, `PARTIAL` | Type of payment |
| `userId` | string | Filter by user |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "transactionId": "txn-99887-66554",
      "orderId": "order-54321-12345",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "paymentType": "TOKEN",
      "amount": {
        "currency": "INR",
        "amount": 50500
      },
      "paymentGateway": "RAZORPAY",
      "gatewayOrderId": "order_RazorpayOrderId123",
      "gatewayTransactionId": "pay_RazorpayPaymentId456",
      "status": "SUCCESS",
      "initiatedAt": "2026-02-25T11:00:00.000Z",
      "processedAt": "2026-02-25T11:05:00.000Z"
    }
  ],
  "pageInfo": { "totalElements": 12450, "totalPages": 623 }
}
```

---

## 8.2 Get Transaction Details

```http
GET /admin/transactions/{transactionId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "transactionId": "txn-99887-66554",
    "orderId": "order-54321-12345",
    "bidId": null,
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "paymentType": "TOKEN",
    "amount": {
      "currency": "INR",
      "amount": 50500
    },
    "paymentGateway": "RAZORPAY",
    "gatewayOrderId": "order_RazorpayOrderId123",
    "gatewayTransactionId": "pay_RazorpayPaymentId456",
    "status": "SUCCESS",
    "metadata": {
      "ipAddress": "106.220.1.1",
      "userAgent": "Mozilla/5.0..."
    },
    "initiatedAt": "2026-02-25T11:00:00.000Z",
    "processedAt": "2026-02-25T11:05:00.000Z"
  }
}
```

---

## 8.3 Initiate Refund

```http
POST /admin/transactions/{transactionId}/refund
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "refundAmount": 50500,
  "reason": "Order cancelled by admin due to vendor unavailability",
  "refundType": "FULL"
}
```

| Field | Required | Values |
|-------|----------|--------|
| `refundAmount` | ✅ | Amount to refund |
| `reason` | ✅ | Reason for refund |
| `refundType` | ✅ | `FULL` or `PARTIAL` |

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Refund initiated successfully",
  "data": {
    "refundId": "refund-001",
    "transactionId": "txn-99887-66554",
    "refundAmount": 50500,
    "currency": "INR",
    "status": "PROCESSING",
    "initiatedBy": "admin-user-001",
    "initiatedAt": "2026-02-24T10:30:45.123Z",
    "estimatedCompletion": "3-5 business days"
  }
}
```

---

# 9. Promo Code Management

## 9.1 Create Promo Code

```http
POST /promos
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "code": "BIDZARO2026",
  "title": "Bidzaro Launch Offer",
  "description": "Flat 10% off on all orders above ₹20,000",
  "type": "PERCENTAGE",
  "value": 10,
  "maxDiscountAmount": 5000,
  "minOrderAmount": 20000,
  "validFrom": "2026-02-24T00:00:00Z",
  "validTo": "2026-03-31T23:59:59Z",
  "usageLimitGlobal": 5000,
  "usageLimitPerUser": 1,
  "applicableTo": "ALL",
  "applicableVendorIds": [],
  "applicableUserIds": [],
  "firstOrderOnly": false
}
```

### Promo Type Reference
| Type | Effect |
|------|--------|
| `PERCENTAGE` | Discount by percentage (e.g., 10% off) |
| `FIXED_AMOUNT` | Fixed amount discount (e.g., ₹500 off) |

### `applicableTo` Reference
| Value | Effect |
|-------|--------|
| `ALL` | Available to all users |
| `SPECIFIC_USERS` | Only for users in `applicableUserIds` |
| `SPECIFIC_VENDORS` | Only for orders from vendors in `applicableVendorIds` |
| `SPECIFIC_CUISINES` | Only for specified cuisine types |

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "promoCodeId": "promo-bidzaro-2026",
    "code": "BIDZARO2026",
    "title": "Bidzaro Launch Offer",
    "type": "PERCENTAGE",
    "value": 10,
    "maxDiscountAmount": 5000,
    "minOrderAmount": 20000,
    "status": "ACTIVE",
    "usageCount": 0,
    "validFrom": "2026-02-24T00:00:00Z",
    "validTo": "2026-03-31T23:59:59Z",
    "createdAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 9.2 Get All Promo Codes

```http
GET /promos?page=0&size=20&status=ACTIVE
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "promoCodeId": "promo-bidzaro-2026",
      "code": "BIDZARO2026",
      "title": "Bidzaro Launch Offer",
      "type": "PERCENTAGE",
      "value": 10,
      "status": "ACTIVE",
      "usageCount": 125,
      "usageLimitGlobal": 5000,
      "validTo": "2026-03-31T23:59:59Z"
    }
  ],
  "pageInfo": { "totalElements": 35, "totalPages": 2 }
}
```

---

## 9.3 Update Promo Code

```http
PUT /promos/{promoCodeId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "title": "Bidzaro Super Launch Offer",
  "value": 15,
  "maxDiscountAmount": 7500,
  "validTo": "2026-04-30T23:59:59Z",
  "usageLimitGlobal": 8000
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "promoCodeId": "promo-bidzaro-2026",
    "code": "BIDZARO2026",
    "value": 15,
    "updatedAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 9.4 Deactivate Promo Code

```http
PATCH /promos/{promoCodeId}/status?status=INACTIVE
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": { "promoCodeId": "promo-bidzaro-2026", "status": "INACTIVE" }
}
```

---

# 10. Support Ticket Management

## 10.1 Get All Support Tickets

```http
GET /support/admin/tickets?page=0&size=20&status=OPEN&priority=HIGH
Authorization: Bearer {accessToken}
```

### Query Parameters
| Param | Values | Description |
|-------|--------|-------------|
| `status` | `OPEN`, `ASSIGNED`, `IN_PROGRESS`, `RESOLVED`, `CLOSED` | Ticket status |
| `priority` | `LOW`, `MEDIUM`, `HIGH`, `URGENT` | Priority level |
| `category` | `ORDER`, `PAYMENT`, `VENDOR`, `ACCOUNT`, `OTHER` | Ticket category |
| `assignedTo` | string | Filter by agent ID |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "ticketId": "tkt-99001-22334",
      "ticketNumber": "TKT-20260224123456",
      "status": "OPEN",
      "priority": "HIGH",
      "category": "ORDER",
      "subject": "Food arrived 2 hours late for wedding",
      "createdBy": "550e8400-e29b-41d4-a716-446655440000",
      "createdByName": "John Doe",
      "createdByEmail": "john.doe@gmail.com",
      "assignedTo": "agent-priya-001",
      "assignedToName": "Priya Sharma",
      "sla": {
        "responseDeadline": "2026-02-24T11:30:00.000Z",
        "resolutionDeadline": "2026-02-25T10:30:00.000Z",
        "status": "ON_TRACK"
      },
      "createdAt": "2026-02-24T10:30:45.123Z"
    }
  ],
  "pageInfo": { "totalElements": 450, "totalPages": 23 }
}
```

---

## 10.2 Reassign Ticket to Agent

```http
PATCH /support/admin/tickets/{ticketId}/assign
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "agentId": "agent-rahul-002",
  "reason": "Priya is on leave. Reassigning to Rahul."
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "ticketId": "tkt-99001-22334",
    "assignedTo": "agent-rahul-002",
    "assignedToName": "Rahul Singh",
    "status": "ASSIGNED",
    "updatedAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 10.3 Resolve Ticket

```http
POST /support/admin/tickets/{ticketId}/resolve
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "resolution": "Investigated the delay. Vendor confirmed traffic delay. Issued 10% discount coupon to customer as compensation.",
  "compensationType": "PROMO_CODE",
  "promoCode": "SORRY10OFF"
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "ticketId": "tkt-99001-22334",
    "status": "RESOLVED",
    "resolution": "Investigated the delay...",
    "resolvedBy": "agent-priya-001",
    "resolvedAt": "2026-02-24T15:00:00.000Z"
  }
}
```

---

## 10.4 Get Ticket Statistics

```http
GET /support/admin/stats
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "totalTickets": 2450,
    "openTickets": 45,
    "assignedTickets": 120,
    "inProgressTickets": 85,
    "resolvedTickets": 2180,
    "closedTickets": 20,
    "breachedSLA": 12,
    "averageResolutionTimeMinutes": 280,
    "ticketsByPriority": {
      "URGENT": 5,
      "HIGH": 40,
      "MEDIUM": 125,
      "LOW": 280
    },
    "ticketsByCategory": {
      "ORDER": 200,
      "PAYMENT": 100,
      "VENDOR": 80,
      "ACCOUNT": 50,
      "OTHER": 20
    }
  }
}
```

---

# 11. Analytics & Reports

## 11.1 Get Platform Overview

```http
GET /analytics/platform/overview
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "platformMetrics": {
      "totalUsers": 5420,
      "totalVendors": 342,
      "totalOrders": 12450,
      "totalRevenue": 42500000,
      "platformEarnings": 850000,
      "averageOrderValue": 3413,
      "conversionRate": 22.5,
      "currency": "INR"
    },
    "growthMetrics": {
      "userGrowthPercentage": 15.5,
      "vendorGrowthPercentage": 8.2,
      "orderGrowthPercentage": 18.7,
      "revenueGrowthPercentage": 22.3
    },
    "topVendors": [
      {
        "vendorId": "vendor-12345-67890",
        "businessName": "Spice Garden Catering",
        "totalOrders": 600,
        "totalRevenue": 900000,
        "averageRating": 4.7
      }
    ],
    "ordersByStatus": {
      "CONFIRMED": 145,
      "IN_PREPARATION": 67,
      "READY_FOR_DELIVERY": 23,
      "DELIVERED": 12105,
      "COMPLETED": 12000,
      "CANCELLED": 200
    },
    "revenueChart": [
      { "date": "2026-02-24", "revenue": 450000, "orders": 32 },
      { "date": "2026-02-23", "revenue": 380000, "orders": 28 }
    ]
  }
}
```

---

## 11.2 Get Revenue Analytics

```http
GET /analytics/revenue?period=monthly
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "period": "monthly",
    "totalRevenue": 12500000,
    "platformFees": 250000,
    "vendorPayouts": 12250000,
    "refunds": 125000,
    "pendingPayments": 1200000,
    "currency": "INR",
    "revenueByGateway": {
      "RAZORPAY": 10000000,
      "STRIPE": 2500000
    }
  }
}
```

---

## 11.3 Get User Analytics

```http
GET /analytics/users
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "totalUsers": 5420,
    "activeUsers": 4856,
    "newUsersToday": 42,
    "newUsersThisWeek": 287,
    "newUsersThisMonth": 1250,
    "usersByCountry": {
      "INDIA": 4500,
      "USA": 920
    },
    "usersByType": {
      "USER": 4800,
      "VENDOR": 342,
      "ADMIN": 5,
      "SUPPORT_AGENT": 12,
      "SUPPORT_MANAGER": 3
    }
  }
}
```

---

## 11.4 Generate Report

```http
GET /analytics/reports?reportType=REVENUE&startDate=2026-01-01&endDate=2026-02-24
Authorization: Bearer {accessToken}
```

### Query Parameters
| Param | Values | Description |
|-------|--------|-------------|
| `reportType` | `REVENUE`, `ORDERS`, `USERS`, `VENDORS`, `BIDS` | Report type |
| `startDate` | YYYY-MM-DD | Start date |
| `endDate` | YYYY-MM-DD | End date |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "reportType": "REVENUE",
    "period": "2026-01-01 to 2026-02-24",
    "summary": {
      "totalRevenue": 24500000,
      "totalOrders": 6200,
      "platformFees": 490000,
      "averageOrderValue": 3952
    },
    "data": [
      { "month": "January 2026", "revenue": 12000000, "orders": 3000 },
      { "month": "February 2026", "revenue": 12500000, "orders": 3200 }
    ]
  }
}
```

---

# 12. Platform Configuration

## 12.1 Get Platform Config by Country

```http
GET /admin/config/{country}
Authorization: Bearer {accessToken}
```

| `country` values | Description |
|-----------------|-------------|
| `INDIA` | Indian market config (INR) |
| `USA` | US market config (USD) |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "configId": "config-india-001",
    "country": "INDIA",
    "biddingConfig": {
      "competitivePeriodHours": 72,
      "coolingPeriodHours": 24,
      "paymentCoolingPeriodHours": 2,
      "bidExpiryHours": 168,
      "maxBidRevisions": 5,
      "maxTargetedVendors": 10
    },
    "paymentConfig": {
      "tokenPercentage": 25,
      "platformFeePercentage": 2,
      "paymentGatewayFeePercentage": 2,
      "currency": "INR",
      "defaultGateway": "RAZORPAY"
    },
    "commissionConfig": {
      "vendorCommissionPercentage": 10,
      "referralCommissionPercentage": 2
    },
    "loyaltyConfig": {
      "pointsPerRupee": 1,
      "rupeesPerPoint": 0.25,
      "maxRedeemPercentage": 50
    },
    "cancellationConfig": {
      "moreThan30Days": 100,
      "between15And30Days": 75,
      "between7And15Days": 50,
      "between3And7Days": 25,
      "lessThan3Days": 0
    },
    "updatedAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 12.2 Update Platform Config

```http
PUT /admin/config/{country}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "biddingConfig": {
    "competitivePeriodHours": 48,
    "coolingPeriodHours": 12,
    "maxBidRevisions": 3
  },
  "paymentConfig": {
    "tokenPercentage": 30,
    "platformFeePercentage": 2.5
  },
  "loyaltyConfig": {
    "pointsPerRupee": 2,
    "maxRedeemPercentage": 40
  }
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Platform configuration updated",
  "data": {
    "country": "INDIA",
    "biddingConfig": {
      "competitivePeriodHours": 48,
      "coolingPeriodHours": 12,
      "maxBidRevisions": 3
    },
    "updatedBy": "admin-user-001",
    "updatedAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

# 13. Announcements

## 13.1 Create Announcement

**Use:** Broadcast announcements to users, vendors, or all.

```http
POST /admin/announcements
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "title": "Platform Maintenance Scheduled",
  "content": "Bidzaro will undergo scheduled maintenance on March 1, 2026 from 2:00 AM to 4:00 AM IST. Services will be temporarily unavailable during this window.",
  "priority": "HIGH",
  "targetAudience": "ALL",
  "publishedAt": "2026-02-24T00:00:00Z",
  "expiresAt": "2026-03-02T00:00:00Z",
  "sendPushNotification": true,
  "sendEmail": false
}
```

### Field Rules
| Field | Required | Values |
|-------|----------|--------|
| `priority` | ✅ | `LOW`, `MEDIUM`, `HIGH`, `URGENT` |
| `targetAudience` | ✅ | `ALL`, `USERS`, `VENDORS`, `SUPPORT` |

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "announcementId": "ann-001",
    "title": "Platform Maintenance Scheduled",
    "priority": "HIGH",
    "targetAudience": "ALL",
    "publishedAt": "2026-02-24T00:00:00Z",
    "expiresAt": "2026-03-02T00:00:00Z",
    "createdAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 13.2 Get All Announcements

```http
GET /admin/announcements?page=0&size=20&active=true
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "announcementId": "ann-001",
      "title": "Platform Maintenance Scheduled",
      "priority": "HIGH",
      "targetAudience": "ALL",
      "publishedAt": "2026-02-24T00:00:00Z",
      "expiresAt": "2026-03-02T00:00:00Z",
      "isActive": true
    }
  ]
}
```

---

## 13.3 Delete Announcement

```http
DELETE /admin/announcements/{announcementId}
Authorization: Bearer {accessToken}
```

### Success Response `204 No Content`
```
HTTP 204 No Content
```

---

# 14. Audit Logs

## 14.1 Get All Audit Logs

**Use:** Track every admin action for compliance and security.

```http
GET /admin/audit-logs?page=0&size=20&entityType=VENDOR&action=STATUS_CHANGE
Authorization: Bearer {accessToken}
```

### Query Parameters
| Param | Values | Description |
|-------|--------|-------------|
| `entityType` | `USER`, `VENDOR`, `ORDER`, `TRANSACTION`, `CONFIG` | Entity type |
| `action` | `STATUS_CHANGE`, `APPROVAL`, `REJECTION`, `REFUND`, `UPDATE` | Action type |
| `performedBy` | string | Admin user ID |
| `startDate` | YYYY-MM-DD | Date range start |
| `endDate` | YYYY-MM-DD | Date range end |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "auditLogId": "audit-001",
      "entityType": "VENDOR",
      "entityId": "vendor-12345-67890",
      "action": "APPROVAL",
      "performedBy": "admin-user-001",
      "performedByName": "Platform Admin",
      "performedByRole": "ADMIN",
      "changes": {
        "old_status": "PENDING",
        "new_status": "APPROVED"
      },
      "ipAddress": "192.168.1.100",
      "timestamp": "2026-02-24T10:30:45.123Z"
    },
    {
      "auditLogId": "audit-002",
      "entityType": "USER",
      "entityId": "550e8400-e29b-41d4-a716-446655440000",
      "action": "STATUS_CHANGE",
      "performedBy": "admin-user-001",
      "changes": {
        "old_status": "ACTIVE",
        "new_status": "SUSPENDED",
        "reason": "Violation of platform terms"
      },
      "timestamp": "2026-02-24T09:15:00.000Z"
    }
  ],
  "pageInfo": { "totalElements": 8500, "totalPages": 425 }
}
```

---

# 📌 Admin API — Complete Error Code Reference

| Error Code | HTTP Status | Description |
|------------|-------------|-------------|
| `ADMIN_AUTH_REQUIRED` | 403 | Admin role required |
| `USER_NOT_FOUND` | 404 | User doesn't exist |
| `VENDOR_NOT_FOUND` | 404 | Vendor doesn't exist |
| `ORDER_NOT_FOUND` | 404 | Order doesn't exist |
| `TRANSACTION_NOT_FOUND` | 404 | Transaction doesn't exist |
| `TICKET_NOT_FOUND` | 404 | Ticket doesn't exist |
| `CONFIG_NOT_FOUND` | 404 | Config doesn't exist for country |
| `INVALID_STATUS` | 400 | Invalid status value |
| `REFUND_FAILED` | 400 | Refund processing failed |
| `ANNOUNCEMENT_NOT_FOUND` | 404 | Announcement doesn't exist |
| `VALIDATION_ERROR` | 422 | Field validation failed |
| `RATE_LIMIT_EXCEEDED` | 429 | Too many requests |

---

# 📌 Admin Quick Reference — Key Workflows

### Vendor Onboarding Workflow
```
1. Vendor submits registration (POST /vendors)
2. Admin gets notification: "New Vendor Pending"
3. Admin reviews: GET /vendors/admin/pending
4. Admin checks documents and approves: POST /vendors/{id}/approve
   OR rejects with reason: POST /vendors/{id}/reject?reason=...
5. Vendor gets email notification with result
```

### User Issue Workflow
```
1. Support ticket raised by user
2. Admin reviews ticket: GET /support/admin/tickets
3. Admin reassigns if needed: PATCH /support/admin/tickets/{id}/assign
4. Admin or agent resolves: POST /support/admin/tickets/{id}/resolve
5. Customer gets resolution email
```

### Refund Workflow
```
1. Admin/agent reviews transaction: GET /admin/transactions/{id}
2. Admin initiates refund: POST /admin/transactions/{id}/refund
3. Refund processed via Razorpay/Stripe (3-5 business days)
4. Transaction status updated to REFUNDED
```

---

# 📌 Important Notes for Admins

1. **Role Enforcement:** All admin endpoints are protected by `@PreAuthorize("hasRole('ADMIN')")`.
2. **Audit Trail:** Every admin action is logged automatically in AuditLogs.
3. **Vendor Approval:** Approve/reject FSSAI, GST, and other docs before approval.
4. **Platform Config:** Changes to biddingConfig affect all future bids immediately.
5. **Promo Codes:** Expired promos auto-deactivate via scheduler every hour.
6. **SLA Monitoring:** SLAMonitorScheduler runs every 30 min to escalate breached tickets.
7. **Token Expiry:** Admin tokens expire in 7 days. Use refresh token to renew.
8. **Rate Limiting:** 100 requests/min per IP. Admin panel may need higher limits.
9. **Revenue Calculation:** Revenue stats are estimated; actual payout reconciliation needed.
10. **Country Config:** INDIA uses Razorpay + INR; USA uses Stripe + USD. Set per-country.

---

*Admin API Documentation — Bidzaro Catering Platform v1.0.0 | Updated: February 24, 2026*

