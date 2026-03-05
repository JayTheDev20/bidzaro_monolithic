# 🛡️ ADMIN API DOCUMENTATION
## Bidzaro Catering Platform — Complete Admin Reference (Real DTO-Based)

**Version:** 1.0.0 | **Base URL:** `http://localhost:8080/api/v1`
**Auth:** `Authorization: Bearer {accessToken}` | **Role Required:** `ADMIN`
**Content-Type:** `application/json`

> All field names, types, and response shapes taken directly from actual Java DTO classes.

---

## 🔐 Admin Auth
```
POST /auth/login  →  { identifier: "admin@bidzaro.com", password: "..." }
All admin APIs    →  Authorization: Bearer {accessToken}
Role required     →  ADMIN (403 Forbidden for non-admin tokens)
```

---

# 1. ADMIN DASHBOARD STATS

```http
GET /admin/dashboard
Authorization: Bearer {accessToken}
```

### Success Response `200 OK` — Full `DashboardStatsResponse` DTO
```json
{
  "success": true,
  "data": {
    "userStats": {
      "totalUsers": 12500,
      "activeUsers": 11200,
      "newUsersToday": 45,
      "newUsersThisWeek": 320,
      "newUsersThisMonth": 1250
    },
    "vendorStats": {
      "totalVendors": 850,
      "activeVendors": 780,
      "pendingApproval": 28,
      "verifiedVendors": 712,
      "newVendorsThisMonth": 32
    },
    "orderStats": {
      "totalOrders": 48500,
      "pendingOrders": 125,
      "completedOrders": 47800,
      "cancelledOrders": 575,
      "ordersToday": 28,
      "ordersThisWeek": 196,
      "ordersThisMonth": 840
    },
    "revenueStats": {
      "totalRevenue": 245000000.00,
      "revenueToday": 125000.00,
      "revenueThisWeek": 875000.00,
      "revenueThisMonth": 3800000.00,
      "platformFees": 4900000.00,
      "pendingPayouts": 780000.00
    },
    "bidStats": {
      "totalBidRequests": 52000,
      "activeBidRequests": 250,
      "acceptedBids": 46800,
      "expiredBids": 4950
    }
  }
}
```

---

# 2. GET ALL USERS

```http
GET /admin/users?page=0&size=20&status=ACTIVE&userType=USER
Authorization: Bearer {accessToken}
```

### Query Parameters
| Param | Type | Notes |
|-------|------|-------|
| `page` | int | Page number (0-based) |
| `size` | int | Page size (default 20) |
| `status` | String | `ACTIVE`, `SUSPENDED`, `LOCKED`, `INACTIVE` |
| `userType` | String | `USER`, `VENDOR`, `ADMIN`, `SUPPORT_AGENT` |
| `search` | String | Search by name/email/phone |

### Success Response `200 OK` — List of `UserResponse` DTOs
```json
{
  "success": true,
  "data": [
    {
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "vendorId": null,
      "email": "john.doe@gmail.com",
      "phone": "+917890123456",
      "userType": "USER",
      "firstName": "John",
      "lastName": "Doe",
      "fullName": "John Doe",
      "profilePictureUrl": "http://localhost:8080/uploads/images/profile-550e8400.jpg",
      "dateOfBirth": "1992-06-15",
      "gender": "MALE",
      "emailVerified": true,
      "phoneVerified": true,
      "twoFactorEnabled": false,
      "preferredLanguage": "en",
      "preferredCurrency": "INR",
      "country": "INDIA",
      "status": "ACTIVE",
      "notificationPreferences": {
        "emailNotifications": { "orderUpdates": true, "bidUpdates": true, "promotional": false, "newsletter": false, "paymentReminders": true, "securityAlerts": true },
        "smsNotifications": { "orderUpdates": true, "bidUpdates": true, "paymentReminders": true, "securityAlerts": true },
        "pushNotifications": { "orderUpdates": true, "bidUpdates": true, "promotional": false, "paymentReminders": true },
        "whatsappNotifications": { "orderUpdates": false, "bidUpdates": false }
      },
      "lastLoginAt": "2026-02-24T10:30:45.123456Z",
      "createdAt": "2026-01-10T08:00:00.000000Z"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 12500, "totalPages": 625 }
}
```

---

# 3. GET USER BY ID

```http
GET /admin/users/{userId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK` — Full `UserResponse` DTO
```json
{
  "success": true,
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "vendorId": null,
    "email": "john.doe@gmail.com",
    "phone": "+917890123456",
    "userType": "USER",
    "firstName": "John",
    "lastName": "Doe",
    "fullName": "John Doe",
    "profilePictureUrl": "http://localhost:8080/uploads/images/profile-550e8400.jpg",
    "dateOfBirth": "1992-06-15",
    "gender": "MALE",
    "emailVerified": true,
    "phoneVerified": true,
    "twoFactorEnabled": false,
    "preferredLanguage": "en",
    "preferredCurrency": "INR",
    "country": "INDIA",
    "status": "ACTIVE",
    "notificationPreferences": {
      "emailNotifications": { "orderUpdates": true, "bidUpdates": true, "promotional": false, "newsletter": false, "paymentReminders": true, "securityAlerts": true },
      "smsNotifications": { "orderUpdates": true, "bidUpdates": true, "paymentReminders": true, "securityAlerts": true },
      "pushNotifications": { "orderUpdates": true, "bidUpdates": true, "promotional": false, "paymentReminders": true },
      "whatsappNotifications": { "orderUpdates": false, "bidUpdates": false }
    },
    "lastLoginAt": "2026-02-24T10:30:45.123456Z",
    "createdAt": "2026-01-10T08:00:00.000000Z"
  }
}
```

---

# 4. SUSPEND USER

```http
PATCH /admin/users/{userId}/suspend
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{ "reason": "Multiple fraud reports received from vendors" }
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "User suspended successfully",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "vendorId": null,
    "email": "john.doe@gmail.com",
    "phone": "+917890123456",
    "userType": "USER",
    "firstName": "John",
    "lastName": "Doe",
    "fullName": "John Doe",
    "profilePictureUrl": null,
    "dateOfBirth": null,
    "gender": null,
    "emailVerified": true,
    "phoneVerified": true,
    "twoFactorEnabled": false,
    "preferredLanguage": null,
    "preferredCurrency": "INR",
    "country": "INDIA",
    "status": "SUSPENDED",
    "notificationPreferences": { "...same structure as above..." },
    "lastLoginAt": "2026-02-24T10:30:45.123456Z",
    "createdAt": "2026-01-10T08:00:00.000000Z"
  }
}
```

---

# 5. REACTIVATE USER

```http
PATCH /admin/users/{userId}/activate
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "User activated successfully",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "ACTIVE",
    "...rest of UserResponse fields..."
  }
}
```

---

# 6. CREATE SUPPORT AGENT ACCOUNT

```http
POST /admin/users/create-agent
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request — Based on `RegisterRequest` DTO
```json
{
  "email": "priya.agent@bidzaro.com",
  "phone": "+917890111222",
  "password": "AgentPass@123",
  "firstName": "Priya",
  "lastName": "Sharma",
  "country": "INDIA",
  "userType": "SUPPORT_AGENT"
}
```

### Success Response `201 Created`
```json
{
  "success": true,
  "message": "Support agent account created",
  "data": {
    "userId": "agent-priya-001",
    "vendorId": null,
    "email": "priya.agent@bidzaro.com",
    "phone": "+917890111222",
    "userType": "SUPPORT_AGENT",
    "firstName": "Priya",
    "lastName": "Sharma",
    "fullName": "Priya Sharma",
    "profilePictureUrl": null,
    "dateOfBirth": null,
    "gender": null,
    "emailVerified": false,
    "phoneVerified": false,
    "twoFactorEnabled": false,
    "preferredLanguage": null,
    "preferredCurrency": "INR",
    "country": "INDIA",
    "status": "ACTIVE",
    "notificationPreferences": {
      "emailNotifications": { "orderUpdates": true, "bidUpdates": true, "promotional": false, "newsletter": false, "paymentReminders": true, "securityAlerts": true },
      "smsNotifications": { "orderUpdates": true, "bidUpdates": true, "paymentReminders": true, "securityAlerts": true },
      "pushNotifications": { "orderUpdates": true, "bidUpdates": true, "promotional": false, "paymentReminders": true },
      "whatsappNotifications": { "orderUpdates": false, "bidUpdates": false }
    },
    "lastLoginAt": null,
    "createdAt": "2026-02-24T10:30:45.123456Z"
  }
}
```

---

# 7. GET ALL VENDORS (Admin View)

```http
GET /admin/vendors?page=0&size=20&approvalStatus=PENDING
Authorization: Bearer {accessToken}
```

### Query Parameters
| Param | Values |
|-------|--------|
| `approvalStatus` | `PENDING`, `APPROVED`, `REJECTED` |
| `status` | `ACTIVE`, `INACTIVE`, `SUSPENDED` |
| `country` | `INDIA`, `USA` |
| `search` | Search by business name/email |

### Success Response `200 OK` — List of full `VendorResponse` DTOs
```json
{
  "success": true,
  "data": [
    {
      "vendorId": "vendor-12345-67890",
      "userId": "vendor-user-550e8400",
      "registeredEmail": "owner@spicegarden.com",
      "registeredPhone": "+917890123456",
      "registeredEmailVerified": false,
      "registeredPhoneVerified": false,
      "businessName": "Spice Garden Catering",
      "businessEmail": "info@spicegarden.com",
      "businessPhone": "+917890123456",
      "businessEmailVerified": false,
      "businessPhoneVerified": false,
      "businessType": "CATERING",
      "businessRegistrationNumber": "KA-REG-2015-12345",
      "taxId": "29ABCDE1234F1Z5",
      "logoUrl": null,
      "bannerUrl": null,
      "description": "Authentic South Indian catering since 2010.",
      "establishedYear": 2010,
      "cuisinesOffered": ["South Indian", "North Indian"],
      "specialties": ["Weddings", "Corporate Events"],
      "businessAddress": { "streetAddress": "25, 3rd Cross, Jayanagar 4th Block", "city": "Bangalore", "state": "Karnataka", "postalCode": "560041", "country": "India" },
      "ownerInfo": { "firstName": "Rajesh", "lastName": "Kumar", "phone": "+917890123456", "email": "owner@spicegarden.com", "idProofType": "AADHAR", "idProofNumber": "1234-5678-9012" },
      "serviceAreas": [ { "city": "Bangalore", "state": "Karnataka", "radiusKm": 30 } ],
      "capacity": { "minGuests": 50, "maxGuests": 3000, "concurrentEvents": 4 },
      "pricing": { "currency": "INR", "startingPricePerPlate": 350.00, "averagePricePerPlate": 500.00 },
      "ratings": null,
      "stats": null,
      "status": "PENDING",
      "approvalStatus": "PENDING",
      "verified": false,
      "featured": false,
      "createdAt": "2026-02-24T10:30:45.123456Z",
      "documents": [
        {
          "documentId": "doc-001-aabb",
          "documentType": "BUSINESS_LICENSE",
          "documentName": "FSSAI Food License",
          "documentUrl": "http://localhost:8080/uploads/documents/fssai-license.pdf",
          "documentNumber": "FSSAI-2024-123456",
          "issueDate": "2024-01-15T00:00:00.000000Z",
          "expiryDate": "2027-01-14T00:00:00.000000Z",
          "verificationStatus": "PENDING",
          "uploadedAt": "2026-02-24T10:30:45.123456Z"
        }
      ],
      "country": "INDIA"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 28, "totalPages": 2 }
}
```

---

# 8. APPROVE VENDOR

```http
POST /admin/vendors/{vendorId}/approve
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{ "notes": "All documents verified. FSSAI license valid. Approved for Bangalore operations." }
```

### Success Response `200 OK` — Full `VendorResponse` DTO
```json
{
  "success": true,
  "message": "Vendor approved successfully",
  "data": {
    "vendorId": "vendor-12345-67890",
    "userId": "vendor-user-550e8400",
    "registeredEmail": "owner@spicegarden.com",
    "registeredPhone": "+917890123456",
    "registeredEmailVerified": false,
    "registeredPhoneVerified": false,
    "businessName": "Spice Garden Catering",
    "businessEmail": "info@spicegarden.com",
    "businessPhone": "+917890123456",
    "businessEmailVerified": false,
    "businessPhoneVerified": false,
    "businessType": "CATERING",
    "businessRegistrationNumber": "KA-REG-2015-12345",
    "taxId": "29ABCDE1234F1Z5",
    "logoUrl": null,
    "bannerUrl": null,
    "description": "Authentic South Indian catering since 2010.",
    "establishedYear": 2010,
    "cuisinesOffered": ["South Indian", "North Indian"],
    "specialties": ["Weddings", "Corporate Events"],
    "businessAddress": { "streetAddress": "25, 3rd Cross, Jayanagar 4th Block", "city": "Bangalore", "state": "Karnataka", "postalCode": "560041", "country": "India" },
    "ownerInfo": { "firstName": "Rajesh", "lastName": "Kumar", "phone": "+917890123456", "email": "owner@spicegarden.com", "idProofType": "AADHAR", "idProofNumber": "1234-5678-9012" },
    "serviceAreas": [ { "city": "Bangalore", "state": "Karnataka", "radiusKm": 30 } ],
    "capacity": { "minGuests": 50, "maxGuests": 3000, "concurrentEvents": 4 },
    "pricing": { "currency": "INR", "startingPricePerPlate": 350.00, "averagePricePerPlate": 500.00 },
    "ratings": { "averageRating": null, "totalReviews": 0 },
    "stats": { "totalOrders": 0, "completedOrders": 0 },
    "status": "ACTIVE",
    "approvalStatus": "APPROVED",
    "verified": true,
    "featured": false,
    "createdAt": "2026-02-24T10:30:45.123456Z",
    "documents": [
      {
        "documentId": "doc-001-aabb",
        "documentType": "BUSINESS_LICENSE",
        "documentName": "FSSAI Food License",
        "documentUrl": "http://localhost:8080/uploads/documents/fssai-license.pdf",
        "documentNumber": "FSSAI-2024-123456",
        "issueDate": "2024-01-15T00:00:00.000000Z",
        "expiryDate": "2027-01-14T00:00:00.000000Z",
        "verificationStatus": "VERIFIED",
        "uploadedAt": "2026-02-24T10:30:45.123456Z"
      }
    ],
    "country": "INDIA"
  }
}
```

---

# 9. REJECT VENDOR

```http
POST /admin/vendors/{vendorId}/reject
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{ "reason": "FSSAI license is expired. Please renew and reapply." }
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Vendor rejected",
  "data": {
    "vendorId": "vendor-12345-67890",
    "businessName": "Spice Garden Catering",
    "status": "INACTIVE",
    "approvalStatus": "REJECTED",
    "...rest of VendorResponse fields..."
  }
}
```

---

# 10. SUSPEND VENDOR

```http
PATCH /admin/vendors/{vendorId}/suspend
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{ "reason": "Received 3 serious complaints of food quality issues in the last month" }
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Vendor suspended",
  "data": {
    "vendorId": "vendor-12345-67890",
    "businessName": "Spice Garden Catering",
    "status": "SUSPENDED",
    "approvalStatus": "APPROVED",
    "...rest of VendorResponse fields..."
  }
}
```

---

# 11. GET ALL ORDERS (Admin)

```http
GET /admin/orders?page=0&size=20&status=CONFIRMED
Authorization: Bearer {accessToken}
```

### Success Response `200 OK` — List of `OrderResponse` DTOs
```json
{
  "success": true,
  "data": [
    {
      "orderId": "order-54321-12345-ccdd",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "bidRequestId": "breq-88990-77665-aabb",
      "eventDetails": {
        "eventType": "WEDDING",
        "eventName": "Priya & Rahul Wedding",
        "eventDate": "2026-05-20",
        "eventTime": "18:00",
        "numberOfGuests": 500,
        "venueAddress": { "streetAddress": "Palace Grounds", "city": "Bangalore", "state": "Karnataka", "postalCode": "560080", "country": "India" }
      },
      "vendorOrders": [
        {
          "vendorOrderId": "vorder-001-aabb",
          "vendorId": "vendor-12345-67890",
          "vendorUserId": null,
          "vendorName": "Spice Garden Catering",
          "items": [
            { "vendorItemId": "vitem-001", "itemName": "Paneer Tikka", "quantity": 500, "pricePerPlate": 175.00, "totalPrice": 87500.00 }
          ],
          "subtotal": 175000.00,
          "serviceCharge": 17500.00,
          "taxAmount": 8750.00,
          "totalAmount": 201250.00,
          "vendorStatus": "ACCEPTED",
          "deliveryStatus": "PENDING"
        }
      ],
      "pricing": {
        "currency": "INR",
        "subtotal": 175000.00,
        "serviceCharges": 17500.00,
        "taxAmount": 8750.00,
        "platformFee": 4025.00,
        "discountAmount": 0.00,
        "totalAmount": 205275.00
      },
      "paymentDetails": {
        "tokenAmount": 51318.75,
        "tokenPaid": true,
        "tokenPaidAt": "2026-02-25T11:05:00.000000Z",
        "totalPaid": 51318.75,
        "balanceDue": 153956.25,
        "paymentStatus": "TOKEN_PAID"
      },
      "contactInfo": { "primaryContactName": "John Doe", "primaryContactPhone": "+917890123456", "primaryContactEmail": "john.doe@gmail.com" },
      "specialInstructions": "Separate veg and non-veg sections.",
      "status": "CONFIRMED",
      "cancellation": null,
      "createdAt": "2026-02-25T11:00:00.000000Z",
      "confirmedAt": "2026-02-25T11:00:00.000000Z",
      "deliveredAt": null,
      "completedAt": null
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 48500, "totalPages": 2425 }
}
```

---

# 12. OVERRIDE ORDER STATUS

```http
PATCH /admin/orders/{orderId}/status
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{
  "status": "COMPLETED",
  "reason": "Admin override — event completed, all payments cleared"
}
```

### Success Response `200 OK` — Full `OrderResponse` DTO
```json
{
  "success": true,
  "message": "Order status updated",
  "data": {
    "orderId": "order-54321-12345-ccdd",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "bidRequestId": "breq-88990-77665-aabb",
    "eventDetails": { "eventType": "WEDDING", "eventName": "Priya & Rahul Wedding", "eventDate": "2026-05-20", "eventTime": "18:00", "numberOfGuests": 500, "venueAddress": { "city": "Bangalore", "state": "Karnataka" } },
    "vendorOrders": [
      { "vendorOrderId": "vorder-001-aabb", "vendorId": "vendor-12345-67890", "vendorUserId": null, "vendorName": "Spice Garden Catering", "items": null, "subtotal": 175000.00, "serviceCharge": 17500.00, "taxAmount": 8750.00, "totalAmount": 201250.00, "vendorStatus": "COMPLETED", "deliveryStatus": "DELIVERED" }
    ],
    "pricing": { "currency": "INR", "subtotal": 175000.00, "serviceCharges": 17500.00, "taxAmount": 8750.00, "platformFee": 4025.00, "discountAmount": 0.00, "totalAmount": 205275.00 },
    "paymentDetails": { "tokenAmount": 51318.75, "tokenPaid": true, "tokenPaidAt": "2026-02-25T11:05:00.000000Z", "totalPaid": 205275.00, "balanceDue": 0.00, "paymentStatus": "FULLY_PAID" },
    "contactInfo": { "primaryContactName": "John Doe", "primaryContactPhone": "+917890123456", "primaryContactEmail": "john.doe@gmail.com" },
    "specialInstructions": null,
    "status": "COMPLETED",
    "cancellation": null,
    "createdAt": "2026-02-25T11:00:00.000000Z",
    "confirmedAt": "2026-02-25T11:00:00.000000Z",
    "deliveredAt": "2026-05-20T22:00:00.000000Z",
    "completedAt": "2026-05-21T08:00:00.000000Z"
  }
}
```

---

# 13. GET ALL BID REQUESTS (Admin)

```http
GET /admin/bids?page=0&size=20&status=ACTIVE
Authorization: Bearer {accessToken}
```

### Success Response `200 OK` — List of `BidRequestResponse` DTOs
```json
{
  "success": true,
  "data": [
    {
      "bidRequestId": "breq-88990-77665-aabb",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "eventDetails": {
        "eventType": "WEDDING",
        "eventName": "Priya & Rahul Wedding",
        "eventDate": "2026-05-20T18:00:00",
        "eventStartTime": "18:00",
        "eventEndTime": "23:30",
        "numberOfGuests": 500,
        "venueAddress": { "streetAddress": "Palace Grounds", "city": "Bangalore", "state": "Karnataka", "postalCode": "560080", "country": "India" }
      },
      "menuItems": [
        { "vendorItemId": null, "masterItemId": "item-001", "itemName": "Paneer Tikka", "quantity": 500 },
        { "vendorItemId": null, "masterItemId": "item-002", "itemName": "Butter Chicken", "quantity": 400 }
      ],
      "additionalRequirements": { "serviceStaffNeeded": true, "numberOfStaff": 25, "decorationNeeded": false, "liveCounters": ["Dosa Counter"], "specialInstructions": "Separate sections." },
      "budget": { "currency": "INR", "estimatedBudget": 200000, "budgetRange": "200000-250000" },
      "targetedVendors": [],
      "competitivePeriod": { "startTime": "2026-02-24T10:30:45.123456Z", "endTime": "2026-02-27T10:30:45.123456Z", "status": "ACTIVE" },
      "acceptedBid": null,
      "status": "ACTIVE",
      "totalBidsReceived": 3,
      "lowestBidAmount": 195000.00,
      "createdAt": "2026-02-24T10:30:45.123456Z",
      "expiresAt": "2026-03-03T10:30:45.123456Z"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 250, "totalPages": 13 }
}
```

---

# 14. CREATE MENU CATEGORY (Admin)

```http
POST /admin/menu/categories
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request — Based on `CategoryRequest` DTO
```json
{
  "categoryName": "Live Counters",
  "categoryNameHindi": "लाइव काउंटर",
  "description": "Live food counters for events",
  "displayOrder": 5,
  "iconUrl": "http://localhost:8080/uploads/icons/live-counters.svg"
}
```

### Success Response `201 Created` — `CategoryResponse` DTO
```json
{
  "success": true,
  "data": {
    "categoryId": "cat-005",
    "categoryName": "Live Counters",
    "categoryNameHindi": "लाइव काउंटर",
    "description": "Live food counters for events",
    "displayOrder": 5,
    "iconUrl": "http://localhost:8080/uploads/icons/live-counters.svg",
    "status": "ACTIVE"
  }
}
```

---

# 15. CREATE MASTER MENU ITEM (Admin)

```http
POST /admin/menu/items
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request — Based on `MasterMenuItemRequest` DTO
```json
{
  "itemName": "Chicken Biryani",
  "itemNameHindi": "चिकन बिरयानी",
  "description": "Aromatic basmati rice cooked with tender chicken and whole spices",
  "categoryId": "cat-002",
  "cuisineType": "Mughlai",
  "foodType": "NON_VEGETARIAN",
  "spiceLevel": "MEDIUM",
  "dietaryTags": [],
  "allergens": ["GLUTEN", "DAIRY"],
  "nutritionalInfo": {
    "calories": 520,
    "proteinGrams": 35,
    "carbsGrams": 65,
    "fatGrams": 14,
    "servingSizeGrams": 400
  },
  "imageUrls": [
    "http://localhost:8080/uploads/images/chicken-biryani-1.jpg"
  ],
  "isPopular": true
}
```

### Success Response `201 Created` — `MenuItemResponse` DTO
```json
{
  "success": true,
  "data": {
    "masterItemId": "item-new-001",
    "itemName": "Chicken Biryani",
    "itemNameHindi": "चिकन बिरयानी",
    "description": "Aromatic basmati rice cooked with tender chicken and whole spices",
    "categoryId": "cat-002",
    "categoryName": null,
    "cuisineType": "Mughlai",
    "foodType": "NON_VEGETARIAN",
    "spiceLevel": "MEDIUM",
    "dietaryTags": [],
    "allergens": ["GLUTEN", "DAIRY"],
    "nutritionalInfo": {
      "calories": 520,
      "proteinGrams": 35,
      "carbsGrams": 65,
      "fatGrams": 14,
      "servingSizeGrams": 400
    },
    "imageUrls": ["http://localhost:8080/uploads/images/chicken-biryani-1.jpg"],
    "isPopular": true,
    "status": "ACTIVE",
    "createdAt": "2026-02-24T10:30:45.123456Z",
    "updatedAt": "2026-02-24T10:30:45.123456Z"
  }
}
```

---

# 16. CREATE PROMO CODE (Admin)

```http
POST /admin/promos
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request — Based on `CreatePromoRequest` DTO
```json
{
  "code": "BIDZARO2026",
  "title": "Bidzaro Launch Offer",
  "description": "Flat ₹10,000 off on orders above ₹1,00,000",
  "type": "FIXED_AMOUNT",
  "value": 10000.00,
  "maxDiscountAmount": 10000.00,
  "minOrderAmount": 100000.00,
  "validFrom": "2026-02-24T00:00:00Z",
  "validTo": "2026-03-31T23:59:59Z",
  "usageLimitGlobal": 5000,
  "usageLimitPerUser": 1,
  "applicableTo": "ALL",
  "applicableVendorIds": null,
  "applicableCuisines": null,
  "firstOrderOnly": false
}
```

| Field | Required | Values |
|-------|----------|--------|
| `code` | ✅ | Unique promo code string |
| `type` | ✅ | `FIXED_AMOUNT`, `PERCENTAGE` |
| `value` | ✅ | Discount value |
| `validFrom` | ✅ | ISO datetime |
| `validTo` | ✅ | ISO datetime |
| `applicableTo` | ✅ | `ALL`, `SPECIFIC_VENDORS`, `SPECIFIC_CUISINES` |
| `maxDiscountAmount` | ❌ | Cap for PERCENTAGE type |
| `minOrderAmount` | ❌ | Minimum order to apply |
| `usageLimitGlobal` | ❌ | Total usage cap |
| `usageLimitPerUser` | ❌ | Per-user usage cap |
| `firstOrderOnly` | ❌ | Restrict to first order only |

### Success Response `201 Created` — `PromoCodeResponse` DTO
```json
{
  "success": true,
  "data": {
    "promoCodeId": "promo-bidzaro-2026-xxyy",
    "code": "BIDZARO2026",
    "title": "Bidzaro Launch Offer",
    "description": "Flat ₹10,000 off on orders above ₹1,00,000",
    "type": "FIXED_AMOUNT",
    "value": 10000.00,
    "maxDiscountAmount": 10000.00,
    "minOrderAmount": 100000.00,
    "validFrom": "2026-02-24T00:00:00.000000Z",
    "validTo": "2026-03-31T23:59:59.000000Z",
    "usageLimitGlobal": 5000,
    "usageLimitPerUser": 1,
    "usedCount": 0,
    "applicableTo": "ALL",
    "applicableVendorIds": null,
    "applicableCuisines": null,
    "firstOrderOnly": false,
    "status": "ACTIVE",
    "createdAt": "2026-02-24T10:30:45.123456Z"
  }
}
```

---

# 17. GET ALL PROMO CODES (Admin)

```http
GET /admin/promos?page=0&size=20&status=ACTIVE
Authorization: Bearer {accessToken}
```

### Success Response `200 OK` — List of `PromoCodeResponse` DTOs
```json
{
  "success": true,
  "data": [
    {
      "promoCodeId": "promo-bidzaro-2026-xxyy",
      "code": "BIDZARO2026",
      "title": "Bidzaro Launch Offer",
      "description": "Flat ₹10,000 off on orders above ₹1,00,000",
      "type": "FIXED_AMOUNT",
      "value": 10000.00,
      "maxDiscountAmount": 10000.00,
      "minOrderAmount": 100000.00,
      "validFrom": "2026-02-24T00:00:00.000000Z",
      "validTo": "2026-03-31T23:59:59.000000Z",
      "usageLimitGlobal": 5000,
      "usageLimitPerUser": 1,
      "usedCount": 125,
      "applicableTo": "ALL",
      "applicableVendorIds": null,
      "applicableCuisines": null,
      "firstOrderOnly": false,
      "status": "ACTIVE",
      "createdAt": "2026-02-23T00:00:00.000000Z"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 45, "totalPages": 3 }
}
```

---

# 18. DEACTIVATE PROMO CODE

```http
PATCH /admin/promos/{promoCodeId}/deactivate
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Promo code deactivated",
  "data": {
    "promoCodeId": "promo-bidzaro-2026-xxyy",
    "code": "BIDZARO2026",
    "status": "INACTIVE",
    "...rest of PromoCodeResponse fields..."
  }
}
```

---

# 19. GET ALL SUPPORT TICKETS (Admin)

```http
GET /admin/support/tickets?page=0&size=20&status=OPEN&priority=HIGH
Authorization: Bearer {accessToken}
```

### Success Response `200 OK` — List of `TicketResponse` DTOs
```json
{
  "success": true,
  "data": [
    {
      "ticketId": "tkt-99001-22334-eeff",
      "ticketNumber": "TKT-20260224-001",
      "createdBy": "550e8400-e29b-41d4-a716-446655440000",
      "createdByName": "John Doe",
      "category": "ORDER",
      "subcategory": "DELIVERY_ISSUE",
      "priority": "HIGH",
      "subject": "Food arrived 2 hours late for my wedding",
      "description": "The catering team was supposed to arrive by 5 PM but arrived at 7 PM...",
      "relatedEntities": {
        "orderId": "order-54321-12345-ccdd",
        "vendorId": "vendor-12345-67890",
        "paymentId": null
      },
      "assignedTo": "agent-priya-001",
      "assignedAt": "2026-02-24T10:31:00.000000Z",
      "conversationId": "conv-support-tkt99001",
      "status": "IN_PROGRESS",
      "sla": {
        "firstResponseDue": "2026-02-24T11:30:45.123456Z",
        "resolutionDue": "2026-02-25T10:30:45.123456Z",
        "firstResponseAt": "2026-02-24T11:00:00.000000Z",
        "resolvedAt": null,
        "slaBreached": false
      },
      "resolution": null,
      "customerSatisfaction": null,
      "createdAt": "2026-02-24T10:30:45.123456Z",
      "closedAt": null
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 156, "totalPages": 8 }
}
```

---

# 20. INITIATE REFUND (Admin)

```http
POST /admin/payments/{transactionId}/refund
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{
  "refundAmount": 51318.75,
  "reason": "Customer cancellation — event postponed more than 30 days. Full refund eligible."
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Refund initiated successfully",
  "data": {
    "transactionId": "txn-99887-66554-eeff",
    "refundId": "refund-001-aabb",
    "orderId": "order-54321-12345-ccdd",
    "refundAmount": 51318.75,
    "currency": "INR",
    "status": "REFUND_INITIATED",
    "estimatedArrival": "5-7 business days",
    "initiatedAt": "2026-02-26T10:00:00.000000Z"
  }
}
```

---

# 21. GET PLATFORM CONFIG

```http
GET /admin/platform-config/{country}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK` — Full `PlatformConfig` fields
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
      "minVendorsForCompetitive": 3,
      "maxBidRevisions": 5
    },
    "paymentConfig": {
      "tokenPercentage": 25,
      "enabledGateways": ["RAZORPAY"],
      "defaultGateway": "RAZORPAY",
      "paymentTimeoutHours": 24,
      "autoRefundEnabled": true
    },
    "cancellationPolicy": {
      "cancellationWindowDays": 30,
      "refundTiers": [
        { "daysBeforeEvent": 30, "refundPercentage": 100 },
        { "daysBeforeEvent": 15, "refundPercentage": 75 },
        { "daysBeforeEvent": 7,  "refundPercentage": 50 },
        { "daysBeforeEvent": 3,  "refundPercentage": 25 },
        { "daysBeforeEvent": 0,  "refundPercentage": 0 }
      ]
    },
    "commissionConfig": {
      "platformFeePercentage": 2,
      "vendorCommissionPercentage": 10,
      "paymentGatewayFeePercentage": 2
    },
    "updatedBy": "admin-001",
    "updatedAt": "2026-02-01T00:00:00.000000Z"
  }
}
```

---

# 22. UPDATE PLATFORM CONFIG

```http
PUT /admin/platform-config/{country}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request — Based on `UpdatePlatformConfigRequest` DTO
```json
{
  "biddingConfig": {
    "competitivePeriodHours": 96,
    "coolingPeriodHours": 24,
    "paymentCoolingPeriodHours": 2,
    "bidExpiryHours": 168,
    "minVendorsForCompetitive": 3,
    "maxBidRevisions": 5
  },
  "paymentConfig": {
    "tokenPercentage": 25,
    "enabledGateways": ["RAZORPAY"],
    "defaultGateway": "RAZORPAY",
    "paymentTimeoutHours": 24,
    "autoRefundEnabled": true
  },
  "cancellationPolicy": {
    "cancellationWindowDays": 30,
    "refundTiers": [
      { "daysBeforeEvent": 30, "refundPercentage": 100 },
      { "daysBeforeEvent": 15, "refundPercentage": 75 },
      { "daysBeforeEvent": 7,  "refundPercentage": 50 },
      { "daysBeforeEvent": 3,  "refundPercentage": 25 },
      { "daysBeforeEvent": 0,  "refundPercentage": 0 }
    ]
  },
  "commissionConfig": {
    "platformFeePercentage": 2,
    "vendorCommissionPercentage": 10,
    "paymentGatewayFeePercentage": 2
  }
}
```

### Success Response `200 OK` — Full config returned (same shape as GET)

---

# 23. CREATE ANNOUNCEMENT

```http
POST /admin/announcements
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request — Based on `CreateAnnouncementRequest` DTO
```json
{
  "title": "Platform Maintenance — Feb 28, 2026",
  "content": "Bidzaro will undergo scheduled maintenance on Feb 28, 2026 from 2 AM to 4 AM IST. Services will be temporarily unavailable.",
  "type": "MAINTENANCE",
  "targetAudience": "ALL",
  "isActive": true,
  "expiresAt": "2026-02-28T06:00:00Z"
}
```

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "announcementId": "ann-001-aabb",
    "title": "Platform Maintenance — Feb 28, 2026",
    "content": "Bidzaro will undergo scheduled maintenance on Feb 28, 2026 from 2 AM to 4 AM IST.",
    "type": "MAINTENANCE",
    "targetAudience": "ALL",
    "isActive": true,
    "expiresAt": "2026-02-28T06:00:00.000000Z",
    "createdBy": "admin-001",
    "createdAt": "2026-02-24T10:30:45.123456Z"
  }
}
```

---

# 24. GET AUDIT LOGS

```http
GET /admin/audit-logs?page=0&size=20&action=VENDOR_APPROVED
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "logId": "log-001-aabb",
      "action": "VENDOR_APPROVED",
      "performedBy": "admin-001",
      "performedByName": "Super Admin",
      "targetType": "VENDOR",
      "targetId": "vendor-12345-67890",
      "details": "Vendor approved. Notes: All documents verified. FSSAI license valid.",
      "ipAddress": "203.0.113.45",
      "createdAt": "2026-02-24T10:30:45.123456Z"
    },
    {
      "logId": "log-002-ccdd",
      "action": "USER_SUSPENDED",
      "performedBy": "admin-001",
      "performedByName": "Super Admin",
      "targetType": "USER",
      "targetId": "550e8400-e29b-41d4-a716-446655440000",
      "details": "User suspended. Reason: Multiple fraud reports received.",
      "ipAddress": "203.0.113.45",
      "createdAt": "2026-02-23T14:00:00.000000Z"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 2500, "totalPages": 125 }
}
```

---

# 25. PLATFORM ANALYTICS

```http
GET /analytics/overview?period=MONTHLY
Authorization: Bearer {accessToken}
```

### Success Response `200 OK` — `AnalyticsOverviewResponse` DTO
```json
{
  "success": true,
  "data": {
    "period": "MONTHLY",
    "totalRevenue": 3800000.00,
    "totalOrders": 840,
    "totalBidRequests": 1200,
    "totalNewUsers": 1250,
    "totalNewVendors": 32,
    "averageOrderValue": 4523.81,
    "topCities": [
      { "city": "Bangalore", "orderCount": 320, "revenue": 1450000.00 },
      { "city": "Mumbai",    "orderCount": 215, "revenue": 980000.00  },
      { "city": "Delhi",     "orderCount": 180, "revenue": 820000.00  }
    ],
    "topCuisines": [
      { "cuisine": "South Indian", "orderCount": 380 },
      { "cuisine": "North Indian", "orderCount": 290 }
    ],
    "topEventTypes": [
      { "eventType": "WEDDING",    "count": 420 },
      { "eventType": "CORPORATE",  "count": 225 },
      { "eventType": "BIRTHDAY",   "count": 195 }
    ]
  }
}
```

---

# 📌 Admin Error Codes

| HTTP | Error Code | Description |
|------|-----------|-------------|
| 400 | `INVALID_STATUS` | Invalid status value provided |
| 400 | `INVALID_CONFIG` | Invalid platform configuration |
| 401 | `UNAUTHORIZED` | Token missing or expired |
| 403 | `FORBIDDEN` | Not an admin account |
| 404 | `USER_NOT_FOUND` | User does not exist |
| 404 | `VENDOR_NOT_FOUND` | Vendor does not exist |
| 404 | `ORDER_NOT_FOUND` | Order does not exist |
| 404 | `PROMO_NOT_FOUND` | Promo code not found |
| 409 | `PROMO_CODE_EXISTS` | Promo code already exists |
| 409 | `VENDOR_ALREADY_APPROVED` | Vendor is already approved |

---

*ADMIN_API_DOCS.md — Based on actual Java DTOs (DashboardStatsResponse, UserResponse, VendorResponse, OrderResponse, BidRequestResponse, CategoryResponse, MenuItemResponse, PromoCodeResponse, TicketResponse, PlatformConfig, CreateAnnouncementRequest, UpdatePlatformConfigRequest, CreatePromoRequest)*
*Bidzaro Catering Platform v1.0.0 | Generated: February 24, 2026*

