# 🏪 VENDOR API DOCUMENTATION
## Bidzaro Catering Platform — Complete Vendor Reference (Real DTO-Based)

**Version:** 1.0.0 | **Base URL:** `http://localhost:8080/api/v1`
**Auth:** `Authorization: Bearer {accessToken}` | **Content-Type:** `application/json`

> All field names, types, and response shapes taken directly from actual Java DTO classes.

---

## 🔐 Vendor Auth Flow
```
Step 1: Register as USER    → POST /auth/register  (userType: "VENDOR")
Step 2: Login               → POST /auth/login
Step 3: Create Vendor Profile → POST /vendors/register
Step 4: Upload Documents    → POST /uploads/document
Step 5: Wait for Approval   → PENDING → APPROVED by admin
Step 6: Manage Menu & Bids  → All vendor APIs
```

---

# 1. VENDOR REGISTRATION (Create Business Profile)

```http
POST /vendors/register
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request — All Fields
```json
{
  "businessName": "Spice Garden Catering",
  "businessEmail": "info@spicegarden.com",
  "businessPhone": "+917890123456",
  "businessType": "CATERING",
  "businessRegistrationNumber": "KA-REG-2015-12345",
  "taxId": "29ABCDE1234F1Z5",
  "description": "Authentic South Indian catering since 2010, serving weddings and corporate events.",
  "establishedYear": 2010,
  "cuisinesOffered": ["South Indian", "North Indian", "Continental"],
  "specialties": ["Weddings", "Corporate Events", "Birthday Parties"],
  "businessAddress": {
    "streetAddress": "25, 3rd Cross, Jayanagar 4th Block",
    "city": "Bangalore",
    "state": "Karnataka",
    "postalCode": "560041",
    "country": "India"
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
  "capacity": {
    "minGuests": 50,
    "maxGuests": 3000,
    "concurrentEvents": 4
  },
  "pricing": {
    "currency": "INR",
    "startingPricePerPlate": 350.00,
    "averagePricePerPlate": 500.00
  },
  "country": "INDIA"
}
```

| Field | Required | Notes |
|-------|----------|-------|
| `businessName` | ✅ | Catering business name |
| `businessEmail` | ✅ | Business contact email |
| `businessPhone` | ✅ | Business contact phone |
| `businessType` | ✅ | `CATERING`, `RESTAURANT`, `HOME_CHEF`, `CLOUD_KITCHEN` |
| `businessAddress` | ✅ | Full business address object |
| `ownerInfo` | ✅ | Owner details and ID proof |
| `capacity.minGuests` | ✅ | Minimum guests you can serve |
| `capacity.maxGuests` | ✅ | Maximum guests you can serve |
| `pricing.currency` | ✅ | `INR` or `USD` |
| `pricing.startingPricePerPlate` | ✅ | Starting price per head |
| `country` | ✅ | `INDIA` or `USA` |
| `businessRegistrationNumber` | ❌ | Company registration number |
| `taxId` | ❌ | GST / Tax ID |
| `description` | ❌ | Business description (max 2000 chars) |
| `establishedYear` | ❌ | Year business started |
| `cuisinesOffered` | ❌ | List of cuisine types |
| `specialties` | ❌ | Event types specialised in |
| `serviceAreas` | ❌ | List of cities/states you serve |
| `capacity.concurrentEvents` | ❌ | Simultaneous events capacity |
| `pricing.averagePricePerPlate` | ❌ | Average price per head |

### Success Response `201 Created`
```json
{
  "success": true,
  "message": "Vendor registered successfully. Pending approval.",
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
    "description": "Authentic South Indian catering since 2010...",
    "establishedYear": 2010,
    "cuisinesOffered": ["South Indian", "North Indian", "Continental"],
    "specialties": ["Weddings", "Corporate Events", "Birthday Parties"],
    "businessAddress": {
      "streetAddress": "25, 3rd Cross, Jayanagar 4th Block",
      "city": "Bangalore",
      "state": "Karnataka",
      "postalCode": "560041",
      "country": "India"
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
    "capacity": { "minGuests": 50, "maxGuests": 3000, "concurrentEvents": 4 },
    "pricing": { "currency": "INR", "startingPricePerPlate": 350.00, "averagePricePerPlate": 500.00 },
    "ratings": null,
    "stats": null,
    "status": "PENDING",
    "approvalStatus": "PENDING",
    "verified": false,
    "featured": false,
    "createdAt": "2026-02-24T10:30:45.123456Z",
    "documents": null,
    "country": "INDIA"
  }
}
```

---

# 2. GET MY VENDOR PROFILE

```http
GET /vendors/my-profile
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "vendorId": "vendor-12345-67890",
    "userId": "vendor-user-550e8400",
    "registeredEmail": "owner@spicegarden.com",
    "registeredPhone": "+917890123456",
    "registeredEmailVerified": true,
    "registeredPhoneVerified": true,
    "businessName": "Spice Garden Catering",
    "businessEmail": "info@spicegarden.com",
    "businessPhone": "+917890123456",
    "businessEmailVerified": true,
    "businessPhoneVerified": true,
    "businessType": "CATERING",
    "businessRegistrationNumber": "KA-REG-2015-12345",
    "taxId": "29ABCDE1234F1Z5",
    "logoUrl": "http://localhost:8080/uploads/images/logo-spice.jpg",
    "bannerUrl": "http://localhost:8080/uploads/images/banner-spice.jpg",
    "description": "Authentic South Indian catering since 2010.",
    "establishedYear": 2010,
    "cuisinesOffered": ["South Indian", "North Indian", "Continental"],
    "specialties": ["Weddings", "Corporate Events"],
    "businessAddress": {
      "streetAddress": "25, 3rd Cross, Jayanagar 4th Block",
      "city": "Bangalore",
      "state": "Karnataka",
      "postalCode": "560041",
      "country": "India"
    },
    "ownerInfo": {
      "firstName": "Rajesh",
      "lastName": "Kumar",
      "phone": "+917890123456",
      "email": "owner@spicegarden.com",
      "idProofType": "AADHAR",
      "idProofNumber": "XXXX-XXXX-9012"
    },
    "serviceAreas": [
      { "city": "Bangalore", "state": "Karnataka", "radiusKm": 30 },
      { "city": "Mysore", "state": "Karnataka", "radiusKm": 20 }
    ],
    "capacity": { "minGuests": 50, "maxGuests": 3000, "concurrentEvents": 4 },
    "pricing": { "currency": "INR", "startingPricePerPlate": 350.00, "averagePricePerPlate": 500.00 },
    "ratings": { "averageRating": 4.7, "totalReviews": 312 },
    "stats": { "totalOrders": 600, "completedOrders": 596 },
    "status": "ACTIVE",
    "approvalStatus": "APPROVED",
    "verified": true,
    "featured": false,
    "createdAt": "2026-01-15T08:00:00.000000Z",
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
        "uploadedAt": "2026-01-15T08:00:00.000000Z"
      },
      {
        "documentId": "doc-002-ccdd",
        "documentType": "TAX_CERTIFICATE",
        "documentName": "GST Certificate",
        "documentUrl": "http://localhost:8080/uploads/documents/gst-cert.pdf",
        "documentNumber": "29ABCDE1234F1Z5",
        "issueDate": "2020-06-01T00:00:00.000000Z",
        "expiryDate": null,
        "verificationStatus": "VERIFIED",
        "uploadedAt": "2026-01-15T08:00:00.000000Z"
      }
    ],
    "country": "INDIA"
  }
}
```

---

# 3. UPDATE VENDOR PROFILE

```http
PUT /vendors/{vendorId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request — All Fields
```json
{
  "businessName": "Spice Garden Premium Catering",
  "businessEmail": "info@spicegarden.com",
  "businessPhone": "+917890123456",
  "description": "Updated description — Premium authentic South Indian catering since 2010.",
  "cuisinesOffered": ["South Indian", "North Indian", "Continental", "Chinese"],
  "specialties": ["Weddings", "Corporate Events", "Birthday Parties"],
  "logoUrl": "http://localhost:8080/uploads/images/logo-spice-new.jpg",
  "bannerUrl": "http://localhost:8080/uploads/images/banner-spice-new.jpg",
  "serviceAreas": [
    { "city": "Bangalore", "state": "Karnataka", "radiusKm": 40 },
    { "city": "Mysore", "state": "Karnataka", "radiusKm": 25 }
  ],
  "capacity": { "minGuests": 50, "maxGuests": 5000, "concurrentEvents": 5 },
  "pricing": { "currency": "INR", "startingPricePerPlate": 400.00, "averagePricePerPlate": 600.00 }
}
```

### Success Response `200 OK`
Same shape as `VendorResponse` DTO — full vendor object returned.

---

# 4. UPLOAD VENDOR DOCUMENT

```http
POST /uploads/document
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data
```

### Form Fields
```
file:        <binary file data — PDF, JPG, PNG>
entityType:  VENDOR_DOCUMENT
entityId:    vendor-12345-67890
```

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "fileId": "file-doc-001-aabb",
    "fileName": "fssai-license-1708773045123.pdf",
    "originalName": "FSSAI_License.pdf",
    "fileType": "DOCUMENT",
    "contentType": "application/pdf",
    "fileSize": 245678,
    "fileUrl": "http://localhost:8080/uploads/documents/fssai-license-1708773045123.pdf",
    "createdAt": "2026-02-24T10:30:45.123456Z"
  }
}
```

> Use the returned `fileUrl` when adding documents to vendor profile.

---

# 5. ADD MENU ITEM (Vendor Menu)

```http
POST /menu/vendor-items
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request — All Fields
```json
{
  "masterItemId": "item-001",
  "customName": "Spice Garden Special Paneer Tikka",
  "customDescription": "Our signature paneer tikka with secret spice blend",
  "pricing": {
    "currency": "INR",
    "pricePerPlate": 200.00,
    "minimumOrderQuantity": 10,
    "discountPercentage": 10.00,
    "discountedPrice": 180.00
  },
  "availability": {
    "isAvailable": true,
    "unavailableReason": null,
    "unavailableUntil": null,
    "advanceNoticeHours": 24,
    "maxDailyCapacity": 500
  },
  "preparationTimeMinutes": 25,
  "customizationOptions": [
    {
      "optionName": "Spice Level",
      "choices": ["Mild", "Medium", "Spicy", "Extra Spicy"],
      "additionalCost": 0.00,
      "isRequired": false
    }
  ]
}
```

| Field | Required | Notes |
|-------|----------|-------|
| `masterItemId` | ✅ | Must exist in master menu catalog |
| `pricing.pricePerPlate` | ✅ | Your price per plate |
| `pricing.currency` | ✅ | `INR` or `USD` |
| `availability.isAvailable` | ✅ | `true`/`false` |
| `customName` | ❌ | Override item name |
| `customDescription` | ❌ | Override item description |
| `pricing.minimumOrderQuantity` | ❌ | Minimum plates to order |
| `pricing.discountPercentage` | ❌ | Discount % (0–100) |
| `pricing.discountedPrice` | ❌ | Calculated discounted price |
| `availability.advanceNoticeHours` | ❌ | Booking notice required |
| `availability.maxDailyCapacity` | ❌ | Max plates per day |
| `preparationTimeMinutes` | ❌ | Prep time in minutes |
| `customizationOptions` | ❌ | List of option sets |

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "vendorItemId": "vitem-001",
    "vendorId": "vendor-12345-67890",
    "masterItemId": "item-001",
    "customName": "Spice Garden Special Paneer Tikka",
    "customDescription": "Our signature paneer tikka with secret spice blend",
    "pricing": {
      "currency": "INR",
      "pricePerPlate": 200.00,
      "minimumOrderQuantity": 10,
      "discountPercentage": 10.00,
      "discountedPrice": 180.00
    },
    "availability": {
      "isAvailable": true,
      "unavailableReason": null,
      "unavailableUntil": null,
      "advanceNoticeHours": 24,
      "maxDailyCapacity": 500
    },
    "preparationTimeMinutes": 25,
    "customizationOptions": [
      {
        "optionName": "Spice Level",
        "choices": ["Mild", "Medium", "Spicy", "Extra Spicy"],
        "additionalCost": 0.00,
        "isRequired": false
      }
    ],
    "stats": { "totalOrders": 0, "averageRating": null, "totalReviews": 0 },
    "status": "ACTIVE",
    "createdAt": "2026-02-24T10:30:45.123456Z"
  }
}
```

---

# 6. GET MY MENU ITEMS

```http
GET /menu/vendor-items/my-items?page=0&size=20&status=ACTIVE
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "vendorItemId": "vitem-001",
      "vendorId": "vendor-12345-67890",
      "masterItemId": "item-001",
      "customName": "Spice Garden Special Paneer Tikka",
      "customDescription": "Our signature paneer tikka with secret spice blend",
      "pricing": {
        "currency": "INR",
        "pricePerPlate": 200.00,
        "minimumOrderQuantity": 10,
        "discountPercentage": 10.00,
        "discountedPrice": 180.00
      },
      "availability": {
        "isAvailable": true,
        "unavailableReason": null,
        "unavailableUntil": null,
        "advanceNoticeHours": 24,
        "maxDailyCapacity": 500
      },
      "preparationTimeMinutes": 25,
      "customizationOptions": [
        { "optionName": "Spice Level", "choices": ["Mild", "Medium", "Spicy"], "additionalCost": 0.00, "isRequired": false }
      ],
      "stats": { "totalOrders": 1200, "averageRating": 4.80, "totalReviews": 85 },
      "status": "ACTIVE",
      "createdAt": "2026-01-20T10:00:00.000000Z"
    },
    {
      "vendorItemId": "vitem-002",
      "vendorId": "vendor-12345-67890",
      "masterItemId": "item-002",
      "customName": null,
      "customDescription": null,
      "pricing": {
        "currency": "INR",
        "pricePerPlate": 220.00,
        "minimumOrderQuantity": 10,
        "discountPercentage": null,
        "discountedPrice": null
      },
      "availability": {
        "isAvailable": false,
        "unavailableReason": "Chicken supply issue",
        "unavailableUntil": "2026-02-26T00:00:00.000000Z",
        "advanceNoticeHours": 24,
        "maxDailyCapacity": 300
      },
      "preparationTimeMinutes": 30,
      "customizationOptions": null,
      "stats": { "totalOrders": 890, "averageRating": 4.60, "totalReviews": 60 },
      "status": "ACTIVE",
      "createdAt": "2026-01-20T10:00:00.000000Z"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 18, "totalPages": 1 }
}
```

---

# 7. UPDATE MENU ITEM

```http
PUT /menu/vendor-items/{vendorItemId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request — All Fields (same as Add Menu Item)
```json
{
  "customName": "Spice Garden Signature Paneer Tikka",
  "pricing": {
    "currency": "INR",
    "pricePerPlate": 210.00,
    "minimumOrderQuantity": 10,
    "discountPercentage": 5.00,
    "discountedPrice": 199.50
  },
  "availability": {
    "isAvailable": true,
    "advanceNoticeHours": 24,
    "maxDailyCapacity": 600
  },
  "preparationTimeMinutes": 20
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "vendorItemId": "vitem-001",
    "vendorId": "vendor-12345-67890",
    "masterItemId": "item-001",
    "customName": "Spice Garden Signature Paneer Tikka",
    "customDescription": "Our signature paneer tikka with secret spice blend",
    "pricing": {
      "currency": "INR",
      "pricePerPlate": 210.00,
      "minimumOrderQuantity": 10,
      "discountPercentage": 5.00,
      "discountedPrice": 199.50
    },
    "availability": {
      "isAvailable": true,
      "unavailableReason": null,
      "unavailableUntil": null,
      "advanceNoticeHours": 24,
      "maxDailyCapacity": 600
    },
    "preparationTimeMinutes": 20,
    "customizationOptions": [
      { "optionName": "Spice Level", "choices": ["Mild", "Medium", "Spicy", "Extra Spicy"], "additionalCost": 0.00, "isRequired": false }
    ],
    "stats": { "totalOrders": 1200, "averageRating": 4.80, "totalReviews": 85 },
    "status": "ACTIVE",
    "createdAt": "2026-01-20T10:00:00.000000Z"
  }
}
```

---

# 8. TOGGLE ITEM AVAILABILITY

```http
PATCH /menu/vendor-items/{vendorItemId}/availability
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{
  "isAvailable": false,
  "reason": "Ingredient shortage — back in stock Feb 26"
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Item availability updated",
  "data": {
    "vendorItemId": "vitem-002",
    "vendorId": "vendor-12345-67890",
    "masterItemId": "item-002",
    "customName": null,
    "customDescription": null,
    "pricing": { "currency": "INR", "pricePerPlate": 220.00, "minimumOrderQuantity": 10, "discountPercentage": null, "discountedPrice": null },
    "availability": {
      "isAvailable": false,
      "unavailableReason": "Ingredient shortage — back in stock Feb 26",
      "unavailableUntil": null,
      "advanceNoticeHours": 24,
      "maxDailyCapacity": 300
    },
    "preparationTimeMinutes": 30,
    "customizationOptions": null,
    "stats": { "totalOrders": 890, "averageRating": 4.60, "totalReviews": 60 },
    "status": "ACTIVE",
    "createdAt": "2026-01-20T10:00:00.000000Z"
  }
}
```

---

# 9. GET AVAILABLE BID LEADS (Bid Requests for Vendor)

```http
GET /bids/available-leads?page=0&size=20
Authorization: Bearer {accessToken}
```

> Returns open BidRequests in vendor's service area where competitive period is still ACTIVE.

### Success Response `200 OK`
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
        "venueAddress": {
          "streetAddress": "Palace Grounds, Jayamahal Road",
          "city": "Bangalore",
          "state": "Karnataka",
          "postalCode": "560080",
          "country": "India"
        }
      },
      "menuItems": [
        { "vendorItemId": null, "masterItemId": "item-001", "itemName": "Paneer Tikka", "quantity": 500 },
        { "vendorItemId": null, "masterItemId": "item-002", "itemName": "Butter Chicken", "quantity": 400 },
        { "vendorItemId": null, "masterItemId": "item-010", "itemName": "Gulab Jamun", "quantity": 500 }
      ],
      "additionalRequirements": {
        "serviceStaffNeeded": true,
        "numberOfStaff": 25,
        "decorationNeeded": false,
        "liveCounters": ["Dosa Counter", "Chaat Counter"],
        "specialInstructions": "Separate veg and non-veg sections. Food ready by 6:30 PM."
      },
      "budget": { "currency": "INR", "estimatedBudget": 200000, "budgetRange": "200000-250000" },
      "targetedVendors": [],
      "competitivePeriod": {
        "startTime": "2026-02-24T10:30:45.123456Z",
        "endTime": "2026-02-27T10:30:45.123456Z",
        "status": "ACTIVE"
      },
      "acceptedBid": null,
      "status": "ACTIVE",
      "totalBidsReceived": 2,
      "lowestBidAmount": 195000.00,
      "createdAt": "2026-02-24T10:30:45.123456Z",
      "expiresAt": "2026-03-03T10:30:45.123456Z"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 12, "totalPages": 1 }
}
```

---

# 10. SUBMIT A BID

```http
POST /bids/submit
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request — All Fields
```json
{
  "bidRequestId": "breq-88990-77665-aabb",
  "quotedPrice": {
    "currency": "INR",
    "subtotal": 175000.00,
    "serviceCharge": 17500.00,
    "taxPercentage": 5.00,
    "taxAmount": 8750.00,
    "totalAmount": 201250.00
  },
  "itemizedPricing": [
    { "vendorItemId": "vitem-001", "itemName": "Paneer Tikka", "quantity": 500, "pricePerPlate": 175.00, "totalPrice": 87500.00 },
    { "vendorItemId": "vitem-002", "itemName": "Butter Chicken", "quantity": 400, "pricePerPlate": 218.75, "totalPrice": 87500.00 },
    { "vendorItemId": "vitem-010", "itemName": "Gulab Jamun", "quantity": 500, "pricePerPlate": 0.00, "totalPrice": 0.00 }
  ],
  "deliveryDetails": {
    "estimatedSetupTime": "2026-05-20T16:00:00",
    "foodReadyTime": "2026-05-20T18:00:00",
    "cleanupTime": "2026-05-21T00:30:00"
  },
  "staffProvided": {
    "chefs": 8,
    "servers": 15,
    "cleaners": 5
  },
  "termsAndConditions": "50% balance due on event day. No cancellation within 3 days of event.",
  "validityPeriodHours": 48,
  "advancePercentage": 25.00,
  "requiredAdvanceAmount": 50312.50
}
```

| Field | Required | Notes |
|-------|----------|-------|
| `bidRequestId` | ✅ | The bid request you are quoting for |
| `quotedPrice.currency` | ✅ | `INR` or `USD` |
| `quotedPrice.totalAmount` | ✅ | Final quoted total |
| `advancePercentage` | ✅ | Token/advance % (typically 25%) |
| `requiredAdvanceAmount` | ✅ | Calculated advance amount |
| `validityPeriodHours` | ✅ | Hours bid is valid (24–168) |
| `itemizedPricing` | ❌ | Per-item breakdown |
| `deliveryDetails` | ❌ | Setup/ready/cleanup times |
| `staffProvided` | ❌ | Chefs/servers/cleaners count |
| `termsAndConditions` | ❌ | Your T&C text |
| `quotedPrice.serviceCharge` | ❌ | Service charge amount |
| `quotedPrice.taxPercentage` | ❌ | Tax % applied |

### Success Response `201 Created`
```json
{
  "success": true,
  "message": "Bid submitted successfully",
  "data": {
    "bidId": "bid-spice-001-aa11",
    "bidRequestId": "breq-88990-77665-aabb",
    "vendorId": "vendor-12345-67890",
    "vendorName": "Spice Garden Catering",
    "eventDetails": null,
    "quotedPrice": {
      "currency": "INR",
      "subtotal": 175000.00,
      "serviceCharge": 17500.00,
      "taxPercentage": 5.00,
      "taxAmount": 8750.00,
      "totalAmount": 201250.00
    },
    "itemizedPricing": [
      { "vendorItemId": "vitem-001", "itemName": "Paneer Tikka", "quantity": 500, "pricePerPlate": 175.00, "totalPrice": 87500.00 },
      { "vendorItemId": "vitem-002", "itemName": "Butter Chicken", "quantity": 400, "pricePerPlate": 218.75, "totalPrice": 87500.00 },
      { "vendorItemId": "vitem-010", "itemName": "Gulab Jamun", "quantity": 500, "pricePerPlate": 0.00, "totalPrice": 0.00 }
    ],
    "deliveryDetails": {
      "estimatedSetupTime": "2026-05-20T16:00:00",
      "foodReadyTime": "2026-05-20T18:00:00",
      "cleanupTime": "2026-05-21T00:30:00"
    },
    "staffProvided": { "chefs": 8, "servers": 15, "cleaners": 5 },
    "termsAndConditions": "50% balance due on event day. No cancellation within 3 days of event.",
    "validityPeriodHours": 48,
    "advancePercentage": 25.00,
    "requiredAdvanceAmount": 50312.50,
    "revisionCount": 0,
    "status": "PENDING",
    "isLowest": true,
    "rank": 1,
    "submittedAt": "2026-02-24T12:00:00.000000Z",
    "expiresAt": "2026-02-26T12:00:00.000000Z"
  }
}
```

---

# 11. GET MY SUBMITTED BIDS

```http
GET /bids/my-bids?page=0&size=20&status=PENDING
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "bidId": "bid-spice-001-aa11",
      "bidRequestId": "breq-88990-77665-aabb",
      "vendorId": "vendor-12345-67890",
      "vendorName": "Spice Garden Catering",
      "eventDetails": {
        "eventType": "WEDDING",
        "eventName": "Priya & Rahul Wedding",
        "eventDate": "2026-05-20T18:00:00",
        "eventStartTime": "18:00",
        "eventEndTime": "23:30",
        "numberOfGuests": 500,
        "venueAddress": { "streetAddress": "Palace Grounds", "city": "Bangalore", "state": "Karnataka", "postalCode": "560080", "country": "India" }
      },
      "quotedPrice": {
        "currency": "INR",
        "subtotal": 175000.00,
        "serviceCharge": 17500.00,
        "taxPercentage": 5.00,
        "taxAmount": 8750.00,
        "totalAmount": 201250.00
      },
      "itemizedPricing": [
        { "vendorItemId": "vitem-001", "itemName": "Paneer Tikka", "quantity": 500, "pricePerPlate": 175.00, "totalPrice": 87500.00 }
      ],
      "deliveryDetails": { "estimatedSetupTime": "2026-05-20T16:00:00", "foodReadyTime": "2026-05-20T18:00:00", "cleanupTime": "2026-05-21T00:30:00" },
      "staffProvided": { "chefs": 8, "servers": 15, "cleaners": 5 },
      "termsAndConditions": "50% balance due on event day.",
      "validityPeriodHours": 48,
      "advancePercentage": 25.00,
      "requiredAdvanceAmount": 50312.50,
      "revisionCount": 0,
      "status": "PENDING",
      "isLowest": true,
      "rank": 1,
      "submittedAt": "2026-02-24T12:00:00.000000Z",
      "expiresAt": "2026-02-26T12:00:00.000000Z"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 8, "totalPages": 1 }
}
```

---

# 12. REVISE A BID

```http
PUT /bids/{bidId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request (only send fields you want to update)
```json
{
  "quotedPrice": {
    "currency": "INR",
    "subtotal": 168000.00,
    "serviceCharge": 16800.00,
    "taxPercentage": 5.00,
    "taxAmount": 8400.00,
    "totalAmount": 193200.00
  },
  "advancePercentage": 25.00,
  "requiredAdvanceAmount": 48300.00,
  "validityPeriodHours": 48
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Bid revised successfully",
  "data": {
    "bidId": "bid-spice-001-aa11",
    "bidRequestId": "breq-88990-77665-aabb",
    "vendorId": "vendor-12345-67890",
    "vendorName": "Spice Garden Catering",
    "eventDetails": null,
    "quotedPrice": {
      "currency": "INR",
      "subtotal": 168000.00,
      "serviceCharge": 16800.00,
      "taxPercentage": 5.00,
      "taxAmount": 8400.00,
      "totalAmount": 193200.00
    },
    "itemizedPricing": null,
    "deliveryDetails": { "estimatedSetupTime": "2026-05-20T16:00:00", "foodReadyTime": "2026-05-20T18:00:00", "cleanupTime": "2026-05-21T00:30:00" },
    "staffProvided": { "chefs": 8, "servers": 15, "cleaners": 5 },
    "termsAndConditions": "50% balance due on event day.",
    "validityPeriodHours": 48,
    "advancePercentage": 25.00,
    "requiredAdvanceAmount": 48300.00,
    "revisionCount": 1,
    "status": "PENDING",
    "isLowest": true,
    "rank": 1,
    "submittedAt": "2026-02-24T12:00:00.000000Z",
    "expiresAt": "2026-02-26T12:00:00.000000Z"
  }
}
```

> **Max revisions:** 5 per bid (configured in platform settings). `revisionCount` increments each time.

---

# 13. WITHDRAW A BID

```http
DELETE /bids/{bidId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{ "success": true, "message": "Bid withdrawn successfully" }
```

---

# 14. GET MY ORDERS (As Vendor)

```http
GET /orders/vendor-orders?page=0&size=20&status=CONFIRMED
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
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
        "venueAddress": {
          "streetAddress": "Palace Grounds, Jayamahal Road",
          "city": "Bangalore",
          "state": "Karnataka",
          "postalCode": "560080",
          "country": "India"
        }
      },
      "vendorOrders": [
        {
          "vendorOrderId": "vorder-001-aabb",
          "vendorId": "vendor-12345-67890",
          "vendorUserId": null,
          "vendorName": "Spice Garden Catering",
          "items": [
            { "vendorItemId": "vitem-001", "itemName": "Paneer Tikka", "quantity": 500, "pricePerPlate": 175.00, "totalPrice": 87500.00 },
            { "vendorItemId": "vitem-002", "itemName": "Butter Chicken", "quantity": 400, "pricePerPlate": 218.75, "totalPrice": 87500.00 }
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
      "contactInfo": {
        "primaryContactName": "John Doe",
        "primaryContactPhone": "+917890123456",
        "primaryContactEmail": "john.doe@gmail.com"
      },
      "specialInstructions": "Separate veg and non-veg sections. Food ready by 6:30 PM sharp.",
      "status": "CONFIRMED",
      "cancellation": null,
      "createdAt": "2026-02-25T11:00:00.000000Z",
      "confirmedAt": "2026-02-25T11:00:00.000000Z",
      "deliveredAt": null,
      "completedAt": null
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 12, "totalPages": 1 }
}
```

---

# 15. UPDATE ORDER STATUS

```http
PATCH /orders/{orderId}/vendor-status
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{
  "vendorStatus": "IN_PREPARATION",
  "deliveryStatus": "PENDING",
  "notes": "Started food preparation"
}
```

| Status Flow | vendorStatus values |
|-------------|-------------------|
| Initial | `ACCEPTED` |
| Cooking | `IN_PREPARATION` |
| Dispatched | `DISPATCHED` |
| On-site | `SETUP_IN_PROGRESS` |
| Food Ready | `DELIVERED` |
| Done | `COMPLETED` |

### Success Response `200 OK`
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
      {
        "vendorOrderId": "vorder-001-aabb",
        "vendorId": "vendor-12345-67890",
        "vendorUserId": null,
        "vendorName": "Spice Garden Catering",
        "items": null,
        "subtotal": 175000.00,
        "serviceCharge": 17500.00,
        "taxAmount": 8750.00,
        "totalAmount": 201250.00,
        "vendorStatus": "IN_PREPARATION",
        "deliveryStatus": "PENDING"
      }
    ],
    "pricing": { "currency": "INR", "subtotal": 175000.00, "serviceCharges": 17500.00, "taxAmount": 8750.00, "platformFee": 4025.00, "discountAmount": 0.00, "totalAmount": 205275.00 },
    "paymentDetails": { "tokenAmount": 51318.75, "tokenPaid": true, "tokenPaidAt": "2026-02-25T11:05:00.000000Z", "totalPaid": 51318.75, "balanceDue": 153956.25, "paymentStatus": "TOKEN_PAID" },
    "contactInfo": { "primaryContactName": "John Doe", "primaryContactPhone": "+917890123456", "primaryContactEmail": "john.doe@gmail.com" },
    "specialInstructions": "Separate veg and non-veg sections.",
    "status": "CONFIRMED",
    "cancellation": null,
    "createdAt": "2026-02-25T11:00:00.000000Z",
    "confirmedAt": "2026-02-25T11:00:00.000000Z",
    "deliveredAt": null,
    "completedAt": null
  }
}
```

---

# 16. GET MY REVIEWS

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
      "reviewId": "rev-11223-44556-ccdd",
      "orderId": "order-54321-12345-ccdd",
      "vendorId": "vendor-12345-67890",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "userName": "John Doe",
      "rating": 5,
      "foodQualityRating": 5,
      "serviceQualityRating": 4,
      "hygieneRating": 5,
      "valueForMoneyRating": 4,
      "punctualityRating": 4,
      "reviewText": "Outstanding food quality! Paneer Tikka was excellent.",
      "images": ["http://localhost:8080/uploads/images/wedding-food-1.jpg"],
      "vendorResponse": null,
      "helpfulCount": 8,
      "status": "APPROVED",
      "createdAt": "2026-05-21T10:00:00.000000Z"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 312, "totalPages": 16 }
}
```

---

# 17. RESPOND TO A REVIEW

```http
POST /reviews/{reviewId}/vendor-response
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{ "responseText": "Thank you so much John! We are thrilled you enjoyed the food. Looking forward to serving you again!" }
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "reviewId": "rev-11223-44556-ccdd",
    "orderId": "order-54321-12345-ccdd",
    "vendorId": "vendor-12345-67890",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "userName": "John Doe",
    "rating": 5,
    "foodQualityRating": 5,
    "serviceQualityRating": 4,
    "hygieneRating": 5,
    "valueForMoneyRating": 4,
    "punctualityRating": 4,
    "reviewText": "Outstanding food quality! Paneer Tikka was excellent.",
    "images": ["http://localhost:8080/uploads/images/wedding-food-1.jpg"],
    "vendorResponse": {
      "responseText": "Thank you so much John! We are thrilled you enjoyed the food. Looking forward to serving you again!",
      "respondedAt": "2026-05-22T09:00:00.000000Z"
    },
    "helpfulCount": 8,
    "status": "APPROVED",
    "createdAt": "2026-05-21T10:00:00.000000Z"
  }
}
```

---

# 18. VENDOR ANALYTICS DASHBOARD

```http
GET /analytics/vendor/dashboard
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
      "pendingOrders": 2,
      "cancelledOrders": 2,
      "totalRevenue": 12500000.00,
      "pendingPayouts": 350000.00,
      "thisMonthRevenue": 450000.00,
      "currency": "INR"
    },
    "bidMetrics": {
      "totalBidsSubmitted": 850,
      "acceptedBids": 600,
      "pendingBids": 12,
      "acceptanceRate": 70.59,
      "averageBidAmount": 180000.00
    },
    "recentOrders": [
      {
        "orderId": "order-54321-12345-ccdd",
        "eventName": "Priya & Rahul Wedding",
        "eventDate": "2026-05-20",
        "guestCount": 500,
        "amount": 201250.00,
        "status": "CONFIRMED"
      },
      {
        "orderId": "order-11111-22222-eeff",
        "eventName": "TCS Annual Day",
        "eventDate": "2026-03-15",
        "guestCount": 1200,
        "amount": 480000.00,
        "status": "CONFIRMED"
      }
    ],
    "upcomingEvents": [
      {
        "orderId": "order-54321-12345-ccdd",
        "eventName": "Priya & Rahul Wedding",
        "eventDate": "2026-05-20",
        "eventTime": "18:00",
        "venue": "Palace Grounds, Bangalore",
        "guestCount": 500,
        "daysUntil": 85
      }
    ],
    "performance": {
      "averageRating": 4.7,
      "totalReviews": 312,
      "responseRate": 95.5,
      "onTimeDeliveryRate": 98.2,
      "repeatCustomers": 120
    }
  }
}
```

---

# 19. CHAT WITH CUSTOMER

```http
POST /chat/conversations
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{ "otherUserId": "550e8400-e29b-41d4-a716-446655440000", "type": "USER_VENDOR" }
```

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "conversationId": "conv-77665-88990-aabb",
    "participants": [
      { "userId": "vendor-user-550e8400", "userType": "VENDOR", "name": "Spice Garden Catering" },
      { "userId": "550e8400-e29b-41d4-a716-446655440000", "userType": "USER", "name": "John Doe" }
    ],
    "otherParticipant": { "userId": "550e8400-e29b-41d4-a716-446655440000", "userType": "USER", "name": "John Doe" },
    "lastMessage": null,
    "unreadCount": 0,
    "status": "ACTIVE",
    "createdAt": "2026-02-24T10:30:45.123456Z",
    "updatedAt": "2026-02-24T10:30:45.123456Z"
  }
}
```

---

# 20. GET CONVERSATIONS

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
      "conversationId": "conv-77665-88990-aabb",
      "participants": [
        { "userId": "vendor-user-550e8400", "userType": "VENDOR", "name": "Spice Garden Catering" },
        { "userId": "550e8400-e29b-41d4-a716-446655440000", "userType": "USER", "name": "John Doe" }
      ],
      "otherParticipant": { "userId": "550e8400-e29b-41d4-a716-446655440000", "userType": "USER", "name": "John Doe" },
      "lastMessage": {
        "message": "Hi, can you cater for 500 guests on May 20?",
        "senderId": "550e8400-e29b-41d4-a716-446655440000",
        "senderType": "USER",
        "timestamp": "2026-02-24T10:31:00.000000Z"
      },
      "unreadCount": 1,
      "status": "ACTIVE",
      "createdAt": "2026-02-24T10:30:45.123456Z",
      "updatedAt": "2026-02-24T10:31:00.000000Z"
    }
  ]
}
```

---

# 21. GET CHAT MESSAGES

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
      "messageId": "msg-001-aaaa",
      "conversationId": "conv-77665-88990-aabb",
      "senderId": "550e8400-e29b-41d4-a716-446655440000",
      "senderType": "USER",
      "senderName": null,
      "message": "Hi, can you cater for 500 guests on May 20, 2026?",
      "messageType": "TEXT",
      "attachments": null,
      "timestamp": "2026-02-24T10:31:00.000000Z"
    },
    {
      "messageId": "msg-002-bbbb",
      "conversationId": "conv-77665-88990-aabb",
      "senderId": "vendor-user-550e8400",
      "senderType": "VENDOR",
      "senderName": null,
      "message": "Yes! We can handle 500 guests. Starting ₹500/plate. Here is our menu brochure.",
      "messageType": "TEXT",
      "attachments": [
        {
          "fileName": "spice-garden-menu-2026.pdf",
          "fileUrl": "http://localhost:8080/uploads/documents/spice-garden-menu-2026.pdf",
          "fileType": "DOCUMENT",
          "fileSize": 512000
        }
      ],
      "timestamp": "2026-02-24T11:00:00.000000Z"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 50, "totalElements": 2, "totalPages": 1 }
}
```

### WebSocket — Send Message as Vendor
```
SEND to: /app/chat.sendMessage
{
  "conversationId": "conv-77665-88990-aabb",
  "message": "We can provide 8 chefs and 15 servers for your event.",
  "messageType": "TEXT"
}
SUBSCRIBE: /topic/conversations.conv-77665-88990-aabb
```

---

# 📌 Vendor Status Reference

| Status | Description |
|--------|-------------|
| `PENDING` | Awaiting admin review |
| `APPROVED` | Approved — can receive leads |
| `REJECTED` | Rejected — need to re-apply |
| `SUSPENDED` | Suspended — contact support |
| `ACTIVE` | Live and operational |
| `INACTIVE` | Temporarily inactive |

# 📌 Bid Status Reference

| Status | Description |
|--------|-------------|
| `PENDING` | Submitted, awaiting customer decision |
| `ACCEPTED` | Customer accepted your bid |
| `REJECTED` | Customer chose another vendor |
| `EXPIRED` | Bid validity period passed |
| `WITHDRAWN` | You withdrew the bid |

# 📌 Order vendorStatus Reference

| Status | Description |
|--------|-------------|
| `ACCEPTED` | Order confirmed, you accepted |
| `IN_PREPARATION` | Cooking in progress |
| `DISPATCHED` | Food dispatched to venue |
| `SETUP_IN_PROGRESS` | Setting up at venue |
| `DELIVERED` | Food served / delivered |
| `COMPLETED` | Order fully completed |
| `CANCELLED` | Order was cancelled |

---

*VENDOR_API_DOCS.md — Based on actual Java DTOs (VendorRegistrationRequest, VendorUpdateRequest, VendorResponse, VendorMenuItemResponse, SubmitBidDTO, VendorBidResponse, BidRequestResponse, OrderResponse, ReviewResponse, VendorDashboardResponse, ConversationResponse, ChatMessageResponse, FileUploadResponse)*
*Bidzaro Catering Platform v1.0.0 | Generated: February 24, 2026*

