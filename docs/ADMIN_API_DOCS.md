# 🛡️ ADMIN API Documentation
**Bidzaro Catering Platform** | Base URL: `http://localhost:8080/api/v1`

> 🔒 = Requires `Authorization: Bearer <accessToken>` with `userType: ADMIN`
> ✅ = Required field | ⬜ = Optional field

---

## 📋 Table of Contents
1. [Enums Reference](#-enums-reference)
2. [Authentication](#-authentication)
3. [Dashboard & Analytics](#-dashboard--analytics)
4. [User Management](#-user-management)
5. [Vendor Management](#-vendor-management)
6. [Support Agent Management](#-support-agent-management)
7. [Menu Management](#-menu-management)
8. [Promo Codes](#-promo-codes)
9. [Announcements](#-announcements)
10. [Platform Configuration](#-platform-configuration)
11. [Audit Logs](#-audit-logs)
12. [Orders](#-orders-overview)
13. [Payments & Transactions](#-payments--transactions)

---

## 🔢 Enums Reference

### UserType
| Value |
|-------|
| `USER` |
| `VENDOR` |
| `ADMIN` |
| `SUPPORT_AGENT` |

### UserStatus
| Value | Description |
|-------|-------------|
| `PENDING_VERIFICATION` | Not yet verified |
| `ACTIVE` | Active account |
| `SUSPENDED` | Suspended by admin |
| `DELETED` | Soft deleted |

### Gender
| Value |
|-------|
| `MALE` |
| `FEMALE` |
| `OTHER` |
| `PREFER_NOT_TO_SAY` |

### VendorStatus
| Value |
|-------|
| `PENDING_APPROVAL` |
| `ACTIVE` |
| `SUSPENDED` |
| `REJECTED` |
| `DELETED` |

### ApprovalStatus
| Value |
|-------|
| `PENDING` |
| `APPROVED` |
| `REJECTED` |
| `UNDER_REVIEW` |

### BusinessType
| Value |
|-------|
| `CATERING` |
| `RESTAURANT` |
| `CLOUD_KITCHEN` |
| `HOME_CHEF` |
| `BAKERY` |

### DocumentVerificationStatus
| Value |
|-------|
| `PENDING` |
| `VERIFIED` |
| `REJECTED` |

### OrderStatus
| Value |
|-------|
| `PENDING_TOKEN_PAYMENT` |
| `CONFIRMED` |
| `IN_PREPARATION` |
| `READY_FOR_DELIVERY` |
| `DELIVERING` |
| `DELIVERED` |
| `COMPLETED` |
| `CANCELLED` |

### TransactionStatus
| Value |
|-------|
| `PENDING` |
| `PROCESSING` |
| `SUCCESS` |
| `FAILED` |
| `REFUNDED` |
| `PARTIALLY_REFUNDED` |

### PaymentGateway
| Value |
|-------|
| `RAZORPAY` |
| `STRIPE` |
| `PAYPAL` |

### PaymentType
| Value |
|-------|
| `TOKEN` |
| `INSTALLMENT` |
| `FINAL` |
| `REFUND` |
| `BALANCE` |
| `FULL` |

### TicketStatus
| Value |
|-------|
| `OPEN` |
| `ASSIGNED` |
| `IN_PROGRESS` |
| `WAITING_FOR_CUSTOMER` |
| `RESOLVED` |
| `CLOSED` |
| `ESCALATED` |

### TicketPriority
| Value | First Response | Resolution |
|-------|---------------|------------|
| `LOW` | 24h | 72h |
| `MEDIUM` | 8h | 48h |
| `HIGH` | 4h | 24h |
| `URGENT` | 1h | 4h |

### PromoType
| Value |
|-------|
| `PERCENTAGE` |
| `FLAT` |

### PromoStatus
| Value |
|-------|
| `ACTIVE` |
| `INACTIVE` |
| `EXPIRED` |

### PromoApplicableTo
| Value |
|-------|
| `ALL` |
| `SPECIFIC_VENDORS` |
| `SPECIFIC_USERS` |
| `SPECIFIC_CUISINES` |

### Announcement TargetAudience
| Value |
|-------|
| `ALL` |
| `USERS` |
| `VENDORS` |
| `SUPPORT_AGENTS` |
| `ADMINS` |

### Announcement Priority
| Value |
|-------|
| `LOW` |
| `MEDIUM` |
| `HIGH` |
| `URGENT` |

### FoodType (Menu)
| Value |
|-------|
| `VEG` |
| `NON_VEG` |
| `VEGAN` |
| `EGG` |

### SpiceLevel (Menu)
| Value |
|-------|
| `MILD` |
| `MEDIUM` |
| `HOT` |
| `EXTRA_HOT` |

### ItemStatus (Menu)
| Value |
|-------|
| `ACTIVE` |
| `INACTIVE` |

### ReviewStatus
| Value |
|-------|
| `PENDING` |
| `APPROVED` |
| `REJECTED` |
| `HIDDEN` |

### ConversationType
| Value |
|-------|
| `USER_VENDOR` |
| `USER_SUPPORT` |
| `VENDOR_SUPPORT` |

---

## 🔐 Authentication

`POST /auth/login`

**Request:**
```json
{
  "identifier": "admin@bidzaro.com",
  "password": "AdminPass@123",
  "fcmToken": "firebase-device-token"
}
```

**Response `200`:**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 604800,
    "user": {
      "userId": "uuid",
      "vendorId": null,
      "email": "admin@bidzaro.com",
      "phone": "+911234567890",
      "userType": "ADMIN",
      "firstName": "Admin",
      "lastName": "Bidzaro",
      "fullName": "Admin Bidzaro",
      "profilePictureUrl": null,
      "emailVerified": true,
      "phoneVerified": true,
      "twoFactorEnabled": false,
      "preferredLanguage": "en",
      "preferredCurrency": "INR",
      "country": "INDIA",
      "status": "ACTIVE",
      "notificationPreferences": {
        "emailNotifications": { "orderUpdates": true, "bidUpdates": true, "securityAlerts": true },
        "pushNotifications": { "orderUpdates": true, "chatMessages": true }
      },
      "lastLoginAt": "2026-03-06T10:00:00Z",
      "createdAt": "2026-01-01T00:00:00Z"
    }
  }
}
```

---

## 📊 Dashboard & Analytics

### Get Platform Analytics Overview
`GET /admin/analytics/overview` 🔒

**Response `200`:**
```json
{
  "data": {
    "platformMetrics": {
      "totalUsers": 4520,
      "totalVendors": 312,
      "totalOrders": 8450,
      "totalRevenue": 45000000.00,
      "platformEarnings": 900000.00,
      "averageOrderValue": 5325.00,
      "conversionRate": 0
    },
    "growthMetrics": {
      "userGrowthPercentage": 12.50,
      "vendorGrowthPercentage": 8.30,
      "orderGrowthPercentage": 15.20,
      "revenueGrowthPercentage": 18.45
    },
    "topVendors": [
      {
        "vendorId": "uuid",
        "vendorName": "Royal Catering Co.",
        "city": "Hyderabad",
        "totalOrders": 245,
        "revenue": 1500000.00,
        "averageRating": 4.80
      }
    ],
    "revenueChart": [
      { "date": "2026-03-01", "revenue": 150000.00, "orders": 28 }
    ]
  }
}
```

---

### Get Vendor Analytics
`GET /analytics/vendor/{vendorId}` 🔒

**Response `200`:** Full vendor dashboard data (same as vendor's own dashboard).

---

## 👥 User Management

### Get All Users
`GET /admin/users?page=0&size=20&userType=USER&status=ACTIVE`

| Query Param | Type | Required | Values |
|-------------|------|----------|--------|
| `page` | int | ⬜ | Default 0 |
| `size` | int | ⬜ | Default 20 |
| `userType` | string | ⬜ | `USER`, `VENDOR`, `ADMIN`, `SUPPORT_AGENT` |
| `status` | string | ⬜ | `PENDING_VERIFICATION`, `ACTIVE`, `SUSPENDED`, `DELETED` |

**Response `200`:**
```json
{
  "data": [
    {
      "userId": "uuid",
      "vendorId": null,
      "email": "rahul@example.com",
      "phone": "+919876543210",
      "userType": "USER",
      "firstName": "Rahul",
      "lastName": "Sharma",
      "fullName": "Rahul Sharma",
      "profilePictureUrl": null,
      "dateOfBirth": "1995-06-15",
      "gender": "MALE",
      "emailVerified": true,
      "phoneVerified": true,
      "twoFactorEnabled": false,
      "preferredLanguage": "en",
      "preferredCurrency": "INR",
      "country": "INDIA",
      "status": "ACTIVE",
      "notificationPreferences": null,
      "lastLoginAt": "2026-03-05T10:00:00Z",
      "createdAt": "2026-01-15T10:00:00Z"
    }
  ],
  "pageInfo": { "page": 0, "size": 20, "totalElements": 4520 }
}
```

---

### Get User by ID
`GET /admin/users/{userId}` 🔒
**Response `200`:** Full `UserResponse` object.

---

### Update User Status
`PUT /admin/users/{userId}/status` 🔒

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `status` | string | ✅ | `ACTIVE`, `SUSPENDED`, `DELETED` |
| `reason` | string | ⬜ | Reason for status change |

**Request:**
```json
{
  "status": "SUSPENDED",
  "reason": "Violated platform terms of service - fraudulent activity detected"
}
```

**Response `200`:** Updated `UserResponse` object.

---

### Delete User (Soft Delete)
`DELETE /admin/users/{userId}` 🔒
**Response `200`:** `{ "success": true, "message": "User deleted successfully" }`

---

### Search Users
`GET /admin/users/search?query=rahul&page=0&size=20` 🔒
**Response `200`:** Paginated `UserResponse` list.

---

## 🏪 Vendor Management

### Get All Vendors
`GET /admin/vendors?page=0&size=20&status=PENDING_APPROVAL`

| Query Param | Type | Required | Values |
|-------------|------|----------|--------|
| `page` | int | ⬜ | Default 0 |
| `size` | int | ⬜ | Default 20 |
| `status` | string | ⬜ | `PENDING_APPROVAL`, `ACTIVE`, `SUSPENDED`, `REJECTED` |
| `approvalStatus` | string | ⬜ | `PENDING`, `APPROVED`, `REJECTED`, `UNDER_REVIEW` |
| `city` | string | ⬜ | Filter by city |

**Response `200`:**
```json
{
  "data": [
    {
      "vendorId": "uuid",
      "userId": "uuid",
      "registeredEmail": "ravi@royalcatering.com",
      "registeredPhone": "+919876543210",
      "businessName": "Royal Catering Co.",
      "businessEmail": "info@royalcatering.com",
      "businessPhone": "+919876543211",
      "businessEmailVerified": false,
      "businessPhoneVerified": false,
      "businessType": "CATERING",
      "businessRegistrationNumber": "REG123456",
      "taxId": "36AABCR1234F1ZV",
      "logoUrl": null,
      "bannerUrl": null,
      "description": "Premium catering for all occasions",
      "establishedYear": 2015,
      "cuisinesOffered": ["North Indian", "Mughlai"],
      "specialties": ["Biryani"],
      "businessAddress": {
        "streetAddress": "45, Banjara Hills",
        "city": "Hyderabad",
        "state": "Telangana",
        "postalCode": "500034",
        "country": null
      },
      "ownerInfo": {
        "firstName": "Ravi",
        "lastName": "Kumar",
        "phone": "+919876543210",
        "email": "ravi@royalcatering.com",
        "idProofType": "AADHAAR",
        "idProofNumber": "1234-5678-9012"
      },
      "serviceAreas": [{ "city": "Hyderabad", "state": "Telangana", "radiusKm": 50 }],
      "capacity": { "minGuests": 50, "maxGuests": 2000, "concurrentEvents": 3 },
      "pricing": { "currency": "INR", "startingPricePerPlate": 250.00, "averagePricePerPlate": 450.00 },
      "ratings": { "averageRating": 0.00, "totalReviews": 0 },
      "stats": { "totalOrders": 0, "completedOrders": 0, "ordersCount": 0 },
      "status": "PENDING_APPROVAL",
      "approvalStatus": "PENDING",
      "verified": false,
      "featured": false,
      "country": "INDIA",
      "documents": [
        {
          "documentId": "uuid",
          "documentType": "FSSAI_LICENSE",
          "documentName": "FSSAI License 2026",
          "documentUrl": "http://localhost:8080/uploads/documents/fssai.pdf",
          "documentNumber": "FSSAI123456",
          "issueDate": "2025-01-01T00:00:00Z",
          "expiryDate": "2027-01-01T00:00:00Z",
          "verificationStatus": "PENDING",
          "uploadedAt": "2026-03-06T10:00:00Z"
        }
      ],
      "createdAt": "2026-03-06T10:00:00Z"
    }
  ],
  "pageInfo": { "page": 0, "size": 20, "totalElements": 45 }
}
```

---

### Get Vendor by ID
`GET /admin/vendors/{vendorId}` 🔒
**Response `200`:** Full `VendorResponse` object.

---

### Approve Vendor
`PUT /admin/vendors/{vendorId}/approve` 🔒

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `notes` | string | ⬜ | Optional approval notes |

**Request:**
```json
{
  "notes": "All documents verified. FSSAI license valid until 2027."
}
```

**Response `200`:**
```json
{
  "data": {
    "vendorId": "uuid",
    "status": "ACTIVE",
    "approvalStatus": "APPROVED",
    "approvalDate": "2026-03-06T10:00:00Z"
  }
}
```

---

### Reject Vendor
`PUT /admin/vendors/{vendorId}/reject` 🔒

| Field | Type | Required |
|-------|------|----------|
| `reason` | string | ✅ |

**Request:**
```json
{
  "reason": "FSSAI license expired. Please renew and reapply."
}
```

**Response `200`:**
```json
{
  "data": {
    "vendorId": "uuid",
    "status": "REJECTED",
    "approvalStatus": "REJECTED",
    "rejectionReason": "FSSAI license expired. Please renew and reapply."
  }
}
```

---

### Suspend Vendor
`PUT /admin/vendors/{vendorId}/suspend` 🔒

| Field | Type | Required |
|-------|------|----------|
| `reason` | string | ✅ |

**Request:**
```json
{ "reason": "Multiple customer complaints about food quality" }
```

**Response `200`:** Updated `VendorResponse` with `status: SUSPENDED`.

---

### Reactivate Vendor
`PUT /admin/vendors/{vendorId}/reactivate` 🔒
**Response `200`:** Updated `VendorResponse` with `status: ACTIVE`.

---

### Verify Vendor Document
`PUT /admin/vendors/{vendorId}/documents/{documentId}/verify` 🔒

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `status` | string | ✅ | `VERIFIED` or `REJECTED` |
| `notes` | string | ⬜ | Verification notes |

**Request:**
```json
{
  "status": "VERIFIED",
  "notes": "FSSAI license verified. Valid until January 2027."
}
```

**Response `200`:** Updated document with `verificationStatus: VERIFIED`.

---

### Toggle Featured Vendor
`PUT /admin/vendors/{vendorId}/featured` 🔒

**Request:** `{ "featured": true }`
**Response `200`:** Updated `VendorResponse` with `featured: true`.

---

## 🎧 Support Agent Management

### Create Support Agent
`POST /admin/support-agents` 🔒

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `email` | string | ✅ | Valid email |
| `phone` | string | ✅ | E.164 format |
| `password` | string | ✅ | Min 8 chars with complexity |
| `firstName` | string | ✅ | 1–50 chars |
| `lastName` | string | ⬜ | Max 50 chars |
| `country` | string | ⬜ | `INDIA` or `USA` |

**Request:**
```json
{
  "email": "priya.support@bidzaro.com",
  "phone": "+919999988888",
  "password": "AgentPass@123",
  "firstName": "Priya",
  "lastName": "Reddy",
  "country": "INDIA"
}
```

**Response `201`:** Full `UserResponse` object with `userType: SUPPORT_AGENT`:
```json
{
  "data": {
    "userId": "uuid",
    "email": "priya.support@bidzaro.com",
    "phone": "+919999988888",
    "userType": "SUPPORT_AGENT",
    "firstName": "Priya",
    "lastName": "Reddy",
    "fullName": "Priya Reddy",
    "emailVerified": false,
    "phoneVerified": false,
    "status": "ACTIVE",
    "createdAt": "2026-03-06T10:00:00Z"
  }
}
```

---

### Get All Support Agents
`GET /admin/support-agents?page=0&size=20` 🔒

**Response `200`:** Paginated list of `UserResponse` with `userType: SUPPORT_AGENT`.

---

### Get Support Agent Workload
`GET /admin/support-agents/{agentId}/workload` 🔒

**Response `200`:**
```json
{
  "data": {
    "agentId": "uuid",
    "agentName": "Priya Reddy",
    "activeTickets": 5,
    "resolvedToday": 3,
    "averageResolutionTimeHours": 6.5,
    "csatScore": 4.8
  }
}
```

---

### Assign Ticket to Agent
`PUT /admin/support/tickets/{ticketId}/assign` 🔒

| Field | Type | Required |
|-------|------|----------|
| `agentId` | string | ✅ |

**Request:** `{ "agentId": "agent-uuid" }`
**Response `200`:** Updated `TicketResponse` with `assignedTo` and `status: ASSIGNED`.

---

## 🍽️ Menu Management

### Get All Categories
`GET /admin/menu/categories?page=0&size=20` 🔒

**Response `200`:**
```json
{
  "data": [
    {
      "categoryId": "uuid",
      "categoryName": "Main Course",
      "categoryNameHindi": "मुख्य व्यंजन",
      "description": "Primary dishes served during meal",
      "iconUrl": "http://localhost:8080/uploads/images/maincourse.png",
      "displayOrder": 1,
      "status": "ACTIVE",
      "createdAt": "2026-01-01T00:00:00Z"
    }
  ]
}
```

---

### Create Category
`POST /admin/menu/categories` 🔒

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `categoryName` | string | ✅ | 2–100 chars |
| `categoryNameHindi` | string | ⬜ | |
| `description` | string | ⬜ | |
| `iconUrl` | string | ⬜ | Uploaded icon URL |
| `displayOrder` | integer | ⬜ | Sort order |
| `status` | string | ⬜ | `ACTIVE`, `INACTIVE` |

**Request:**
```json
{
  "categoryName": "Starters",
  "categoryNameHindi": "स्टार्टर",
  "description": "Appetizers and starters",
  "iconUrl": "http://localhost:8080/uploads/images/starters.png",
  "displayOrder": 2,
  "status": "ACTIVE"
}
```

**Response `201`:** Full `CategoryResponse` object.

---

### Update Category
`PUT /admin/menu/categories/{categoryId}` 🔒
Same body as create, all fields optional.

---

### Delete Category
`DELETE /admin/menu/categories/{categoryId}` 🔒
**Response `200`:** `{ "success": true, "message": "Category deleted" }`

---

### Create Master Menu Item
`POST /admin/menu/items` 🔒

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `itemName` | string | ✅ | 2–100 chars |
| `itemNameHindi` | string | ⬜ | Hindi name |
| `description` | string | ⬜ | |
| `categoryId` | string | ✅ | Must exist |
| `cuisineType` | string | ⬜ | e.g. `North Indian`, `Chinese` |
| `foodType` | string | ✅ | `VEG`, `NON_VEG`, `VEGAN`, `EGG` |
| `spiceLevel` | string | ⬜ | `MILD`, `MEDIUM`, `HOT`, `EXTRA_HOT` |
| `dietaryTags` | array | ⬜ | e.g. `["Gluten-Free", "Dairy-Free"]` |
| `allergens` | array | ⬜ | e.g. `["Dairy", "Nuts"]` |
| `nutritionalInfo` | object | ⬜ | |
| `nutritionalInfo.calories` | integer | ⬜ | |
| `nutritionalInfo.proteinGrams` | integer | ⬜ | |
| `nutritionalInfo.carbsGrams` | integer | ⬜ | |
| `nutritionalInfo.fatGrams` | integer | ⬜ | |
| `nutritionalInfo.servingSizeGrams` | integer | ⬜ | |
| `imageUrls` | array | ⬜ | List of image URLs |
| `isPopular` | boolean | ⬜ | |
| `status` | string | ⬜ | `ACTIVE`, `INACTIVE` |

**Request:**
```json
{
  "itemName": "Chicken Biryani",
  "itemNameHindi": "चिकन बिरयानी",
  "description": "Aromatic basmati rice with tender chicken",
  "categoryId": "uuid",
  "cuisineType": "North Indian",
  "foodType": "NON_VEG",
  "spiceLevel": "MEDIUM",
  "dietaryTags": ["Gluten-Free"],
  "allergens": ["Dairy"],
  "nutritionalInfo": {
    "calories": 450,
    "proteinGrams": 28,
    "carbsGrams": 55,
    "fatGrams": 12,
    "servingSizeGrams": 350
  },
  "imageUrls": ["http://localhost:8080/uploads/images/biryani.jpg"],
  "isPopular": true,
  "status": "ACTIVE"
}
```

**Response `201`:** Full `MenuItemResponse`:
```json
{
  "data": {
    "masterItemId": "uuid",
    "itemName": "Chicken Biryani",
    "itemNameHindi": "चिकन बिरयानी",
    "description": "Aromatic basmati rice with tender chicken",
    "categoryId": "uuid",
    "categoryName": "Main Course",
    "cuisineType": "North Indian",
    "foodType": "NON_VEG",
    "spiceLevel": "MEDIUM",
    "dietaryTags": ["Gluten-Free"],
    "allergens": ["Dairy"],
    "nutritionalInfo": {
      "calories": 450,
      "proteinGrams": 28,
      "carbsGrams": 55,
      "fatGrams": 12,
      "servingSizeGrams": 350
    },
    "imageUrls": ["http://localhost:8080/uploads/images/biryani.jpg"],
    "isPopular": true,
    "status": "ACTIVE",
    "createdAt": "2026-03-06T10:00:00Z",
    "updatedAt": "2026-03-06T10:00:00Z"
  }
}
```

---

### Update Master Menu Item
`PUT /admin/menu/items/{masterItemId}` 🔒
Same body as create, all fields optional.
**Response `200`:** Updated `MenuItemResponse`.

---

### Delete Master Menu Item
`DELETE /admin/menu/items/{masterItemId}` 🔒
**Response `200`:** `{ "success": true, "message": "Menu item deleted" }`

---

### Moderate Review
`PUT /admin/reviews/{reviewId}/moderate` 🔒

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `status` | string | ✅ | `APPROVED`, `REJECTED`, `HIDDEN` |
| `notes` | string | ⬜ | Moderation notes |

**Request:**
```json
{
  "status": "REJECTED",
  "notes": "Review contains inappropriate language"
}
```
**Response `200`:** Updated `ReviewResponse` with new status.

---

## 🏷️ Promo Codes

### Get All Promo Codes
`GET /admin/promos?page=0&size=20&status=ACTIVE` 🔒

**Response `200`:**
```json
{
  "data": [
    {
      "promoCodeId": "uuid",
      "code": "WELCOME50",
      "title": "Welcome Discount",
      "description": "₹500 off on your first order",
      "type": "FLAT",
      "value": 500.00,
      "maxDiscountAmount": 500.00,
      "minOrderAmount": 5000.00,
      "validFrom": "2026-01-01T00:00:00Z",
      "validTo": "2026-12-31T23:59:59Z",
      "usageLimitGlobal": 1000,
      "usageLimitPerUser": 1,
      "usedCount": 345,
      "applicableTo": "ALL",
      "applicableVendorIds": null,
      "applicableCuisines": null,
      "firstOrderOnly": true,
      "status": "ACTIVE",
      "createdAt": "2026-01-01T00:00:00Z"
    }
  ],
  "pageInfo": { "page": 0, "size": 20, "totalElements": 12 }
}
```

---

### Create Promo Code
`POST /admin/promos` 🔒

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `code` | string | ✅ | Unique promo code |
| `title` | string | ⬜ | Display title |
| `description` | string | ⬜ | |
| `type` | string | ✅ | `PERCENTAGE` or `FLAT` |
| `value` | decimal | ✅ | Must be positive |
| `maxDiscountAmount` | decimal | ⬜ | Cap for PERCENTAGE type |
| `minOrderAmount` | decimal | ⬜ | Minimum order to apply |
| `validFrom` | datetime | ⬜ | ISO-8601 |
| `validTo` | datetime | ⬜ | ISO-8601 |
| `usageLimitGlobal` | integer | ⬜ | Max total uses |
| `usageLimitPerUser` | integer | ⬜ | Max uses per user |
| `applicableTo` | string | ⬜ | `ALL`, `SPECIFIC_VENDORS`, `SPECIFIC_USERS`, `SPECIFIC_CUISINES` |
| `applicableVendorIds` | array | ⬜ | For `SPECIFIC_VENDORS` |
| `applicableUserIds` | array | ⬜ | For `SPECIFIC_USERS` |
| `applicableCuisines` | array | ⬜ | For `SPECIFIC_CUISINES` |
| `firstOrderOnly` | boolean | ⬜ | Restrict to first order |

**Request:**
```json
{
  "code": "HOLI2026",
  "title": "Holi Festival Special",
  "description": "Flat 10% off on all orders during Holi",
  "type": "PERCENTAGE",
  "value": 10.00,
  "maxDiscountAmount": 2000.00,
  "minOrderAmount": 10000.00,
  "validFrom": "2026-03-14T00:00:00Z",
  "validTo": "2026-03-20T23:59:59Z",
  "usageLimitGlobal": 500,
  "usageLimitPerUser": 1,
  "applicableTo": "ALL",
  "firstOrderOnly": false
}
```

**Response `201`:** Full `PromoCodeResponse` object:
```json
{
  "data": {
    "promoCodeId": "uuid",
    "code": "HOLI2026",
    "title": "Holi Festival Special",
    "description": "Flat 10% off on all orders during Holi",
    "type": "PERCENTAGE",
    "value": 10.00,
    "maxDiscountAmount": 2000.00,
    "minOrderAmount": 10000.00,
    "validFrom": "2026-03-14T00:00:00Z",
    "validTo": "2026-03-20T23:59:59Z",
    "usageLimitGlobal": 500,
    "usageLimitPerUser": 1,
    "usedCount": 0,
    "applicableTo": "ALL",
    "applicableVendorIds": null,
    "applicableCuisines": null,
    "firstOrderOnly": false,
    "status": "ACTIVE",
    "createdAt": "2026-03-06T10:00:00Z"
  }
}
```

---

### Update Promo Code
`PUT /admin/promos/{promoId}` 🔒
Same body as create, all fields optional.

---

### Deactivate Promo Code
`PUT /admin/promos/{promoId}/deactivate` 🔒
**Response `200`:** Updated `PromoCodeResponse` with `status: INACTIVE`.

---

### Delete Promo Code
`DELETE /admin/promos/{promoId}` 🔒
**Response `200`:** `{ "success": true, "message": "Promo code deleted" }`

---

## 📢 Announcements

### Get All Announcements
`GET /admin/announcements?page=0&size=20` 🔒

**Response `200`:**
```json
{
  "data": [
    {
      "id": "uuid",
      "title": "Platform Maintenance on March 10",
      "content": "The platform will be down for maintenance from 2 AM to 4 AM IST on March 10.",
      "targetAudience": "ALL",
      "priority": "HIGH",
      "isActive": true,
      "startDate": "2026-03-09T00:00:00Z",
      "endDate": "2026-03-10T06:00:00Z",
      "createdBy": "admin-uuid",
      "createdAt": "2026-03-06T10:00:00Z"
    }
  ]
}
```

---

### Create Announcement
`POST /admin/announcements` 🔒

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `title` | string | ✅ | 5–200 chars |
| `content` | string | ✅ | 10–5000 chars |
| `targetAudience` | string | ✅ | `ALL`, `USERS`, `VENDORS`, `SUPPORT_AGENTS`, `ADMINS` |
| `priority` | string | ⬜ | `LOW`, `MEDIUM`, `HIGH`, `URGENT` |
| `isActive` | boolean | ⬜ | Default true |
| `startDate` | datetime | ⬜ | ISO-8601 |
| `endDate` | datetime | ⬜ | ISO-8601 |

**Request:**
```json
{
  "title": "Platform Maintenance on March 10",
  "content": "The platform will be down for scheduled maintenance from 2 AM to 4 AM IST on March 10, 2026.",
  "targetAudience": "ALL",
  "priority": "HIGH",
  "isActive": true,
  "startDate": "2026-03-09T00:00:00Z",
  "endDate": "2026-03-10T06:00:00Z"
}
```

**Response `201`:** Full announcement object (same as list item above).

---

### Update Announcement
`PUT /admin/announcements/{announcementId}` 🔒
Same body as create, all fields optional.

---

### Delete Announcement
`DELETE /admin/announcements/{announcementId}` 🔒
**Response `200`:** `{ "success": true, "message": "Announcement deleted" }`

---

## ⚙️ Platform Configuration

### Get All Config Keys
`GET /admin/config` 🔒

**Response `200`:**
```json
{
  "data": [
    {
      "id": "uuid",
      "configKey": "TOKEN_PAYMENT_PERCENTAGE",
      "configValue": "25",
      "description": "Percentage of total order value charged as token payment",
      "isActive": true,
      "updatedAt": "2026-01-01T10:00:00Z"
    },
    {
      "configKey": "PLATFORM_FEE_PERCENTAGE",
      "configValue": "2",
      "description": "Platform commission percentage on each order"
    },
    {
      "configKey": "BID_EXPIRY_HOURS",
      "configValue": "72",
      "description": "Hours after which a bid request expires"
    },
    {
      "configKey": "MAX_BIDS_PER_REQUEST",
      "configValue": "10",
      "description": "Maximum vendor bids per bid request"
    },
    {
      "configKey": "ORDER_AUTO_COMPLETE_HOURS",
      "configValue": "24",
      "description": "Hours after DELIVERED to auto-complete an order"
    },
    {
      "configKey": "LOYALTY_POINTS_PER_RUPEE",
      "configValue": "1",
      "description": "Loyalty points earned per rupee spent"
    }
  ]
}
```

---

### Get Config by Key
`GET /admin/config/{key}` 🔒

**Response `200`:**
```json
{
  "data": {
    "configKey": "TOKEN_PAYMENT_PERCENTAGE",
    "configValue": "25",
    "description": "Percentage of total order value charged as token payment",
    "isActive": true
  }
}
```

---

### Update Config
`PUT /admin/config/{key}` 🔒

| Field | Type | Required |
|-------|------|----------|
| `value` | string | ✅ |

**Request:** `{ "value": "30" }`
**Response `200`:** Updated config object.

---

## 📜 Audit Logs

### Get Audit Logs
`GET /admin/audit?page=0&size=20&entityType=VENDOR&startDate=2026-03-01T00:00:00Z&endDate=2026-03-31T23:59:59Z` 🔒

| Query Param | Type | Required | Description |
|-------------|------|----------|-------------|
| `page` | int | ⬜ | |
| `size` | int | ⬜ | |
| `entityType` | string | ⬜ | `USER`, `VENDOR`, `ORDER`, `PAYMENT`, `PROMO` |
| `entityId` | string | ⬜ | Specific entity ID |
| `action` | string | ⬜ | e.g. `VENDOR_APPROVED`, `USER_SUSPENDED` |
| `performedBy` | string | ⬜ | Admin user ID |
| `startDate` | datetime | ⬜ | ISO-8601 |
| `endDate` | datetime | ⬜ | ISO-8601 |

**Response `200`:**
```json
{
  "data": [
    {
      "auditId": "uuid",
      "entityType": "VENDOR",
      "entityId": "vendor-uuid",
      "action": "VENDOR_APPROVED",
      "performedBy": "admin-uuid",
      "performedByName": "Admin Bidzaro",
      "details": {
        "vendorName": "Royal Catering Co.",
        "notes": "All documents verified."
      },
      "ipAddress": "192.168.1.1",
      "createdAt": "2026-03-06T10:00:00Z"
    }
  ],
  "pageInfo": { "page": 0, "size": 20, "totalElements": 450 }
}
```

---

## 📦 Orders Overview

### Get All Orders
`GET /admin/orders?page=0&size=20&status=CONFIRMED` 🔒

| Query Param | Type | Required | Values |
|-------------|------|----------|--------|
| `status` | string | ⬜ | Any `OrderStatus` value |
| `vendorId` | string | ⬜ | Filter by vendor |
| `userId` | string | ⬜ | Filter by user |
| `page` | int | ⬜ | |
| `size` | int | ⬜ | |

**Response `200`:** Paginated list of full `OrderResponse` objects.

---

### Get Order by ID
`GET /admin/orders/{orderId}` 🔒
**Response `200`:** Full `OrderResponse` object.

---

### Update Order Status (Admin Override)
`PUT /admin/orders/{orderId}/status` 🔒

**Request:** `{ "status": "CANCELLED", "reason": "Vendor unable to fulfil due to emergency" }`
**Response `200`:** Updated `OrderResponse`.

---

## 💳 Payments & Transactions

### Get All Transactions
`GET /admin/payments?page=0&size=20&status=SUCCESS` 🔒

| Query Param | Type | Required |
|-------------|------|----------|
| `status` | string | ⬜ | Any `TransactionStatus` |
| `gateway` | string | ⬜ | `RAZORPAY`, `STRIPE` |
| `userId` | string | ⬜ | |
| `orderId` | string | ⬜ | |
| `page` | int | ⬜ | |
| `size` | int | ⬜ | |

**Response `200`:**
```json
{
  "data": [
    {
      "transactionId": "uuid",
      "orderId": "uuid",
      "bidId": "uuid",
      "userId": "uuid",
      "vendorId": "uuid",
      "paymentType": "TOKEN",
      "installmentNumber": null,
      "amount": {
        "currency": "INR",
        "amount": 37913.40,
        "platformFee": 758.27,
        "vendorPayout": 37155.13
      },
      "paymentGateway": "RAZORPAY",
      "gatewayTransactionId": "pay_XYZ789GHI012",
      "gatewayOrderId": "order_ABC123DEF456",
      "paymentMethod": "UPI",
      "paymentMethodDetails": {
        "cardLastFour": null,
        "cardBrand": null,
        "cardNetwork": null,
        "upiId": "rahul@upi",
        "bankName": null,
        "walletName": null
      },
      "status": "SUCCESS",
      "failureReason": null,
      "initiatedAt": "2026-03-06T14:55:00Z",
      "processedAt": "2026-03-06T15:00:00Z",
      "settledAt": null,
      "createdAt": "2026-03-06T14:55:00Z"
    }
  ],
  "pageInfo": { "page": 0, "size": 20, "totalElements": 8450 }
}
```

---

### Get Revenue Stats
`GET /admin/payments/stats` 🔒

**Response `200`:**
```json
{
  "data": {
    "totalRevenue": 45000000.00,
    "currentMonthRevenue": 2500000.00,
    "previousMonthRevenue": 2100000.00,
    "revenueGrowth": 19.05,
    "platformFees": 900000.00,
    "vendorPayouts": 44100000.00,
    "pendingPayouts": 150000.00,
    "totalTransactions": 8450,
    "successfulTransactions": 8220,
    "failedTransactions": 180,
    "refundedTransactions": 50
  }
}
```

---

## ⚠️ Standard Error Response

```json
{
  "success": false,
  "error": {
    "code": "FORBIDDEN",
    "message": "Admin access required",
    "path": "/api/v1/admin/users",
    "timestamp": "2026-03-06T10:00:00Z",
    "fieldErrors": {}
  }
}
```

**Admin Error Codes:**
| Code | HTTP | Description |
|------|------|-------------|
| `UNAUTHORIZED` | 401 | Missing or expired token |
| `FORBIDDEN` | 403 | Not an admin |
| `RESOURCE_NOT_FOUND` | 404 | User/vendor/order not found |
| `CONFLICT` | 409 | Promo code already exists |
| `VALIDATION_ERROR` | 400 | Request validation failed |
| `INVALID_STATUS_TRANSITION` | 400 | Cannot change to this status |
