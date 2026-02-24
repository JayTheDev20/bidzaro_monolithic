# 🏪 VENDOR API DOCUMENTATION
## Bidzaro Catering Platform — Complete Vendor Guide

**Version:** 1.0.0
**Base URL:** `http://localhost:8080/api/v1`
**Target Audience:** Catering Service Providers / Vendors
**Authentication:** JWT Bearer Token (Role: VENDOR)

---

## 📋 Table of Contents

1. [Vendor Registration & Auth](#1-vendor-registration--auth)
2. [Vendor Profile Management](#2-vendor-profile-management)
3. [Menu Management](#3-menu-management)
4. [Bid Management (Leads & Quotes)](#4-bid-management-leads--quotes)
5. [Order Management](#5-order-management)
6. [Chat with Customers](#6-chat-with-customers)
7. [Reviews & Ratings](#7-reviews--ratings)
8. [Analytics & Dashboard](#8-analytics--dashboard)
9. [File Uploads](#9-file-uploads)
10. [Notifications](#10-notifications)

---

## 🔐 Vendor Auth Flow

```
1. Register as USER  →  POST /auth/register  (userType: "VENDOR")
2. Create Vendor Profile  →  POST /vendors
3. Wait for Admin Approval (status: PENDING → APPROVED)
4. Login is blocked until APPROVED
5. After approval, Login  →  POST /auth/login
6. Use accessToken with Authorization: Bearer {accessToken}
```

> **Important:** Vendors CANNOT login until their profile is approved by an admin.

---

# 1. Vendor Registration & Auth

## 1.1 Register User Account (Step 1)

**Use:** First, create a user account with `userType: VENDOR`.

```http
POST /auth/register
Content-Type: application/json
```

### Request Payload
```json
{
  "email": "owner@spicegarden.com",
  "phone": "+917890123456",
  "password": "VendorPass@123",
  "firstName": "Rajesh",
  "lastName": "Kumar",
  "userType": "VENDOR",
  "country": "INDIA",
  "fcmToken": "firebase_device_token_here",
  "deviceInfo": "iPhone 15 - iOS 17"
}
```

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "userId": "vendor-user-550e8400",
      "email": "owner@spicegarden.com",
      "userType": "VENDOR",
      "status": "PENDING_VERIFICATION"
    }
  }
}
```

---

## 1.2 Create Vendor Business Profile (Step 2)

**Use:** Submit your business details for admin review and approval.

```http
POST /vendors
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "businessName": "Spice Garden Catering",
  "businessEmail": "info@spicegarden.com",
  "businessPhone": "+917890123456",
  "businessType": "CATERING",
  "businessRegistrationNumber": "KA-REG-2015-12345",
  "taxId": "29ABCDE1234F1Z5",
  "description": "Premium authentic South Indian catering since 2010. We specialize in traditional wedding feasts and corporate lunches.",
  "establishedYear": 2010,
  "country": "INDIA",
  "businessAddress": {
    "streetAddress": "25, 3rd Cross, Jayanagar 4th Block",
    "city": "Bangalore",
    "state": "Karnataka",
    "postalCode": "560041",
    "country": "India",
    "latitude": 12.9279,
    "longitude": 77.5826
  },
  "ownerInfo": {
    "firstName": "Rajesh",
    "lastName": "Kumar",
    "phone": "+917890123456",
    "email": "owner@spicegarden.com",
    "idProofType": "AADHAR",
    "idProofNumber": "1234-5678-9012"
  },
  "serviceAreas": [
    { "city": "Bangalore", "state": "Karnataka", "radiusKm": 30 },
    { "city": "Mysore", "state": "Karnataka", "radiusKm": 20 }
  ],
  "cuisinesOffered": ["South Indian", "North Indian", "Continental"],
  "specialties": ["Weddings", "Corporate Events", "Birthday Parties", "Religious Functions"],
  "capacity": {
    "minGuests": 50,
    "maxGuests": 3000,
    "concurrentEvents": 4
  },
  "pricing": {
    "startingPricePerPlate": 350,
    "averagePricePerPlate": 500,
    "currency": "INR"
  },
  "documents": [
    {
      "documentType": "BUSINESS_LICENSE",
      "documentName": "FSSAI Food License",
      "documentUrl": "http://localhost:8080/uploads/documents/fssai-license.pdf",
      "documentNumber": "FSSAI-2024-123456",
      "issueDate": "2024-01-15",
      "expiryDate": "2027-01-14"
    },
    {
      "documentType": "TAX_CERTIFICATE",
      "documentName": "GST Certificate",
      "documentUrl": "http://localhost:8080/uploads/documents/gst-cert.pdf",
      "documentNumber": "29ABCDE1234F1Z5",
      "issueDate": "2020-06-01",
      "expiryDate": null
    },
    {
      "documentType": "HEALTH_CERTIFICATE",
      "documentName": "Health & Hygiene Certificate",
      "documentUrl": "http://localhost:8080/uploads/documents/health-cert.pdf",
      "documentNumber": "KA-HEALTH-2024-789",
      "issueDate": "2024-02-01",
      "expiryDate": "2025-01-31"
    }
  ]
}
```

### Field Rules
| Field | Required | Values / Rules |
|-------|----------|----------------|
| `businessName` | ✅ | Max 255 chars |
| `businessEmail` | ✅ | Valid email, unique across vendors |
| `businessPhone` | ✅ | E.164 format |
| `businessType` | ✅ | `CATERING`, `RESTAURANT`, `CLOUD_KITCHEN`, `HOME_CHEF`, `BAKERY` |
| `businessRegistrationNumber` | ✅ | Max 100 chars |
| `taxId` | ✅ | GST/Tax ID |
| `country` | ✅ | `INDIA` or `USA` |
| `documents` | ✅ | Min 2 documents required |
| `serviceAreas` | ✅ | At least 1 service area |

### Success Response `201 Created`
```json
{
  "success": true,
  "message": "Vendor registered successfully. Pending approval.",
  "data": {
    "vendorId": "vendor-12345-67890",
    "userId": "vendor-user-550e8400",
    "businessName": "Spice Garden Catering",
    "businessEmail": "info@spicegarden.com",
    "businessType": "CATERING",
    "approvalStatus": "PENDING",
    "status": "PENDING_APPROVAL",
    "country": "INDIA",
    "verified": false,
    "ratings": {
      "averageRating": 0,
      "totalReviews": 0
    },
    "createdAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 1.3 Vendor Login (After Approval)

```http
POST /auth/login
Content-Type: application/json
```

### Request Payload
```json
{
  "identifier": "owner@spicegarden.com",
  "password": "VendorPass@123",
  "fcmToken": "firebase_device_token_here",
  "deviceInfo": "iPhone 15 - iOS 17"
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "userId": "vendor-user-550e8400",
      "userType": "VENDOR",
      "status": "ACTIVE"
    }
  }
}
```

### Error — Not Yet Approved
```json
{
  "success": false,
  "statusCode": 401,
  "error": {
    "code": "VENDOR_NOT_APPROVED",
    "message": "Your vendor account is currently PENDING. Please wait for admin approval."
  }
}
```

---

# 2. Vendor Profile Management

## 2.1 Get My Vendor Profile

```http
GET /vendors/me
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "vendorId": "vendor-12345-67890",
    "userId": "vendor-user-550e8400",
    "businessName": "Spice Garden Catering",
    "businessEmail": "info@spicegarden.com",
    "businessPhone": "+917890123456",
    "logoUrl": "http://localhost:8080/uploads/images/logo-spice.jpg",
    "bannerUrl": "http://localhost:8080/uploads/images/banner-spice.jpg",
    "description": "Premium authentic South Indian catering since 2010.",
    "establishedYear": 2010,
    "country": "INDIA",
    "verified": true,
    "featured": false,
    "status": "ACTIVE",
    "approvalStatus": "APPROVED",
    "approvalDate": "2026-01-20T09:00:00.000Z",
    "ratings": {
      "averageRating": 4.7,
      "totalReviews": 312,
      "ratingBreakdown": {
        "fiveStars": 260,
        "fourStars": 40,
        "threeStars": 10,
        "twoStars": 2,
        "oneStar": 0
      }
    },
    "stats": {
      "totalOrders": 600,
      "completedOrders": 596,
      "cancelledOrders": 4,
      "totalRevenue": 900000,
      "averageOrderValue": 1500,
      "responseTimeMinutes": 12
    },
    "businessAddress": {
      "streetAddress": "25, 3rd Cross, Jayanagar 4th Block",
      "city": "Bangalore",
      "state": "Karnataka",
      "postalCode": "560041",
      "country": "India"
    },
    "serviceAreas": [
      { "city": "Bangalore", "state": "Karnataka", "radiusKm": 30 },
      { "city": "Mysore", "state": "Karnataka", "radiusKm": 20 }
    ],
    "cuisinesOffered": ["South Indian", "North Indian", "Continental"],
    "specialties": ["Weddings", "Corporate Events"],
    "capacity": { "minGuests": 50, "maxGuests": 3000, "concurrentEvents": 4 },
    "pricing": {
      "startingPricePerPlate": 350,
      "averagePricePerPlate": 500,
      "currency": "INR"
    },
    "createdAt": "2026-01-15T08:00:00.000Z"
  }
}
```

---

## 2.2 Update Vendor Profile

```http
PUT /vendors/{vendorId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "description": "Updated: Premium South Indian & Continental catering for all occasions since 2010.",
  "logoUrl": "http://localhost:8080/uploads/images/logo-spice-new.jpg",
  "bannerUrl": "http://localhost:8080/uploads/images/banner-spice-new.jpg",
  "cuisinesOffered": ["South Indian", "North Indian", "Continental", "Chinese"],
  "specialties": ["Weddings", "Corporate Events", "Birthday Parties"],
  "serviceAreas": [
    { "city": "Bangalore", "state": "Karnataka", "radiusKm": 40 },
    { "city": "Mysore", "state": "Karnataka", "radiusKm": 25 },
    { "city": "Mangalore", "state": "Karnataka", "radiusKm": 15 }
  ],
  "capacity": {
    "minGuests": 30,
    "maxGuests": 5000,
    "concurrentEvents": 5
  },
  "pricing": {
    "startingPricePerPlate": 400,
    "averagePricePerPlate": 600,
    "currency": "INR"
  }
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Vendor profile updated successfully",
  "data": {
    "vendorId": "vendor-12345-67890",
    "businessName": "Spice Garden Catering",
    "description": "Updated: Premium South Indian & Continental...",
    "updatedAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

# 3. Menu Management

## 3.1 Get My Menu Items

**Use:** See all menu items you've added to your profile.

```http
GET /menu/vendor-items?vendorId=vendor-12345-67890&page=0&size=20
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "vendorItemId": "vitem-001",
      "masterItemId": "item-001",
      "vendorId": "vendor-12345-67890",
      "customName": "Spice Garden Special Paneer Tikka",
      "basePrice": 200,
      "discountPercentage": 10,
      "effectivePrice": 180,
      "isAvailable": true,
      "preparationTime": 25,
      "customizationOptions": [
        {
          "name": "Spice Level",
          "options": ["Mild", "Medium", "Spicy", "Extra Spicy"],
          "required": false
        }
      ],
      "status": "ACTIVE",
      "createdAt": "2026-01-20T10:00:00.000Z"
    }
  ],
  "pageInfo": { "totalElements": 45, "totalPages": 3 }
}
```

---

## 3.2 Add Item to My Menu

**Use:** Link a master menu item to your vendor profile with your own price.

```http
POST /menu/vendor-items
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "masterItemId": "item-001",
  "customName": "Spice Garden Special Paneer Tikka",
  "pricePerPlate": 200,
  "discountPercentage": 10,
  "isAvailable": true,
  "preparationTime": 25,
  "customizationOptions": [
    {
      "name": "Spice Level",
      "options": ["Mild", "Medium", "Spicy", "Extra Spicy"],
      "required": false
    },
    {
      "name": "Serving Size",
      "options": ["Regular (150g)", "Large (250g)"],
      "required": false
    }
  ],
  "notes": "Prepared with fresh paneer. Gluten-free option available on request."
}
```

### Field Rules
| Field | Required | Rules |
|-------|----------|-------|
| `masterItemId` | ✅ | Must be a valid master item ID |
| `pricePerPlate` | ✅ | > 0, your selling price |
| `discountPercentage` | ❌ | 0 to 99 |
| `isAvailable` | ✅ | true or false |
| `preparationTime` | ❌ | Minutes (e.g. 20) |

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "vendorItemId": "vitem-045",
    "masterItemId": "item-001",
    "vendorId": "vendor-12345-67890",
    "customName": "Spice Garden Special Paneer Tikka",
    "basePrice": 200,
    "discountPercentage": 10,
    "effectivePrice": 180,
    "isAvailable": true,
    "status": "ACTIVE",
    "createdAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 3.3 Update Menu Item

```http
PUT /menu/vendor-items/{vendorItemId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "customName": "Spice Garden Paneer Tikka Deluxe",
  "pricePerPlate": 220,
  "discountPercentage": 5,
  "isAvailable": true,
  "preparationTime": 30
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "vendorItemId": "vitem-001",
    "customName": "Spice Garden Paneer Tikka Deluxe",
    "basePrice": 220,
    "effectivePrice": 209,
    "isAvailable": true,
    "updatedAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 3.4 Toggle Item Availability

**Use:** Quickly mark an item as available or unavailable (e.g., out of stock).

```http
PATCH /menu/vendor-items/{vendorItemId}/availability?isAvailable=false
Authorization: Bearer {accessToken}
```

### Query Parameters
| Param | Required | Values |
|-------|----------|--------|
| `isAvailable` | ✅ | `true` or `false` |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "vendorItemId": "vitem-001",
    "itemName": "Spice Garden Special Paneer Tikka",
    "isAvailable": false,
    "message": "Item marked as unavailable"
  }
}
```

---

## 3.5 Delete Menu Item

```http
DELETE /menu/vendor-items/{vendorItemId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Menu item removed from your profile"
}
```

---

## 3.6 Browse Master Menu Items (Catalog)

**Use:** Browse the platform's master catalog to find items you want to offer.

```http
GET /menu/items?page=0&size=20
GET /menu/categories
GET /menu/items/search?query=paneer&page=0&size=10
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "masterItemId": "item-001",
      "itemName": "Paneer Tikka",
      "itemNameHindi": "पनीर टिक्का",
      "description": "Grilled cottage cheese with spices",
      "categoryId": "cat-001",
      "categoryName": "Starters",
      "cuisineType": "North Indian",
      "foodType": "VEGETARIAN",
      "spiceLevel": "MEDIUM",
      "imageUrls": ["http://localhost:8080/uploads/images/paneer-tikka.jpg"],
      "isPopular": true
    }
  ]
}
```

---

# 4. Bid Management (Leads & Quotes)

## 4.1 View Active Bid Requests (Leads)

**Use:** See all open bid requests from customers in your service area.

```http
GET /bids/active?page=0&size=20
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "bidRequestId": "breq-88990-77665",
      "userId": "user-550e8400",
      "status": "ACTIVE",
      "eventDetails": {
        "eventType": "WEDDING",
        "eventName": "Priya & Rahul Wedding",
        "eventDate": "2026-05-20T18:00:00Z",
        "numberOfGuests": 500,
        "venueAddress": {
          "city": "Bangalore",
          "state": "Karnataka"
        }
      },
      "menuItems": [
        { "masterItemId": "item-001", "itemName": "Paneer Tikka", "quantity": 500 },
        { "masterItemId": "item-002", "itemName": "Butter Chicken", "quantity": 400 }
      ],
      "additionalRequirements": {
        "serviceStaffNeeded": true,
        "numberOfStaff": 25,
        "liveCounters": true,
        "specialInstructions": "Separate veg and non-veg counters"
      },
      "budget": {
        "estimatedBudget": 200000,
        "budgetRange": "200000-250000",
        "currency": "INR"
      },
      "competitivePeriod": {
        "startTime": "2026-02-24T10:30:45.123Z",
        "endTime": "2026-02-27T10:30:45.123Z",
        "status": "ACTIVE"
      },
      "expiresAt": "2026-03-03T10:30:45.123Z",
      "createdAt": "2026-02-24T10:30:45.123Z"
    }
  ],
  "pageInfo": { "totalElements": 15, "totalPages": 1 }
}
```

---

## 4.2 Submit a Bid (Quote)

**Use:** Submit your price quote for a customer's bid request.

```http
POST /bids/requests/{bidRequestId}/submit-bid
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "quotedPrice": {
    "subtotal": 175000,
    "serviceCharge": 17500,
    "taxAmount": 17500,
    "totalAmount": 210000,
    "currency": "INR"
  },
  "itemizedPricing": [
    {
      "vendorItemId": "vitem-001",
      "itemName": "Paneer Tikka",
      "quantity": 500,
      "pricePerPlate": 175,
      "totalPrice": 87500
    },
    {
      "vendorItemId": "vitem-002",
      "itemName": "Butter Chicken",
      "quantity": 400,
      "pricePerPlate": 220,
      "totalPrice": 88000
    },
    {
      "vendorItemId": "vitem-010",
      "itemName": "Gulab Jamun",
      "quantity": 500,
      "pricePerPlate": 0,
      "totalPrice": 0,
      "notes": "Complimentary with order"
    }
  ],
  "validityPeriodHours": 48,
  "notes": "Price includes service staff (25 persons), live counter setup, and basic decoration."
}
```

### Field Rules
| Field | Required | Rules |
|-------|----------|-------|
| `quotedPrice.totalAmount` | ✅ | > 0 |
| `quotedPrice.currency` | ✅ | `INR` or `USD` |
| `itemizedPricing` | ❌ | Recommended for transparency |
| `validityPeriodHours` | ✅ | How long your quote is valid |

### Success Response `201 Created`
```json
{
  "success": true,
  "message": "Bid submitted successfully",
  "data": {
    "bidId": "bid-vendor-spice-001",
    "bidRequestId": "breq-88990-77665",
    "vendorId": "vendor-12345-67890",
    "status": "PENDING",
    "quotedPrice": {
      "subtotal": 175000,
      "serviceCharge": 17500,
      "taxAmount": 17500,
      "totalAmount": 210000,
      "currency": "INR"
    },
    "validityPeriodHours": 48,
    "submittedAt": "2026-02-24T10:30:45.123Z",
    "revisionCount": 0
  }
}
```

### Error Response — Already Submitted
```json
{
  "success": false,
  "statusCode": 409,
  "error": {
    "code": "BID_ALREADY_SUBMITTED",
    "message": "You have already submitted a bid for this request. Use PUT to revise."
  }
}
```

---

## 4.3 Revise Your Bid

**Use:** Update your submitted bid with a revised price (max 5 revisions).

```http
PUT /bids/{bidId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "quotedPrice": {
    "subtotal": 165000,
    "serviceCharge": 16500,
    "taxAmount": 16500,
    "totalAmount": 198000,
    "currency": "INR"
  },
  "itemizedPricing": [
    {
      "vendorItemId": "vitem-001",
      "itemName": "Paneer Tikka",
      "quantity": 500,
      "pricePerPlate": 165,
      "totalPrice": 82500
    },
    {
      "vendorItemId": "vitem-002",
      "itemName": "Butter Chicken",
      "quantity": 400,
      "pricePerPlate": 206,
      "totalPrice": 82400
    }
  ],
  "validityPeriodHours": 48
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Bid revised successfully",
  "data": {
    "bidId": "bid-vendor-spice-001",
    "quotedPrice": {
      "totalAmount": 198000,
      "currency": "INR"
    },
    "revisionCount": 1,
    "revisionsRemaining": 4,
    "revisedAt": "2026-02-25T09:00:00.000Z"
  }
}
```

---

## 4.4 Withdraw Your Bid

**Use:** Cancel your submitted bid if you can't take the order.

```http
DELETE /bids/{bidId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Bid withdrawn",
  "data": {
    "bidId": "bid-vendor-spice-001",
    "status": "WITHDRAWN",
    "withdrawnAt": "2026-02-24T15:00:00.000Z"
  }
}
```

---

## 4.5 Get All My Submitted Bids

```http
GET /bids/vendor/submitted?page=0&size=20&status=PENDING
Authorization: Bearer {accessToken}
```

### Query Parameters
| Param | Description |
|-------|-------------|
| `status` | `PENDING`, `ACCEPTED`, `REJECTED`, `WITHDRAWN`, `EXPIRED` |
| `page` | Page number |
| `size` | Items per page |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "bidId": "bid-vendor-spice-001",
      "bidRequestId": "breq-88990-77665",
      "status": "ACCEPTED",
      "quotedPrice": {
        "totalAmount": 198000,
        "currency": "INR"
      },
      "eventDetails": {
        "eventType": "WEDDING",
        "eventName": "Priya & Rahul Wedding",
        "eventDate": "2026-05-20T18:00:00Z",
        "numberOfGuests": 500
      },
      "submittedAt": "2026-02-24T10:30:45.123Z",
      "revisionCount": 1
    }
  ],
  "pageInfo": { "totalElements": 28, "totalPages": 2 }
}
```

---

# 5. Order Management

## 5.1 Get My Vendor Orders

**Use:** View all orders assigned to your vendor account.

```http
GET /orders/vendor?page=0&size=20&status=CONFIRMED
Authorization: Bearer {accessToken}
```

### Order Status Values
| Status | Meaning |
|--------|---------|
| `CONFIRMED` | Order confirmed, awaiting your action |
| `IN_PREPARATION` | You've started preparing |
| `READY_FOR_DELIVERY` | Food is ready |
| `DELIVERED` | Food delivered to venue |
| `COMPLETED` | Event complete, payment done |
| `CANCELLED` | Order cancelled |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "orderId": "order-54321-12345",
      "status": "CONFIRMED",
      "eventDetails": {
        "eventType": "WEDDING",
        "eventName": "Priya & Rahul Wedding",
        "eventDate": "2026-05-20",
        "eventTime": "18:00",
        "numberOfGuests": 500,
        "venueAddress": {
          "streetAddress": "Palace Grounds, Jayamahal",
          "city": "Bangalore",
          "state": "Karnataka"
        }
      },
      "vendorOrders": [
        {
          "vendorOrderId": "vorder-001",
          "vendorStatus": "ACCEPTED",
          "deliveryStatus": "PENDING",
          "items": [
            { "itemName": "Paneer Tikka", "quantity": 500, "pricePerPlate": 175 },
            { "itemName": "Butter Chicken", "quantity": 400, "pricePerPlate": 206 }
          ],
          "totalAmount": 198000
        }
      ],
      "paymentDetails": {
        "paymentStatus": "TOKEN_PAID",
        "tokenAmount": 49500,
        "balanceDue": 148500
      },
      "createdAt": "2026-02-25T11:00:00.000Z"
    }
  ],
  "pageInfo": { "totalElements": 12, "totalPages": 1 }
}
```

---

## 5.2 Get Vendor Order Details

```http
GET /orders/{orderId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "orderId": "order-54321-12345",
    "status": "CONFIRMED",
    "confirmedAt": "2026-02-25T11:00:00.000Z",
    "eventDetails": {
      "eventType": "WEDDING",
      "eventName": "Priya & Rahul Wedding",
      "eventDate": "2026-05-20",
      "eventTime": "18:00",
      "numberOfGuests": 500,
      "venueAddress": {
        "streetAddress": "Palace Grounds, Jayamahal",
        "city": "Bangalore",
        "state": "Karnataka",
        "postalCode": "560080",
        "gpsCoordinates": { "latitude": 12.9850, "longitude": 77.5950 }
      }
    },
    "vendorOrders": [
      {
        "vendorOrderId": "vorder-001",
        "vendorId": "vendor-12345-67890",
        "vendorStatus": "ACCEPTED",
        "deliveryStatus": "PENDING",
        "items": [
          { "itemName": "Paneer Tikka", "quantity": 500, "pricePerPlate": 175, "totalPrice": 87500 },
          { "itemName": "Butter Chicken", "quantity": 400, "pricePerPlate": 206, "totalPrice": 82400 },
          { "itemName": "Gulab Jamun", "quantity": 500, "pricePerPlate": 0, "totalPrice": 0 }
        ],
        "subtotal": 169900,
        "serviceCharge": 16990,
        "taxAmount": 16990,
        "totalAmount": 203880
      }
    ],
    "specialInstructions": "Separate veg and non-veg counters. Food ready by 6:30 PM.",
    "paymentDetails": {
      "paymentStatus": "TOKEN_PAID",
      "tokenAmount": 49500,
      "tokenPaidAt": "2026-02-25T11:05:00.000Z",
      "balanceDue": 148500
    },
    "createdAt": "2026-02-25T11:00:00.000Z"
  }
}
```

---

## 5.3 Update Order Status (Delivery Updates)

**Use:** Update the preparation and delivery progress of your order.

```http
PATCH /orders/{orderId}/status?status=IN_PREPARATION
Authorization: Bearer {accessToken}
```

### Status Progression Flow
```
CONFIRMED → IN_PREPARATION → READY_FOR_DELIVERY → DELIVERED → COMPLETED
```

### Allowed Status Values for Vendors
| Status | When to Use |
|--------|-------------|
| `IN_PREPARATION` | When you start preparing food |
| `READY_FOR_DELIVERY` | When food is packed and ready |
| `DELIVERED` | When food is delivered to the venue |

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Order status updated to IN_PREPARATION",
  "data": {
    "orderId": "order-54321-12345",
    "status": "IN_PREPARATION",
    "updatedAt": "2026-05-20T12:00:00.000Z"
  }
}
```

---

# 6. Chat with Customers

## 6.1 Get All My Conversations

```http
GET /chat/conversations?page=0&size=20
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "conversationId": "conv-77665-88990",
      "conversationType": "USER_VENDOR",
      "participants": [
        { "userId": "user-550e8400", "userType": "USER", "name": "John Doe" },
        { "userId": "vendor-user-550e8400", "userType": "VENDOR", "name": "Spice Garden Catering" }
      ],
      "lastMessage": {
        "message": "Can you do 500 guests for May 20?",
        "senderId": "user-550e8400",
        "timestamp": "2026-02-24T10:30:00.000Z"
      },
      "unreadCount": 2
    }
  ]
}
```

---

## 6.2 Get Chat History

```http
GET /chat/conversations/{conversationId}/messages?page=0&size=50
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "messageId": "msg-001",
      "senderId": "user-550e8400",
      "senderType": "USER",
      "message": "Can you do 500 guests for May 20?",
      "messageType": "TEXT",
      "timestamp": "2026-02-24T10:30:00.000Z"
    },
    {
      "messageId": "msg-002",
      "senderId": "vendor-user-550e8400",
      "senderType": "VENDOR",
      "message": "Yes! We can handle 500 guests. Our base price is ₹500/plate for a standard menu.",
      "messageType": "TEXT",
      "timestamp": "2026-02-24T10:45:00.000Z"
    }
  ]
}
```

---

## 6.3 Send Message via WebSocket

```
WebSocket URL: ws://localhost:8080/api/v1/ws
Authorization: Bearer {accessToken}

// Send to: /app/chat.sendMessage
{
  "conversationId": "conv-77665-88990",
  "message": "I'll include complimentary desserts for orders above ₹1,50,000.",
  "messageType": "TEXT"
}

// Subscribe to live messages: /topic/conversations.conv-77665-88990
```

---

# 7. Reviews & Ratings

## 7.1 Get Reviews for My Business

```http
GET /reviews/vendor/{vendorId}?page=0&size=20
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "reviewId": "rev-11223-44556",
      "orderId": "order-54321-12345",
      "userName": "John Doe",
      "rating": 5,
      "foodQualityRating": 5,
      "serviceQualityRating": 4,
      "hygieneRating": 5,
      "reviewText": "Excellent food quality and very professional service...",
      "images": ["http://localhost:8080/uploads/images/wedding-food-1.jpg"],
      "helpfulCount": 8,
      "vendorResponse": null,
      "status": "APPROVED",
      "createdAt": "2026-02-26T10:00:00.000Z"
    }
  ],
  "pageInfo": { "totalElements": 312, "totalPages": 16 }
}
```

---

## 7.2 Reply to a Customer Review

**Use:** Publicly respond to a customer review.

```http
POST /reviews/{reviewId}/vendor-response
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "responseText": "Thank you so much John for the wonderful feedback! We are delighted that you enjoyed our food and service. We look forward to serving you at your next event!"
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "reviewId": "rev-11223-44556",
    "vendorResponse": {
      "responseText": "Thank you so much John for the wonderful feedback!...",
      "respondedAt": "2026-02-26T12:00:00.000Z"
    }
  }
}
```

---

# 8. Analytics & Dashboard

## 8.1 Get My Vendor Dashboard

**Use:** See your business performance metrics.

```http
GET /analytics/vendor/{vendorId}/dashboard
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "vendorId": "vendor-12345-67890",
    "vendorName": "Spice Garden Catering",
    "metrics": {
      "totalOrders": 600,
      "completedOrders": 596,
      "pendingOrders": 4,
      "cancelledOrders": 0,
      "totalRevenue": 900000,
      "pendingPayouts": 50000,
      "thisMonthRevenue": 120000,
      "currency": "INR"
    },
    "bidMetrics": {
      "totalBidsSubmitted": 850,
      "acceptedBids": 600,
      "pendingBids": 18,
      "acceptanceRate": 70.6,
      "averageBidAmount": 35000
    },
    "performance": {
      "averageRating": 4.7,
      "totalReviews": 312,
      "responseRate": 98.5,
      "onTimeDeliveryRate": 99.3,
      "repeatCustomers": 120
    },
    "recentOrders": [
      {
        "orderId": "order-54321-12345",
        "customerName": "John Doe",
        "eventDate": "2026-05-20",
        "status": "CONFIRMED",
        "totalAmount": 198000
      }
    ],
    "upcomingEvents": [
      {
        "orderId": "order-54321-12345",
        "eventName": "Priya & Rahul Wedding",
        "eventDate": "2026-05-20",
        "numberOfGuests": 500,
        "venueCity": "Bangalore"
      }
    ]
  }
}
```

---

## 8.2 Get Revenue Analytics

```http
GET /analytics/vendor/{vendorId}/revenue?period=monthly
Authorization: Bearer {accessToken}
```

### Query Parameters
| Param | Values | Description |
|-------|--------|-------------|
| `period` | `daily`, `weekly`, `monthly`, `yearly` | Analytics period |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "period": "monthly",
    "vendorId": "vendor-12345-67890",
    "totalRevenue": 120000,
    "completedOrders": 15,
    "averageOrderValue": 8000,
    "currency": "INR",
    "revenueByMonth": [
      { "month": "January 2026", "revenue": 95000, "orders": 12 },
      { "month": "February 2026", "revenue": 120000, "orders": 15 }
    ]
  }
}
```

---

# 9. File Uploads

## 9.1 Upload Business Logo

**Use:** Upload vendor logo. Get URL, then use it in `PUT /vendors/{vendorId}`.

```http
POST /uploads/image
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data
```

### Form Data
```
file: <binary image data>   (max 5MB, JPEG/PNG/WebP)
entityType: VENDOR_LOGO
entityId: vendor-12345-67890
```

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "fileId": "file-logo-001",
    "fileName": "logo-1708773045.png",
    "fileType": "IMAGE",
    "fileSize": 145678,
    "fileUrl": "http://localhost:8080/uploads/images/logo-1708773045.png",
    "entityType": "VENDOR_LOGO",
    "entityId": "vendor-12345-67890",
    "createdAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 9.2 Upload Business Document

**Use:** Upload license/certificate. Get URL, then include in vendor registration or update.

```http
POST /uploads/document
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data
```

### Form Data
```
file: <binary PDF data>   (max 10MB, PDF/DOC/DOCX/JPEG/PNG)
entityType: VENDOR_DOCUMENT
entityId: vendor-12345-67890
```

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "fileId": "file-doc-001",
    "fileName": "fssai-2026-1708773045.pdf",
    "fileType": "DOCUMENT",
    "contentType": "application/pdf",
    "fileSize": 512345,
    "fileUrl": "http://localhost:8080/uploads/documents/fssai-2026-1708773045.pdf",
    "entityType": "VENDOR_DOCUMENT",
    "entityId": "vendor-12345-67890"
  }
}
```

---

## 9.3 Upload Menu Item Photo

```http
POST /uploads/image
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data
```

### Form Data
```
file: <binary image>
entityType: MENU_ITEM
entityId: vitem-001
```

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "fileId": "file-menu-001",
    "fileUrl": "http://localhost:8080/uploads/images/menu-1708773045.jpg",
    "entityType": "MENU_ITEM"
  }
}
```

---

# 10. Notifications

## 10.1 Get My Notifications

```http
GET /notifications?page=0&size=20
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "notificationId": "notif-001",
      "title": "New Bid Request",
      "message": "A new wedding bid request for 500 guests in Bangalore has been posted.",
      "type": "NEW_BID_REQUEST",
      "isRead": false,
      "referenceId": "breq-88990-77665",
      "referenceType": "BID_REQUEST",
      "createdAt": "2026-02-24T10:30:45.123Z"
    },
    {
      "notificationId": "notif-002",
      "title": "Bid Accepted",
      "message": "Your bid for Priya & Rahul Wedding has been accepted! Token payment received.",
      "type": "BID_ACCEPTED",
      "isRead": false,
      "referenceId": "bid-vendor-spice-001",
      "referenceType": "BID",
      "createdAt": "2026-02-25T11:10:00.000Z"
    },
    {
      "notificationId": "notif-003",
      "title": "New Review",
      "message": "John Doe left a 5-star review for your catering service.",
      "type": "NEW_REVIEW",
      "isRead": true,
      "referenceId": "rev-11223-44556",
      "referenceType": "REVIEW",
      "createdAt": "2026-02-26T10:05:00.000Z"
    }
  ]
}
```

---

## 10.2 Mark Notification as Read

```http
PATCH /notifications/{notificationId}/read
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": { "notificationId": "notif-001", "isRead": true }
}
```

---

# 📌 Vendor API — Complete Error Code Reference

| Error Code | HTTP Status | Description |
|------------|-------------|-------------|
| `VENDOR_NOT_APPROVED` | 401 | Vendor not approved by admin yet |
| `VENDOR_EXISTS` | 409 | Vendor profile already exists for user |
| `EMAIL_EXISTS` | 409 | Business email already registered |
| `INVALID_COUNTRY` | 400 | Country must be USA or INDIA |
| `BID_REQUEST_NOT_FOUND` | 404 | Bid request doesn't exist |
| `BID_ALREADY_SUBMITTED` | 409 | Already submitted bid for this request |
| `BID_NOT_OWNED` | 403 | This bid belongs to another vendor |
| `MAX_REVISIONS_REACHED` | 400 | Maximum 5 bid revisions allowed |
| `BID_EXPIRED` | 400 | Bid request has expired |
| `ORDER_NOT_FOUND` | 404 | Order doesn't exist |
| `VENDOR_ITEM_NOT_FOUND` | 404 | Vendor menu item not found |
| `MASTER_ITEM_NOT_FOUND` | 404 | Master menu item not found |
| `ITEM_ALREADY_EXISTS` | 409 | This master item already added to your menu |
| `REVIEW_NOT_OWNED` | 403 | Cannot reply to another vendor's review |
| `RESPONSE_EXISTS` | 400 | Already responded to this review |
| `VALIDATION_ERROR` | 422 | Field validation failed |
| `FORBIDDEN` | 403 | Role not permitted for this action |

---

# 📌 Vendor Business Flow Summary

```
Step 1: Register user account (userType: VENDOR)
     ↓
Step 2: Upload required documents (File Upload API)
     ↓
Step 3: Create vendor business profile (POST /vendors)
     ↓
Step 4: Wait for admin approval (email notification sent)
     ↓
Step 5: Login after approval (POST /auth/login)
     ↓
Step 6: Add menu items (POST /menu/vendor-items)
     ↓
Step 7: Browse active bid requests (GET /bids/active)
     ↓
Step 8: Submit competitive bids (POST /bids/requests/{id}/submit-bid)
     ↓
Step 9: Bid gets accepted → Receive token payment notification
     ↓
Step 10: Manage order status (PATCH /orders/{id}/status)
     ↓
Step 11: Deliver food → Mark as DELIVERED
     ↓
Step 12: Order auto-completed after 24hrs or customer confirms
     ↓
Step 13: Receive payment (balance amount transferred to bank account)
     ↓
Step 14: View analytics (GET /analytics/vendor/{id}/dashboard)
```

---

# 📌 Important Notes for Vendors

1. **Approval Required:** You cannot login until your profile is approved by admin.
2. **Documents:** At least 2 documents required during registration (e.g., FSSAI + GST).
3. **Bid Revisions:** Maximum 5 revisions per bid. Choose prices carefully.
4. **Bid Validity:** Set `validityPeriodHours` correctly — expired bids are auto-removed.
5. **Token Payment:** After bid is accepted, customer pays 25% token. You get notified immediately.
6. **Cancellation Policy:** If customer cancels, token refund is based on days before event.
7. **Menu Items:** Add items from master catalog. Set competitive prices to win more bids.
8. **Response Time:** Respond to bid requests quickly — low response time improves ranking.
9. **Rating Impact:** Average rating affects your position in vendor search results.
10. **File Uploads:** Upload files first via `/uploads/image` or `/uploads/document`, then use URLs.

---

*Vendor API Documentation — Bidzaro Catering Platform v1.0.0 | Updated: February 24, 2026*

