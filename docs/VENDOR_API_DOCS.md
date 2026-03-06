# 🏪 VENDOR API Documentation
**Bidzaro Catering Platform** | Base URL: `http://localhost:8080/api/v1`

> 🔒 = Requires `Authorization: Bearer <accessToken>` with `userType: VENDOR`
> ✅ = Required field | ⬜ = Optional field

---

## 📋 Table of Contents
1. [Enums Reference](#-enums-reference)
2. [Authentication](#-authentication)
3. [Vendor Profile](#-vendor-profile)
4. [Menu Management](#-menu-management)
5. [Bid Marketplace](#-bid-marketplace)
6. [Order Management](#-order-management)
7. [Reviews](#-reviews)
8. [Analytics Dashboard](#-analytics-dashboard)
9. [Notifications](#-notifications)
10. [Chat](#-chat)
11. [File Upload](#-file-upload)
12. [Support Tickets](#-support-tickets)

---

## 🔢 Enums Reference

### VendorStatus
| Value | Description |
|-------|-------------|
| `PENDING_APPROVAL` | Profile submitted, awaiting admin review |
| `ACTIVE` | Approved and can receive bids |
| `SUSPENDED` | Temporarily suspended by admin |
| `REJECTED` | Application rejected |
| `DELETED` | Soft deleted |

### ApprovalStatus
| Value | Description |
|-------|-------------|
| `PENDING` | Not yet reviewed |
| `APPROVED` | Admin approved |
| `REJECTED` | Admin rejected |
| `UNDER_REVIEW` | Currently being reviewed |

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

### BidStatus (VendorBid)
| Value | Description |
|-------|-------------|
| `SUBMITTED` | Bid submitted and visible to user |
| `REVISED` | Bid revised (up to 5 times) |
| `ACCEPTED` | User accepted this bid |
| `REJECTED` | User rejected or accepted another |
| `EXPIRED` | Validity period expired |
| `WITHDRAWN` | Vendor withdrew the bid |

### VendorOrderStatus
| Value |
|-------|
| `PENDING` |
| `ACCEPTED` |
| `CONFIRMED` |
| `IN_PREPARATION` |
| `READY` |
| `DELIVERED` |
| `COMPLETED` |

### DeliveryStatus
| Value |
|-------|
| `PENDING` |
| `ON_THE_WAY` |
| `DELIVERED` |

### OrderStatus (Full order)
| Value | Description |
|-------|-------------|
| `PENDING_TOKEN_PAYMENT` | Awaiting user's token payment |
| `CONFIRMED` | Token paid, order confirmed |
| `IN_PREPARATION` | Food being prepared |
| `READY_FOR_DELIVERY` | Ready to deliver |
| `DELIVERING` | Out for delivery |
| `DELIVERED` | Delivered to venue |
| `COMPLETED` | Event completed |
| `CANCELLED` | Cancelled |

### VendorMenuItem Status
| Value |
|-------|
| `ACTIVE` |
| `INACTIVE` |
| `OUT_OF_STOCK` |

### FoodType
| Value |
|-------|
| `VEG` |
| `NON_VEG` |
| `VEGAN` |
| `EGG` |

### SpiceLevel
| Value |
|-------|
| `MILD` |
| `MEDIUM` |
| `HOT` |
| `EXTRA_HOT` |

### ConversationType
| Value | Description |
|-------|-------------|
| `USER_VENDOR` | Chat with customer |
| `VENDOR_SUPPORT` | Chat with support agent |

### MessageType
| Value |
|-------|
| `TEXT` |
| `IMAGE` |
| `FILE` |
| `SYSTEM` |

### PaymentMethod
| Value |
|-------|
| `CARD` |
| `UPI` |
| `NET_BANKING` |
| `WALLET` |

---

## 🔐 Authentication

Vendors use the same auth endpoints. Register with `userType: VENDOR`.

### Register as Vendor
`POST /auth/register`

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `email` | string | ✅ | Valid email |
| `phone` | string | ✅ | E.164 e.g. `+919876543210` |
| `password` | string | ✅ | Min 8 chars, complexity required |
| `firstName` | string | ✅ | 1–50 chars |
| `lastName` | string | ⬜ | Max 50 chars |
| `country` | string | ⬜ | `INDIA` or `USA` |
| `userType` | string | ✅ | Must be `VENDOR` |
| `fcmToken` | string | ⬜ | Firebase device token |

**Request:**
```json
{
  "email": "ravi@royalcatering.com",
  "phone": "+919876543210",
  "password": "SecurePass@123",
  "firstName": "Ravi",
  "lastName": "Kumar",
  "country": "INDIA",
  "userType": "VENDOR",
  "fcmToken": "firebase-device-token"
}
```

**Response `201`:**
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
      "email": "ravi@royalcatering.com",
      "phone": "+919876543210",
      "userType": "VENDOR",
      "firstName": "Ravi",
      "lastName": "Kumar",
      "fullName": "Ravi Kumar",
      "profilePictureUrl": null,
      "emailVerified": false,
      "phoneVerified": false,
      "preferredLanguage": "en",
      "preferredCurrency": "INR",
      "country": "INDIA",
      "status": "PENDING_VERIFICATION",
      "createdAt": "2026-03-06T10:00:00Z"
    }
  }
}
```
> ⏰ After registration you receive reminders to complete your vendor profile: **5 min → 1h (×3) → 24h (×3)** then account deleted if no profile created.

---

### Login
`POST /auth/login`

**Request:**
```json
{
  "identifier": "ravi@royalcatering.com",
  "password": "SecurePass@123",
  "fcmToken": "firebase-device-token"
}
```
**Response `200`:** Same structure as register response. `vendorId` will be populated if vendor profile exists.

---

## 🏪 Vendor Profile

### Create Vendor Profile (Onboarding)
`POST /vendors` 🔒

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `businessName` | string | ✅ | 2–100 chars |
| `businessEmail` | string | ✅ | Valid email |
| `businessPhone` | string | ✅ | Valid phone |
| `businessType` | string | ✅ | `CATERING`, `RESTAURANT`, `CLOUD_KITCHEN`, `HOME_CHEF`, `BAKERY` |
| `businessRegistrationNumber` | string | ⬜ | |
| `taxId` | string | ⬜ | GST/Tax ID |
| `description` | string | ⬜ | Business description |
| `establishedYear` | integer | ⬜ | Year established |
| `cuisinesOffered` | array | ⬜ | e.g. `["North Indian", "Chinese"]` |
| `specialties` | array | ⬜ | e.g. `["Biryani", "Buffets"]` |
| `country` | string | ✅ | `INDIA` or `USA` |
| `businessAddress` | object | ⬜ | |
| `businessAddress.streetAddress` | string | ✅ (if address given) | |
| `businessAddress.city` | string | ✅ (if address given) | |
| `businessAddress.state` | string | ✅ (if address given) | |
| `businessAddress.postalCode` | string | ✅ (if address given) | |
| `businessAddress.latitude` | double | ⬜ | For geospatial matching |
| `businessAddress.longitude` | double | ⬜ | For geospatial matching |
| `ownerInfo` | object | ⬜ | |
| `ownerInfo.firstName` | string | ✅ (if ownerInfo given) | |
| `ownerInfo.lastName` | string | ✅ (if ownerInfo given) | |
| `ownerInfo.phone` | string | ⬜ | |
| `ownerInfo.email` | string | ⬜ | |
| `ownerInfo.idProofType` | string | ⬜ | e.g. `AADHAAR`, `PAN` |
| `ownerInfo.idProofNumber` | string | ⬜ | |
| `serviceAreas` | array | ⬜ | |
| `serviceAreas[].city` | string | ⬜ | |
| `serviceAreas[].state` | string | ⬜ | |
| `serviceAreas[].radiusKm` | integer | ⬜ | Service radius |
| `capacity` | object | ⬜ | |
| `capacity.minGuests` | integer | ⬜ | Min 10 |
| `capacity.maxGuests` | integer | ⬜ | Max 10000 |
| `capacity.concurrentEvents` | integer | ⬜ | |
| `pricing` | object | ⬜ | |
| `pricing.currency` | string | ⬜ | |
| `pricing.startingPricePerPlate` | decimal | ⬜ | Must be positive |
| `pricing.averagePricePerPlate` | decimal | ⬜ | |
| `documents` | array | ⬜ | |
| `documents[].documentType` | string | ✅ (if doc given) | e.g. `FSSAI_LICENSE`, `GST_CERTIFICATE` |
| `documents[].documentName` | string | ✅ (if doc given) | |
| `documents[].documentUrl` | string | ✅ (if doc given) | Uploaded file URL |
| `documents[].documentNumber` | string | ⬜ | |
| `documents[].issueDate` | datetime | ⬜ | ISO-8601 |
| `documents[].expiryDate` | datetime | ⬜ | |

**Request:**
```json
{
  "businessName": "Royal Catering Co.",
  "businessEmail": "info@royalcatering.com",
  "businessPhone": "+919876543211",
  "businessType": "CATERING",
  "businessRegistrationNumber": "REG123456",
  "taxId": "36AABCR1234F1ZV",
  "description": "Premium catering for weddings, corporate events and parties",
  "establishedYear": 2015,
  "cuisinesOffered": ["North Indian", "Mughlai", "Chinese"],
  "specialties": ["Biryani", "Dal Makhani", "Live Counters"],
  "country": "INDIA",
  "businessAddress": {
    "streetAddress": "45, Road No 10, Banjara Hills",
    "city": "Hyderabad",
    "state": "Telangana",
    "postalCode": "500034",
    "latitude": 17.4234,
    "longitude": 78.4481
  },
  "ownerInfo": {
    "firstName": "Ravi",
    "lastName": "Kumar",
    "phone": "+919876543210",
    "email": "ravi@royalcatering.com",
    "idProofType": "AADHAAR",
    "idProofNumber": "1234-5678-9012"
  },
  "serviceAreas": [
    { "city": "Hyderabad", "state": "Telangana", "radiusKm": 50 },
    { "city": "Secunderabad", "state": "Telangana", "radiusKm": 30 }
  ],
  "capacity": {
    "minGuests": 50,
    "maxGuests": 2000,
    "concurrentEvents": 3
  },
  "pricing": {
    "currency": "INR",
    "startingPricePerPlate": 250.00,
    "averagePricePerPlate": 450.00
  },
  "documents": [
    {
      "documentType": "FSSAI_LICENSE",
      "documentName": "FSSAI License 2026",
      "documentUrl": "http://localhost:8080/uploads/documents/fssai.pdf",
      "documentNumber": "FSSAI123456",
      "issueDate": "2025-01-01T00:00:00Z",
      "expiryDate": "2027-01-01T00:00:00Z"
    }
  ]
}
```

**Response `201`:** Full `VendorResponse` object:
```json
{
  "success": true,
  "data": {
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
    "description": "Premium catering for weddings, corporate events and parties",
    "establishedYear": 2015,
    "cuisinesOffered": ["North Indian", "Mughlai", "Chinese"],
    "specialties": ["Biryani", "Dal Makhani", "Live Counters"],
    "businessAddress": {
      "streetAddress": "45, Road No 10, Banjara Hills",
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
    "serviceAreas": [
      { "city": "Hyderabad", "state": "Telangana", "radiusKm": 50 }
    ],
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
}
```

---

### Get My Vendor Profile
`GET /vendors/me` 🔒
**Response `200`:** Full `VendorResponse` object (same as above).

---

### Update Vendor Profile
`PUT /vendors/me` 🔒

All fields same as create, all optional. Only sends fields you want to update.

**Request (partial update example):**
```json
{
  "description": "Updated description – now serving pan-Asian cuisine too",
  "cuisinesOffered": ["North Indian", "Mughlai", "Chinese", "Thai"],
  "pricing": {
    "currency": "INR",
    "startingPricePerPlate": 300.00,
    "averagePricePerPlate": 500.00
  }
}
```
**Response `200`:** Updated `VendorResponse` object.

---

### Update Logo
`PUT /vendors/me/logo` 🔒

**Request:** `{ "logoUrl": "http://localhost:8080/uploads/images/logo-new.jpg" }`
**Response `200`:** Updated `VendorResponse` object.

---

### Update Banner
`PUT /vendors/me/banner` 🔒

**Request:** `{ "bannerUrl": "http://localhost:8080/uploads/images/banner-new.jpg" }`
**Response `200`:** Updated `VendorResponse` object.

---

## 🍽️ Menu Management

### Get My Menu Items
`GET /menu/vendor/my?page=0&size=20` 🔒

**Response `200`:** Paginated list of `VendorMenuItemResponse` objects:
```json
{
  "data": [
    {
      "vendorItemId": "uuid",
      "vendorId": "uuid",
      "masterItemId": "uuid",
      "customName": "Special Hyderabadi Dum Biryani",
      "customDescription": "Slow-cooked aromatic rice with tender chicken",
      "pricing": {
        "currency": "INR",
        "pricePerPlate": 320.00,
        "minimumOrderQuantity": 10,
        "discountPercentage": 5.00,
        "discountedPrice": 304.00
      },
      "availability": {
        "isAvailable": true,
        "unavailableReason": null,
        "unavailableUntil": null,
        "advanceNoticeHours": 24,
        "maxDailyCapacity": 500
      },
      "preparationTimeMinutes": 120,
      "customizationOptions": [
        {
          "optionName": "Spice Level",
          "choices": ["Mild", "Medium", "Hot"],
          "additionalCost": 0.00,
          "isRequired": false
        }
      ],
      "stats": {
        "totalOrders": 120,
        "averageRating": 4.50,
        "totalReviews": 45
      },
      "status": "ACTIVE",
      "createdAt": "2026-01-10T10:00:00Z"
    }
  ],
  "pageInfo": { "page": 0, "size": 20, "totalElements": 12 }
}
```

---

### Add Menu Item
`POST /menu/vendor` 🔒

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `masterItemId` | string | ✅ | Must exist in platform master menu |
| `customName` | string | ⬜ | Override item name |
| `customDescription` | string | ⬜ | Override description |
| `pricePerPlate` | decimal | ✅ | Must be > 0 |
| `minimumOrderQuantity` | integer | ⬜ | Min 1 |
| `discountPercentage` | decimal | ⬜ | 0–100 |
| `isAvailable` | boolean | ⬜ | Default true |
| `unavailableReason` | string | ⬜ | |
| `advanceNoticeHours` | integer | ⬜ | Min 0 |
| `maxDailyCapacity` | integer | ⬜ | Min 1 |
| `preparationTimeMinutes` | integer | ⬜ | Min 1 |
| `customizationOptions` | array | ⬜ | |
| `customizationOptions[].optionName` | string | ✅ (if option given) | |
| `customizationOptions[].choices` | array | ✅ (if option given) | Non-empty list |
| `customizationOptions[].additionalCost` | decimal | ⬜ | Min 0 |
| `customizationOptions[].isRequired` | boolean | ⬜ | |

**Request:**
```json
{
  "masterItemId": "uuid",
  "customName": "Special Hyderabadi Dum Biryani",
  "customDescription": "Slow-cooked aromatic rice with tender chicken",
  "pricePerPlate": 320.00,
  "minimumOrderQuantity": 10,
  "discountPercentage": 5.00,
  "isAvailable": true,
  "advanceNoticeHours": 24,
  "maxDailyCapacity": 500,
  "preparationTimeMinutes": 120,
  "customizationOptions": [
    {
      "optionName": "Spice Level",
      "choices": ["Mild", "Medium", "Hot"],
      "additionalCost": 0.00,
      "isRequired": false
    },
    {
      "optionName": "Meat Type",
      "choices": ["Chicken", "Mutton"],
      "additionalCost": 50.00,
      "isRequired": true
    }
  ]
}
```

**Response `201`:** Full `VendorMenuItemResponse` object.

---

### Update Menu Item
`PUT /menu/vendor/{vendorItemId}` 🔒
Same body as Add Menu Item, all fields optional.
**Response `200`:** Updated `VendorMenuItemResponse` object.

---

### Toggle Item Availability
`PUT /menu/vendor/{vendorItemId}/availability` 🔒

| Field | Type | Required |
|-------|------|----------|
| `isAvailable` | boolean | ✅ |
| `unavailableReason` | string | ⬜ |

**Request:** `{ "isAvailable": false, "unavailableReason": "Out of stock until next week" }`
**Response `200`:** Updated `VendorMenuItemResponse` object.

---

### Delete Menu Item
`DELETE /menu/vendor/{vendorItemId}` 🔒
**Response `200`:** `{ "success": true, "message": "Menu item removed" }`

---

## 📋 Bid Marketplace

### Get Available Bid Requests
`GET /bids/requests/available?page=0&size=20` 🔒

> Returns bid requests in vendor's service area that haven't reached bid limit.

**Response `200`:** Paginated list of `BidRequestResponse`:
```json
{
  "data": [
    {
      "bidRequestId": "uuid",
      "userId": "uuid",
      "eventDetails": {
        "eventType": "WEDDING",
        "eventName": "Sharma Wedding Reception",
        "eventDate": "2026-04-15T00:00:00",
        "eventStartTime": "18:00",
        "eventEndTime": "23:00",
        "numberOfGuests": 300,
        "venueAddress": {
          "streetAddress": "Taj Banjara Hotel",
          "city": "Hyderabad",
          "state": "Telangana",
          "postalCode": "500034",
          "country": "India"
        }
      },
      "menuItems": [
        { "vendorItemId": "uuid", "masterItemId": "uuid", "itemName": "Chicken Biryani", "quantity": 300 }
      ],
      "additionalRequirements": {
        "serviceStaffNeeded": true,
        "numberOfStaff": 10,
        "decorationNeeded": false,
        "liveCounters": ["Biryani Station"],
        "specialInstructions": "Halal food preferred"
      },
      "budget": {
        "currency": "INR",
        "estimatedBudget": 150000.00,
        "budgetRange": "100000-200000"
      },
      "targetedVendors": [],
      "competitivePeriod": {
        "startTime": "2026-03-06T10:00:00Z",
        "endTime": "2026-03-09T10:00:00Z",
        "status": "ACTIVE"
      },
      "acceptedBid": null,
      "status": "ACTIVE",
      "totalBidsReceived": 2,
      "lowestBidAmount": 145000.00,
      "createdAt": "2026-03-06T10:00:00Z",
      "expiresAt": "2026-03-09T10:00:00Z"
    }
  ]
}
```

---

### Submit a Bid
`POST /bids/{bidRequestId}/submit` 🔒

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `quotedPrice` | object | ✅ | |
| `quotedPrice.currency` | string | ⬜ | Default from vendor |
| `quotedPrice.subtotal` | decimal | ✅ | Must be positive |
| `quotedPrice.serviceCharge` | decimal | ⬜ | |
| `quotedPrice.taxPercentage` | decimal | ⬜ | |
| `quotedPrice.taxAmount` | decimal | ⬜ | |
| `quotedPrice.totalAmount` | decimal | ✅ | Must be positive |
| `itemizedPricing` | array | ⬜ | Per-item breakdown |
| `itemizedPricing[].vendorItemId` | string | ⬜ | |
| `itemizedPricing[].itemName` | string | ⬜ | |
| `itemizedPricing[].quantity` | integer | ⬜ | Min 1 |
| `itemizedPricing[].pricePerPlate` | decimal | ⬜ | Min 0 |
| `itemizedPricing[].totalPrice` | decimal | ⬜ | |
| `deliveryDetails` | object | ⬜ | |
| `deliveryDetails.estimatedSetupTime` | string | ⬜ | e.g. `16:00` |
| `deliveryDetails.foodReadyTime` | string | ⬜ | |
| `deliveryDetails.cleanupTime` | string | ⬜ | |
| `staffProvided` | object | ⬜ | |
| `staffProvided.chefs` | integer | ⬜ | |
| `staffProvided.servers` | integer | ⬜ | |
| `staffProvided.cleaners` | integer | ⬜ | |
| `termsAndConditions` | string | ⬜ | |
| `validityPeriodHours` | integer | ⬜ | Default 168 (7 days) |
| `advancePercentage` | decimal | ⬜ | 0–100 |

**Request:**
```json
{
  "quotedPrice": {
    "currency": "INR",
    "subtotal": 120000.00,
    "serviceCharge": 6000.00,
    "taxPercentage": 18.00,
    "taxAmount": 22680.00,
    "totalAmount": 148680.00
  },
  "itemizedPricing": [
    {
      "vendorItemId": "uuid",
      "itemName": "Chicken Biryani",
      "quantity": 300,
      "pricePerPlate": 400.00,
      "totalPrice": 120000.00
    }
  ],
  "deliveryDetails": {
    "estimatedSetupTime": "16:00",
    "foodReadyTime": "17:30",
    "cleanupTime": "00:00"
  },
  "staffProvided": {
    "chefs": 4,
    "servers": 10,
    "cleaners": 2
  },
  "termsAndConditions": "25% advance required at confirmation. Full payment 7 days before event.",
  "validityPeriodHours": 168,
  "advancePercentage": 25.00
}
```

**Response `201`:** Full `VendorBidResponse`:
```json
{
  "data": {
    "bidId": "uuid",
    "bidRequestId": "uuid",
    "vendorId": "uuid",
    "vendorName": "Royal Catering Co.",
    "eventDetails": null,
    "quotedPrice": {
      "currency": "INR",
      "subtotal": 120000.00,
      "serviceCharge": 6000.00,
      "taxPercentage": 18.00,
      "taxAmount": 22680.00,
      "totalAmount": 148680.00
    },
    "itemizedPricing": [
      {
        "vendorItemId": "uuid",
        "itemName": "Chicken Biryani",
        "quantity": 300,
        "pricePerPlate": 400.00,
        "totalPrice": 120000.00
      }
    ],
    "deliveryDetails": {
      "estimatedSetupTime": "16:00",
      "foodReadyTime": "17:30",
      "cleanupTime": "00:00"
    },
    "staffProvided": { "chefs": 4, "servers": 10, "cleaners": 2 },
    "termsAndConditions": "25% advance required at confirmation.",
    "validityPeriodHours": 168,
    "advancePercentage": 25.00,
    "requiredAdvanceAmount": 37170.00,
    "revisionCount": 0,
    "status": "SUBMITTED",
    "isLowest": true,
    "rank": 1,
    "submittedAt": "2026-03-06T12:00:00Z",
    "expiresAt": "2026-03-13T12:00:00Z"
  }
}
```

---

### Get My Submitted Bids
`GET /bids/my?page=0&size=20` 🔒

---

### Get Bid by ID
`GET /bids/{bidId}` 🔒
**Response `200`:** Single `VendorBidResponse` object.

---

### Revise a Bid
`PUT /bids/{bidId}` 🔒

Same body as Submit Bid. Maximum **5 revisions** allowed.

**Response `200`:** Updated `VendorBidResponse` with incremented `revisionCount`.

---

### Withdraw a Bid
`DELETE /bids/{bidId}` 🔒
**Response `200`:** `{ "success": true, "message": "Bid withdrawn successfully" }`

---

## 📦 Order Management

### Get My Orders
`GET /orders/vendor/my?page=0&size=20` 🔒

**Response `200`:** Paginated list of `OrderResponse` objects (same as client order response).

---

### Get Order by ID
`GET /orders/{orderId}` 🔒
**Response `200`:** Full `OrderResponse` object including all `vendorOrders`, `pricing`, `paymentDetails`, etc.

---

### Update Order Status
`PUT /orders/{orderId}/status` 🔒

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `status` | string | ✅ | Valid vendor-controlled statuses |

**Valid status transitions for vendor:**
```
CONFIRMED → IN_PREPARATION → READY_FOR_DELIVERY → DELIVERING → DELIVERED
```

**Request:**
```json
{ "status": "IN_PREPARATION" }
```

**Response `200`:** Updated `OrderResponse` with new status and timestamps.

> ⚠️ Only the assigned vendor can update their order status. Other status changes (`CONFIRMED`, `COMPLETED`, `CANCELLED`) are system or user controlled.

---

### Get Upcoming Orders (Next 30 Days)
`GET /orders/vendor/upcoming?page=0&size=20` 🔒

---

## ⭐ Reviews

### Get My Reviews
`GET /reviews/vendor/my?page=0&size=10` 🔒

**Response `200`:**
```json
{
  "data": [
    {
      "reviewId": "uuid",
      "orderId": "uuid",
      "vendorId": "uuid",
      "userId": "uuid",
      "rating": 5,
      "foodQualityRating": 5,
      "serviceQualityRating": 4,
      "hygieneRating": 5,
      "valueForMoneyRating": 4,
      "punctualityRating": 5,
      "reviewText": "Excellent food and service!",
      "images": ["http://localhost:8080/uploads/images/review1.jpg"],
      "vendorResponse": null,
      "helpfulCount": 12,
      "reportedCount": 0,
      "status": "APPROVED",
      "moderationNotes": null,
      "createdAt": "2026-03-01T10:00:00Z",
      "updatedAt": "2026-03-01T10:00:00Z"
    }
  ]
}
```

---

### Respond to a Review
`POST /reviews/{reviewId}/respond` 🔒

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `responseText` | string | ✅ | Max 1000 chars |

**Request:**
```json
{
  "responseText": "Thank you for your kind words! We are delighted you enjoyed the biryani. We look forward to serving you again!"
}
```

**Response `200`:** Updated `ReviewResponse` with `vendorResponse`:
```json
{
  "data": {
    "reviewId": "uuid",
    "vendorResponse": {
      "responseText": "Thank you for your kind words!...",
      "respondedAt": "2026-03-06T10:00:00Z"
    }
  }
}
```

---

## 📊 Analytics Dashboard

### Get Vendor Dashboard
`GET /analytics/vendor/dashboard` 🔒

**Response `200`:**
```json
{
  "data": {
    "revenueMetrics": {
      "totalRevenue": 450000.00,
      "currentMonthRevenue": 120000.00,
      "lastMonthRevenue": 98000.00,
      "revenueGrowth": 22.44,
      "pendingPayouts": 15000.00
    },
    "orderMetrics": {
      "totalOrders": 48,
      "completedOrders": 42,
      "cancelledOrders": 3,
      "activeOrders": 3,
      "completionRate": 87.50,
      "cancellationRate": 6.25
    },
    "bidMetrics": {
      "totalBidsSubmitted": 85,
      "bidsAccepted": 48,
      "bidsRejected": 20,
      "bidsPending": 17,
      "bidSuccessRate": 56.47
    },
    "ratingMetrics": {
      "averageRating": 4.50,
      "totalReviews": 42,
      "fiveStarCount": 30,
      "fourStarCount": 8,
      "threeStarCount": 3,
      "twoStarCount": 1,
      "oneStarCount": 0
    },
    "upcomingEvents": [
      {
        "orderId": "uuid",
        "eventType": "WEDDING",
        "eventDate": "2026-04-15",
        "numberOfGuests": 300,
        "totalAmount": 148680.00,
        "status": "CONFIRMED"
      }
    ]
  }
}
```

---

## 🔔 Notifications

### Get My Notifications
`GET /notifications?page=0&size=20` 🔒

**Response `200`:**
```json
{
  "data": [
    {
      "notificationId": "uuid",
      "userId": "uuid",
      "title": "New Bid Request!",
      "message": "A new bid request for 300 guests wedding in Hyderabad matches your service area.",
      "type": "NEW_BID_REQUEST",
      "channel": "IN_APP",
      "isRead": false,
      "data": { "bidRequestId": "uuid" },
      "createdAt": "2026-03-06T10:00:00Z"
    },
    {
      "notificationId": "uuid",
      "userId": "uuid",
      "title": "Bid Accepted!",
      "message": "Your bid for wedding event on April 15 has been accepted.",
      "type": "BID_ACCEPTED",
      "channel": "IN_APP",
      "isRead": false,
      "data": { "bidId": "uuid", "bidRequestId": "uuid" },
      "createdAt": "2026-03-06T14:00:00Z"
    }
  ]
}
```

---

### Get Unread Count
`GET /notifications/unread/count` 🔒
**Response `200`:** `{ "data": { "count": 5 } }`

---

### Mark as Read
`PUT /notifications/{notificationId}/read` 🔒
**Response `200`:** `{ "success": true }`

---

### Mark All as Read
`PUT /notifications/read-all` 🔒
**Response `200`:** `{ "success": true }`

---

## 💬 Chat

### Get My Conversations
`GET /chat/conversations?page=0&size=20` 🔒

**Response `200`:** Paginated list of `ConversationResponse`:
```json
{
  "data": [
    {
      "conversationId": "uuid",
      "participants": [
        { "userId": "user-uuid", "userType": "USER", "name": "Rahul Sharma", "profilePictureUrl": null },
        { "userId": "vendor-user-uuid", "userType": "VENDOR", "name": "Royal Catering Co.", "profilePictureUrl": "http://..." }
      ],
      "conversationType": "USER_VENDOR",
      "relatedTo": { "entityType": "VENDOR", "entityId": "vendor-uuid" },
      "lastMessage": {
        "message": "What is the per-plate cost?",
        "senderId": "user-uuid",
        "timestamp": "2026-03-06T11:00:00Z"
      },
      "unreadCount": { "vendor-user-uuid": 1, "user-uuid": 0 },
      "status": "ACTIVE",
      "createdAt": "2026-03-06T10:00:00Z",
      "updatedAt": "2026-03-06T11:00:00Z"
    }
  ]
}
```

---

### Get Messages in Conversation
`GET /chat/conversations/{conversationId}/messages?page=0&size=50` 🔒

**Response `200`:**
```json
{
  "data": [
    {
      "messageId": "uuid",
      "conversationId": "uuid",
      "senderId": "user-uuid",
      "senderType": "USER",
      "message": "Hello, can you cater for 300 guests for our wedding?",
      "messageType": "TEXT",
      "attachments": [],
      "readBy": [
        { "userId": "vendor-user-uuid", "readAt": "2026-03-06T10:06:00Z" }
      ],
      "isDeleted": false,
      "deletedAt": null,
      "timestamp": "2026-03-06T10:05:00Z",
      "createdAt": "2026-03-06T10:05:00Z"
    }
  ]
}
```

---

### Send Message
`POST /chat/conversations/{conversationId}/messages` 🔒

| Field | Type | Required |
|-------|------|----------|
| `message` | string | ✅ |
| `messageType` | string | ⬜ | Default `TEXT` |
| `fileUrl` | string | ⬜ | For IMAGE/FILE type |

**Request:**
```json
{
  "message": "Yes, we can cater for 300 guests. Our per-plate rate starts at ₹400.",
  "messageType": "TEXT"
}
```
**Response `201`:** Message object.

---

### Mark Messages as Read
`PUT /chat/conversations/{conversationId}/read` 🔒
**Response `200`:** `{ "success": true }`

---

### WebSocket Chat (Real-time)
**Connect:** `ws://localhost:8080/api/v1/ws?token=<accessToken>`
**Subscribe:** `SUBSCRIBE /topic/conversations.{conversationId}`
**Send:** `SEND /app/chat/{conversationId}` — body: `{ "message": "Hello!", "messageType": "TEXT" }`

---

## 📤 File Upload

### Upload Image (Logo/Banner/Menu Photos)
`POST /upload/image` 🔒
`Content-Type: multipart/form-data`

| Form Field | Type | Required | Notes |
|-----------|------|----------|-------|
| `file` | file | ✅ | Max 5MB, JPEG/PNG/GIF/WebP |
| `entityType` | string | ⬜ | `VENDOR_LOGO`, `VENDOR_BANNER`, `MENU_ITEM`, `VENDOR_DOCUMENT` |
| `entityId` | string | ⬜ | vendorId or itemId |

**Response `200`:**
```json
{
  "data": {
    "fileId": "uuid",
    "fileName": "uuid.jpg",
    "originalName": "logo.jpg",
    "fileUrl": "http://localhost:8080/uploads/images/uuid.jpg",
    "fileType": "IMAGE",
    "contentType": "image/jpeg",
    "fileSize": 102400,
    "entityType": "VENDOR_LOGO",
    "entityId": "vendor-uuid",
    "uploadedBy": "uuid",
    "createdAt": "2026-03-06T10:00:00Z"
  }
}
```

---

### Upload Document (Business License, GST etc.)
`POST /upload/document` 🔒
Max 10MB. Supported: PDF, DOC, DOCX, JPEG, PNG.
Same response structure as image upload.

---

## 🎫 Support Tickets

### Create Ticket
`POST /support/tickets` 🔒

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `category` | string | ✅ | e.g. `PAYMENT`, `ACCOUNT`, `ORDER`, `TECHNICAL` |
| `subcategory` | string | ⬜ | e.g. `PAYOUT_DELAY`, `BID_ISSUE` |
| `priority` | string | ⬜ | `LOW`, `MEDIUM`, `HIGH`, `URGENT` |
| `subject` | string | ✅ | 5–200 chars |
| `description` | string | ✅ | 10–2000 chars |
| `orderId` | string | ⬜ | |
| `vendorId` | string | ⬜ | Your own vendor ID |
| `paymentId` | string | ⬜ | |
| `attachmentUrls` | array | ⬜ | |

**Request:**
```json
{
  "category": "PAYMENT",
  "subcategory": "PAYOUT_DELAY",
  "subject": "Payment not received for order completed on March 1",
  "description": "Order was completed on March 1 but I haven't received the payout yet.",
  "priority": "HIGH",
  "orderId": "uuid",
  "paymentId": "uuid",
  "attachmentUrls": []
}
```

**Response `201`:** Full `TicketResponse` object (same as client support response).

---

### Get My Tickets
`GET /support/tickets/my?page=0&size=20` 🔒
**Response `200`:** Paginated `TicketResponse` list.

---

## ⚠️ Standard Error Response

```json
{
  "success": false,
  "error": {
    "code": "FORBIDDEN",
    "message": "You are not authorized to access this resource",
    "path": "/api/v1/orders/uuid",
    "timestamp": "2026-03-06T10:00:00Z",
    "fieldErrors": {}
  }
}
```

**Common Vendor Error Codes:**
| Code | HTTP | Description |
|------|------|-------------|
| `UNAUTHORIZED` | 401 | Invalid or missing token |
| `FORBIDDEN` | 403 | Not your order/bid |
| `RESOURCE_NOT_FOUND` | 404 | Bid request or order not found |
| `VENDOR_NOT_APPROVED` | 403 | Vendor profile not approved yet |
| `DUPLICATE_BID` | 409 | Already submitted a bid for this request |
| `BID_INACTIVE` | 400 | Bid request no longer accepting bids |
| `MAX_REVISIONS` | 400 | Maximum 5 bid revisions reached |
| `VALIDATION_ERROR` | 400 | Request validation failed |
| `INVALID_STATUS_TRANSITION` | 400 | Cannot change to this order status |
