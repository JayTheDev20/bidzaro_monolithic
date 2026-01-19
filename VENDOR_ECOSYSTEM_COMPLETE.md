# Complete Vendor Ecosystem APIs Documentation

**Version:** 2.0.0 - Full Project Overview  
**Base URL:** `http://localhost:8080/api/v1`  
**Date:** January 19, 2026

---

## 📋 Table of Contents

1. [Project Structure Overview](#project-structure-overview)
2. [Vendor Core APIs](#vendor-core-apis)
3. [Menu Management APIs](#menu-management-apis)
4. [Bid Management APIs](#bid-management-apis)
5. [Order Management APIs](#order-management-apis)
6. [Payment APIs](#payment-apis)
7. [Review & Ratings APIs](#review--ratings-apis)
8. [Cart Management APIs](#cart-management-apis)
9. [Analytics & Dashboard APIs](#analytics--dashboard-apis)
10. [File Upload APIs](#file-upload-apis)
11. [Vendor Workflow & Integration](#vendor-workflow--integration)

---

## Project Structure Overview

The bidzaro catering platform is built as a **modular microservices architecture** with the following components:

```
bidzaro_monolithic/src/main/java/com/cateringmarketplace/module/
├── vendor/              ✓ Vendor registration & profiles
├── menu/               ✓ Menu & item management
├── bid/                ✓ Bidding system
├── order/              ✓ Order management
├── payment/            ✓ Payment processing
├── review/             ✓ Reviews & ratings
├── analytics/          ✓ Dashboards & reports
├── cart/               ✓ Shopping cart
├── auth/               ✓ Authentication
├── admin/              ✓ Admin panel
├── upload/             ✓ File uploads
├── chat/               ✓ Messaging
├── notification/       ✓ Notifications
├── loyalty/            ✓ Loyalty program
├── promo/              ✓ Promotional codes
├── referral/           ✓ Referral system
├── wishlist/           ✓ Wishlist
└── support/            ✓ Support tickets
```

### Vendor-Related Modules Dependencies

```
VENDOR (Core)
  ├── Menu (Vendor menu items)
  ├── Bid (Vendor bidding)
  ├── Order (Vendor orders)
  ├── Review (Vendor ratings & reviews)
  ├── Payment (Vendor payouts)
  ├── Analytics (Vendor dashboard)
  └── Upload (Vendor documents)
```

---

## Vendor Core APIs

### Complete Vendor Registration Flow

**Base Endpoint:** `/vendors`

#### 1. Register as Vendor

**POST** `/vendors`

**Authentication:** Required (Bearer Token) - User must be authenticated

**Description:** Vendor registration with complete business information. Vendor enters PENDING_APPROVAL status.

**Headers:**
```
Authorization: Bearer {accessToken}
Content-Type: application/json
```

**Request Body:**
```json
{
  "businessName": "Delicious Catering Co.",
  "businessEmail": "contact@deliciouscatering.com",
  "businessPhone": "+919876543210",
  "businessType": "CATERING",
  "businessRegistrationNumber": "REG123456789",
  "taxId": "27XXXXX1234Z1Z5",
  "description": "Premium catering services for all occasions",
  "establishedYear": 2018,
  "cuisinesOffered": ["North Indian", "South Indian", "Chinese"],
  "specialties": ["weddings", "corporate_events", "birthday_parties"],
  "country": "India",
  "businessAddress": {
    "streetAddress": "123 Main Street, Block A",
    "city": "Mumbai",
    "state": "Maharashtra",
    "postalCode": "400001",
    "country": "India",
    "latitude": 19.0760,
    "longitude": 72.8777
  },
  "ownerInfo": {
    "firstName": "Ramesh",
    "lastName": "Kumar",
    "phone": "+919876543210",
    "email": "ramesh@deliciouscatering.com",
    "idProofType": "AADHAR",
    "idProofNumber": "123456789012"
  },
  "serviceAreas": [
    {
      "city": "Mumbai",
      "state": "Maharashtra",
      "radiusKm": 50
    },
    {
      "city": "Pune",
      "state": "Maharashtra",
      "radiusKm": 30
    }
  ],
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

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Vendor registered successfully. Pending approval.",
  "data": {
    "vendorId": "vendor_60f5b3e4a1d2c9123a4b9f10",
    "userId": "user_60f5a3d2c9d4f8123a4b9e0f",
    "businessName": "Delicious Catering Co.",
    "businessEmail": "contact@deliciouscatering.com",
    "businessPhone": "+919876543210",
    "businessType": "CATERING",
    "status": "PENDING_APPROVAL",
    "approvalStatus": "PENDING",
    "createdAt": "2026-01-08T12:00:00Z"
  }
}
```

#### 2. Get All Vendors

**GET** `/vendors`

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 20)
- `status` (optional) - PENDING_APPROVAL, ACTIVE, SUSPENDED, REJECTED
- `city` (optional)
- `cuisine` (optional)
- `sortBy` (default: createdAt)
- `sortDir` (default: desc)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Vendors retrieved successfully",
  "data": [
    {
      "vendorId": "vendor_60f5b3e4a1d2c9123a4b9f10",
      "businessName": "Delicious Catering Co.",
      "businessType": "CATERING",
      "cuisinesOffered": ["North Indian", "South Indian", "Chinese"],
      "status": "ACTIVE",
      "verified": true,
      "ratings": {
        "averageRating": 4.5,
        "totalReviews": 150
      },
      "stats": {
        "totalOrders": 500,
        "completedOrders": 495
      },
      "createdAt": "2026-01-08T12:00:00Z"
    }
  ],
  "pageInfo": {
    "totalElements": 156,
    "totalPages": 8,
    "currentPage": 0,
    "pageSize": 20,
    "hasNext": true
  }
}
```

#### 3. Get Vendor by ID

**GET** `/vendors/{vendorId}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "vendorId": "vendor_60f5b3e4a1d2c9123a4b9f10",
    "businessName": "Delicious Catering Co.",
    "businessEmail": "contact@deliciouscatering.com",
    "businessPhone": "+919876543210",
    "businessType": "CATERING",
    "description": "Premium catering services for all occasions",
    "establishedYear": 2018,
    "cuisinesOffered": ["North Indian", "South Indian", "Chinese"],
    "specialties": ["weddings", "corporate_events"],
    "status": "ACTIVE",
    "approvalStatus": "APPROVED",
    "verified": true,
    "featured": true,
    "businessAddress": {
      "streetAddress": "123 Main Street, Block A",
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
    },
    "ratings": {
      "averageRating": 4.5,
      "totalReviews": 150
    },
    "stats": {
      "totalOrders": 500,
      "completedOrders": 495,
      "cancelledOrders": 5
    },
    "createdAt": "2026-01-08T12:00:00Z"
  }
}
```

#### 4. Get My Vendor Profile

**GET** `/vendors/me`

**Authentication:** Required (Bearer Token)

**Response (200 OK):** Same as Get Vendor by ID

#### 5. Update Vendor Profile

**PUT** `/vendors/{vendorId}`

**Authentication:** Required (Bearer Token)

**Request Body:**
```json
{
  "description": "Updated description",
  "cuisinesOffered": ["North Indian", "South Indian", "Chinese", "Continental"],
  "specialties": ["weddings", "corporate_events", "birthday_parties", "conferences"],
  "capacity": {
    "minGuests": 50,
    "maxGuests": 6000,
    "concurrentEvents": 6
  },
  "pricing": {
    "currency": "INR",
    "startingPricePerPlate": 600.00,
    "averagePricePerPlate": 850.00
  }
}
```

**Response (200 OK):** Updated vendor object

#### 6. Search Vendors

**GET** `/vendors/search`

**Query Parameters:**
- `query` (optional) - Business name or cuisine
- `city` (optional)
- `cuisines` (optional, repeatable) - e.g., `&cuisines=North Indian&cuisines=Chinese`
- `rating` (optional) - Minimum rating
- `page` (default: 0)
- `size` (default: 20)

**Response (200 OK):** List of matching vendors

---

### Admin Vendor Management

#### 7. Get Pending Vendors (Admin Only)

**GET** `/vendors/admin/pending`

**Authentication:** Required (Bearer Token - Admin Role)

**Query Parameters:**
- `page` (default: 0)
- `size` (default: 20)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Pending vendors retrieved",
  "data": [
    {
      "vendorId": "vendor_pending_001",
      "businessName": "New Catering Services",
      "businessEmail": "new@catering.com",
      "businessType": "CATERING",
      "status": "PENDING_APPROVAL",
      "approvalStatus": "PENDING",
      "createdAt": "2026-01-18T10:00:00Z"
    }
  ],
  "pageInfo": {
    "totalElements": 12,
    "totalPages": 1,
    "currentPage": 0
  }
}
```

#### 8. Approve Vendor (Admin Only)

**POST** `/vendors/{vendorId}/approve`

**Authentication:** Required (Bearer Token - Admin Role)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Vendor approved successfully",
  "data": {
    "vendorId": "vendor_pending_001",
    "status": "ACTIVE",
    "approvalStatus": "APPROVED",
    "approvalDate": "2026-01-19T14:00:00Z"
  }
}
```

#### 9. Reject Vendor (Admin Only)

**POST** `/vendors/{vendorId}/reject?reason=Incomplete%20documentation`

**Authentication:** Required (Bearer Token - Admin Role)

**Query Parameters:**
- `reason` (required) - Reason for rejection

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Vendor rejected",
  "data": {
    "vendorId": "vendor_pending_002",
    "status": "REJECTED",
    "approvalStatus": "REJECTED",
    "rejectionReason": "Incomplete documentation"
  }
}
```

---

## Menu Management APIs

All menu operations involve vendor items. The platform has Master Menu Items that vendors customize.

**Base Endpoint:** `/menu`

### Master Menu Items

#### 1. Get All Categories

**GET** `/menu/categories`

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "categoryId": "cat_123",
      "name": "Appetizers",
      "description": "Starters and appetizers",
      "status": "ACTIVE"
    },
    {
      "categoryId": "cat_124",
      "name": "Main Course",
      "description": "Main dishes"
    }
  ]
}
```

#### 2. Get All Menu Items

**GET** `/menu/items?page=0&size=20`

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "itemId": "item_123",
      "name": "Paneer Tikka",
      "description": "Marinated paneer cubes",
      "category": "Appetizers",
      "spicyLevel": 2,
      "vegetarian": true
    }
  ],
  "pageInfo": {
    "totalElements": 250,
    "totalPages": 13,
    "currentPage": 0
  }
}
```

#### 3. Search Menu Items

**GET** `/menu/items/search?query=paneer&page=0&size=20`

#### 4. Get Popular Items

**GET** `/menu/items/popular`

---

### Vendor Menu Management

Vendors add master items to their custom menu with vendor-specific pricing.

#### 5. Get Vendor Menu Items

**GET** `/menu/vendor-items?vendorId={vendorId}&page=0&size=20`

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "vendorItemId": "vendor_item_123",
      "vendorId": "vendor_60f5b3e4a1d2c9123a4b9f10",
      "itemId": "item_123",
      "itemName": "Paneer Tikka",
      "category": "Appetizers",
      "pricePerUnit": 150.00,
      "minOrderQuantity": 50,
      "isAvailable": true,
      "preparationTime": 2,
      "createdAt": "2026-01-08T12:00:00Z"
    }
  ]
}
```

#### 6. Add Item to Vendor Menu

**POST** `/menu/vendor-items`

**Authentication:** Required (Bearer Token - Vendor Role)

**Request Body:**
```json
{
  "itemId": "item_123",
  "pricePerUnit": 150.00,
  "minOrderQuantity": 50,
  "preparationTime": 2
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Item added to menu",
  "data": {
    "vendorItemId": "vendor_item_123",
    "vendorId": "vendor_60f5b3e4a1d2c9123a4b9f10",
    "itemName": "Paneer Tikka",
    "pricePerUnit": 150.00,
    "minOrderQuantity": 50,
    "isAvailable": true
  }
}
```

#### 7. Update Vendor Menu Item

**PUT** `/menu/vendor-items/{vendorItemId}`

**Authentication:** Required (Bearer Token - Vendor Role)

**Request Body:**
```json
{
  "pricePerUnit": 160.00,
  "minOrderQuantity": 50,
  "preparationTime": 2
}
```

**Response (200 OK):** Updated vendor item

#### 8. Update Item Availability

**PATCH** `/menu/vendor-items/{vendorItemId}/availability?isAvailable=false&reason=Out of stock`

**Authentication:** Required (Bearer Token - Vendor Role)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Availability updated",
  "data": {
    "vendorItemId": "vendor_item_123",
    "isAvailable": false,
    "unavailableReason": "Out of stock"
  }
}
```

#### 9. Delete Vendor Menu Item

**DELETE** `/menu/vendor-items/{vendorItemId}`

**Authentication:** Required (Bearer Token - Vendor Role)

---

## Bid Management APIs

The bid system allows customers to create bid requests and vendors to submit competitive bids.

**Base Endpoint:** `/bids`

### Bid Request Lifecycle

1. **Customer** creates a bid request
2. **Vendors** submit bids
3. **Customer** accepts best bid
4. **Order** is created from accepted bid

### Customer - Bid Request Endpoints

#### 1. Create Bid Request

**POST** `/bids/requests`

**Authentication:** Required (Bearer Token)

**Request Body:**
```json
{
  "eventName": "Corporate Meeting Catering",
  "eventDate": "2026-06-15T18:00:00Z",
  "eventDuration": 4,
  "guestCount": 150,
  "eventLocation": {
    "address": "123 Business Park, Mumbai",
    "city": "Mumbai",
    "state": "Maharashtra",
    "latitude": 19.0760,
    "longitude": 72.8777
  },
  "cuisinePreferences": ["North Indian", "Continental"],
  "serviceType": ["BUFFET", "LIVE_COUNTER"],
  "budget": {
    "minBudget": 20000.00,
    "maxBudget": 50000.00,
    "currency": "INR"
  },
  "specialRequirements": "Vegetarian options required, Nut-free meals needed",
  "preferredVendors": ["vendor_123", "vendor_456"]
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Bid request created successfully",
  "data": {
    "bidRequestId": "bid_req_123",
    "userId": "user_123",
    "eventName": "Corporate Meeting Catering",
    "eventDate": "2026-06-15T18:00:00Z",
    "guestCount": 150,
    "status": "ACTIVE",
    "totalBidsReceived": 0,
    "createdAt": "2026-01-08T10:00:00Z"
  }
}
```

#### 2. Get User's Bid Requests

**GET** `/bids/requests?page=0&size=20`

**Authentication:** Required (Bearer Token)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Bid requests retrieved",
  "data": [
    {
      "bidRequestId": "bid_req_123",
      "eventName": "Corporate Meeting Catering",
      "eventDate": "2026-06-15T18:00:00Z",
      "guestCount": 150,
      "budget": {
        "minBudget": 20000.00,
        "maxBudget": 50000.00,
        "currency": "INR"
      },
      "status": "ACTIVE",
      "totalBidsReceived": 5,
      "lowestBidAmount": 25000.00,
      "createdAt": "2026-01-08T10:00:00Z"
    }
  ],
  "pageInfo": {
    "totalElements": 12,
    "totalPages": 1,
    "currentPage": 0
  }
}
```

#### 3. Get Bid Request Details

**GET** `/bids/requests/{bidRequestId}`

#### 4. Get Bids for Request (Customer View)

**GET** `/bids/requests/{bidRequestId}/bids`

**Authentication:** Required (Bearer Token)

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "bidId": "bid_123",
      "bidRequestId": "bid_req_123",
      "vendorId": "vendor_60f5b3e4a1d2c9123a4b9f10",
      "vendorName": "Delicious Catering Co.",
      "submittedAt": "2026-01-10T14:00:00Z",
      "quotedPrice": {
        "perPlate": 150.00,
        "totalAmount": 22500.00,
        "currency": "INR"
      },
      "staffProvided": {
        "chefs": 3,
        "servers": 5,
        "cleaners": 2
      },
      "status": "SUBMITTED",
      "ranking": 1
    }
  ]
}
```

#### 5. Accept Bid

**POST** `/bids/{bidId}/accept`

**Authentication:** Required (Bearer Token)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Bid accepted. Cooling period started.",
  "data": {
    "bidId": "bid_123",
    "status": "ACCEPTED",
    "coolingPeriodEndsAt": "2026-01-11T14:00:00Z"
  }
}
```

#### 6. Cancel Bid Request

**DELETE** `/bids/requests/{bidRequestId}`

**Authentication:** Required (Bearer Token)

---

### Vendor - Bid Submission Endpoints

#### 7. Submit Bid (Vendor)

**POST** `/bids/requests/{bidRequestId}/submit-bid`

**Authentication:** Required (Bearer Token - Vendor Role)

**Request Body:**
```json
{
  "quotedPrice": {
    "perPlate": 150.00,
    "totalAmount": 22500.00,
    "deliveryCharges": 500.00,
    "currency": "INR"
  },
  "staffProvided": {
    "chefs": 3,
    "servers": 5,
    "cleaners": 2
  },
  "menuOptions": [
    {
      "vendorItemId": "vendor_item_123",
      "quantity": 100,
      "notes": "Paneer Tikka"
    }
  ],
  "deliveryAvailable": true,
  "specialServices": ["Bar Service", "DJ Arrangements"],
  "notes": "We specialize in corporate events"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Bid submitted successfully",
  "data": {
    "bidId": "bid_123",
    "vendorId": "vendor_60f5b3e4a1d2c9123a4b9f10",
    "bidRequestId": "bid_req_123",
    "status": "SUBMITTED",
    "submittedAt": "2026-01-10T14:00:00Z",
    "quotedPrice": {
      "totalAmount": 22500.00,
      "currency": "INR"
    }
  }
}
```

#### 8. Get Vendor's Submitted Bids

**GET** `/bids/vendor/submitted?page=0&size=20`

**Authentication:** Required (Bearer Token - Vendor Role)

**Response (200 OK):** List of vendor's bids

#### 9. Get Bid Requests for Vendor (Available to Bid On)

**GET** `/bids/vendor/received?page=0&size=20`

**Authentication:** Required (Bearer Token - Vendor Role)

#### 10. Revise Bid

**PUT** `/bids/{bidId}?reason=Price adjustment`

**Authentication:** Required (Bearer Token - Vendor Role)

**Request Body:** Same as Submit Bid

**Response (200 OK):** Updated bid

#### 11. Withdraw Bid

**DELETE** `/bids/{bidId}`

**Authentication:** Required (Bearer Token - Vendor Role)

---

## Order Management APIs

Orders are created from accepted bids or directly from cart purchases.

**Base Endpoint:** `/orders`

#### 1. Create Order from Bid

**POST** `/orders?bidRequestId=bid_req_123`

**Authentication:** Required (Bearer Token)

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Order created. Please complete token payment.",
  "data": {
    "orderId": "order_789",
    "bidRequestId": "bid_req_123",
    "vendorId": "vendor_123",
    "vendorName": "Delicious Catering Co.",
    "eventDate": "2026-06-15T18:00:00Z",
    "guestCount": 150,
    "totalAmount": 22500.00,
    "tokenAmount": 5625.00,
    "status": "PENDING_TOKEN_PAYMENT",
    "createdAt": "2026-01-08T11:00:00Z"
  }
}
```

#### 2. Get User's Orders

**GET** `/orders?page=0&size=20`

**Authentication:** Required (Bearer Token)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Orders retrieved",
  "data": [
    {
      "orderId": "order_789",
      "vendorName": "Delicious Catering Co.",
      "eventDate": "2026-06-15T18:00:00Z",
      "guestCount": 150,
      "totalAmount": 22500.00,
      "status": "CONFIRMED",
      "createdAt": "2026-01-08T11:00:00Z"
    }
  ]
}
```

#### 3. Get Order Details

**GET** `/orders/{orderId}`

**Authentication:** Required (Bearer Token)

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "orderId": "order_789",
    "userId": "user_123",
    "vendorId": "vendor_123",
    "vendorName": "Delicious Catering Co.",
    "eventDetails": {
      "eventName": "Corporate Meeting Catering",
      "eventDate": "2026-06-15T18:00:00Z",
      "guestCount": 150,
      "eventLocation": {
        "address": "123 Business Park",
        "city": "Mumbai"
      }
    },
    "items": [
      {
        "vendorItemId": "vendor_item_123",
        "itemName": "Paneer Tikka",
        "quantity": 100,
        "pricePerUnit": 150.00,
        "totalPrice": 15000.00
      }
    ],
    "pricing": {
      "subtotal": 22000.00,
      "deliveryCharges": 500.00,
      "taxes": 4000.00,
      "totalAmount": 26500.00,
      "tokenAmount": 6625.00
    },
    "status": "CONFIRMED",
    "paymentStatus": "CONFIRMED",
    "createdAt": "2026-01-08T11:00:00Z"
  }
}
```

#### 4. Update Order Status

**PATCH** `/orders/{orderId}/status?status=IN_PROGRESS`

**Authentication:** Required (Bearer Token)

**Response (200 OK):** Updated order

#### 5. Cancel Order

**POST** `/orders/{orderId}/cancel?reason=User requested cancellation`

**Authentication:** Required (Bearer Token)

**Response (200 OK):** Cancelled order

#### 6. Get Upcoming Orders

**GET** `/orders/upcoming?page=0&size=20`

**Authentication:** Required (Bearer Token)

#### 7. Get Order History

**GET** `/orders/history?page=0&size=20`

**Authentication:** Required (Bearer Token)

#### 8. Get Vendor's Orders

**GET** `/orders/vendor?page=0&size=20`

**Authentication:** Required (Bearer Token - Vendor Role)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Vendor orders retrieved",
  "data": [
    {
      "orderId": "order_789",
      "customerName": "John Doe",
      "eventDate": "2026-06-15T18:00:00Z",
      "guestCount": 150,
      "totalAmount": 22500.00,
      "status": "CONFIRMED",
      "createdAt": "2026-01-08T11:00:00Z"
    }
  ]
}
```

---

## Payment APIs

**Base Endpoint:** `/payments`

#### 1. Initiate Payment

**POST** `/payments/initiate?orderId={orderId}&paymentType=TOKEN&amount=5625.00`

**Authentication:** Required (Bearer Token)

**Query Parameters:**
- `orderId` (required)
- `paymentType` (required) - TOKEN (25%) or FINAL (remaining)
- `amount` (required) - Amount to pay in INR

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Payment initiated",
  "data": {
    "transactionId": "txn_123",
    "orderId": "order_789",
    "amount": 5625.00,
    "currency": "INR",
    "paymentType": "TOKEN",
    "gateway": "RAZORPAY",
    "razorpayOrderId": "order_razorpay_123",
    "razorpayOrderKey": "rzp_test_key_123",
    "status": "INITIATED",
    "expiresAt": "2026-01-09T11:00:00Z"
  }
}
```

#### 2. Verify Payment

**POST** `/payments/verify?gatewayOrderId={gid}&gatewayPaymentId={pid}&signature={sig}`

**Authentication:** Required (Bearer Token)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Payment verified successfully",
  "data": {
    "transactionId": "txn_123",
    "orderId": "order_789",
    "amount": 5625.00,
    "status": "SUCCESS",
    "paymentMethod": "CARD",
    "verifiedAt": "2026-01-08T11:30:00Z"
  }
}
```

#### 3. Get User Transactions

**GET** `/payments/transactions?page=0&size=20`

**Authentication:** Required (Bearer Token)

#### 4. Get Transaction Details

**GET** `/payments/transactions/{transactionId}`

**Authentication:** Required (Bearer Token)

#### 5. Get Order Transactions

**GET** `/payments/order/{orderId}`

**Authentication:** Required (Bearer Token)

#### 6. Razorpay Webhook

**POST** `/payments/webhook/razorpay`

Automatically handles payment updates from Razorpay

#### 7. Stripe Webhook

**POST** `/payments/webhook/stripe`

Automatically handles payment updates from Stripe

---

## Review & Ratings APIs

Customers can review vendors after order completion. Vendors can respond to reviews.

**Base Endpoint:** `/reviews`

#### 1. Create Review

**POST** `/reviews`

**Authentication:** Required (Bearer Token)

**Request Body:**
```json
{
  "orderId": "order_789",
  "vendorId": "vendor_123",
  "rating": 5,
  "foodQualityRating": 5,
  "serviceQualityRating": 4,
  "hygieneRating": 5,
  "valueForMoneyRating": 4,
  "punctualityRating": 5,
  "reviewText": "Excellent catering service! Food was delicious and staff was very professional.",
  "images": [
    "https://storage.example.com/review_123_1.jpg",
    "https://storage.example.com/review_123_2.jpg"
  ]
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Review submitted successfully",
  "data": {
    "reviewId": "review_123",
    "orderId": "order_789",
    "vendorId": "vendor_123",
    "rating": 5,
    "reviewText": "Excellent catering service!",
    "status": "APPROVED",
    "createdAt": "2026-01-10T15:00:00Z"
  }
}
```

#### 2. Get Review

**GET** `/reviews/{reviewId}`

#### 3. Get Vendor Reviews

**GET** `/reviews/vendor/{vendorId}?page=0&size=20`

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "reviewId": "review_123",
      "orderId": "order_789",
      "userId": "user_123",
      "userName": "John Doe",
      "rating": 5,
      "reviewText": "Excellent catering service!",
      "foodQualityRating": 5,
      "serviceQualityRating": 4,
      "images": ["url1", "url2"],
      "helpfulCount": 12,
      "vendorResponse": {
        "responseText": "Thank you for your kind words!",
        "respondedAt": "2026-01-10T16:00:00Z"
      },
      "createdAt": "2026-01-10T15:00:00Z"
    }
  ],
  "pageInfo": {
    "totalElements": 150,
    "totalPages": 8,
    "currentPage": 0
  }
}
```

#### 4. Get My Reviews

**GET** `/reviews/my?page=0&size=20`

**Authentication:** Required (Bearer Token)

#### 5. Add Vendor Response

**POST** `/reviews/{reviewId}/vendor-response?responseText=Thank you for your feedback`

**Authentication:** Required (Bearer Token - Vendor Role)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Response added",
  "data": {
    "reviewId": "review_123",
    "vendorResponse": {
      "responseText": "Thank you for your feedback",
      "respondedAt": "2026-01-10T16:00:00Z"
    }
  }
}
```

#### 6. Mark as Helpful

**POST** `/reviews/{reviewId}/helpful`

**Authentication:** Required (Bearer Token)

#### 7. Report Review

**POST** `/reviews/{reviewId}/report?reason=Inappropriate content`

**Authentication:** Required (Bearer Token)

---

## Cart Management APIs

Cart system for vendors selling through the platform (not bid-based).

**Base Endpoint:** `/cart`

#### 1. Get Cart

**GET** `/cart`

**Authentication:** Required (Bearer Token)

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "cartItemId": "cart_item_123",
      "vendorItemId": "vendor_item_123",
      "vendorId": "vendor_123",
      "vendorName": "Delicious Catering Co.",
      "itemName": "Paneer Tikka",
      "quantity": 100,
      "pricePerUnit": 150.00,
      "totalPrice": 15000.00
    },
    {
      "cartItemId": "cart_item_124",
      "vendorItemId": "vendor_item_124",
      "vendorId": "vendor_123",
      "itemName": "Butter Chicken",
      "quantity": 50,
      "pricePerUnit": 200.00,
      "totalPrice": 10000.00
    }
  ]
}
```

#### 2. Get Cart Grouped by Vendor

**GET** `/cart/grouped`

**Authentication:** Required (Bearer Token)

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "vendor_123": [
      {
        "cartItemId": "cart_item_123",
        "itemName": "Paneer Tikka",
        "quantity": 100,
        "totalPrice": 15000.00
      },
      {
        "cartItemId": "cart_item_124",
        "itemName": "Butter Chicken",
        "quantity": 50,
        "totalPrice": 10000.00
      }
    ],
    "vendor_456": [
      {
        "cartItemId": "cart_item_125",
        "itemName": "Biryani",
        "quantity": 75,
        "totalPrice": 18750.00
      }
    ]
  }
}
```

#### 3. Add to Cart

**POST** `/cart/items?vendorItemId={vendorItemId}&quantity=100`

**Authentication:** Required (Bearer Token)

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Item added to cart",
  "data": {
    "cartItemId": "cart_item_123",
    "itemName": "Paneer Tikka",
    "quantity": 100,
    "totalPrice": 15000.00
  }
}
```

#### 4. Update Cart Item

**PUT** `/cart/items/{cartItemId}?quantity=120`

**Authentication:** Required (Bearer Token)

#### 5. Remove from Cart

**DELETE** `/cart/items/{cartItemId}`

**Authentication:** Required (Bearer Token)

#### 6. Clear Cart

**DELETE** `/cart`

**Authentication:** Required (Bearer Token)

#### 7. Get Cart Count

**GET** `/cart/count`

**Authentication:** Required (Bearer Token)

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "count": 225
  }
}
```

#### 8. Get Cart Total

**GET** `/cart/total`

**Authentication:** Required (Bearer Token)

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "total": 43000.00
  }
}
```

---

## Analytics & Dashboard APIs

**Base Endpoint:** `/analytics`

### Admin Analytics

#### 1. Platform Overview

**GET** `/analytics/overview`

**Authentication:** Required (Bearer Token - Admin Role)

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "totalUsers": 5000,
    "activeUsers": 2500,
    "totalVendors": 250,
    "activeVendors": 200,
    "totalOrders": 5000,
    "completedOrders": 4500,
    "totalRevenue": 5000000.00,
    "platformFees": 500000.00,
    "ordersByStatus": {
      "PENDING": 50,
      "CONFIRMED": 300,
      "COMPLETED": 4500,
      "CANCELLED": 150
    }
  }
}
```

#### 2. Revenue Analytics

**GET** `/analytics/revenue?period=month`

**Authentication:** Required (Bearer Token - Admin Role)

**Query Parameters:**
- `period` - week, month, quarter, year

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "period": "month",
    "totalRevenue": 500000.00,
    "platformFees": 50000.00,
    "vendorPayouts": 400000.00,
    "refunds": 5000.00,
    "pendingPayments": 45000.00
  }
}
```

#### 3. User Analytics

**GET** `/analytics/users`

**Authentication:** Required (Bearer Token - Admin Role)

#### 4. Vendor Analytics

**GET** `/analytics/vendors`

**Authentication:** Required (Bearer Token - Admin Role)

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "totalVendors": 250,
    "activeVendors": 200,
    "pendingApproval": 15,
    "verifiedVendors": 180
  }
}
```

#### 5. Order Analytics

**GET** `/analytics/orders?period=month`

**Authentication:** Required (Bearer Token - Admin Role)

#### 6. Generate Report

**GET** `/analytics/reports?reportType=REVENUE&startDate=2026-01-01&endDate=2026-01-31`

**Authentication:** Required (Bearer Token - Admin Role)

---

### Vendor Dashboard

#### 7. Vendor Dashboard

**GET** `/analytics/vendor/dashboard`

**Authentication:** Required (Bearer Token - Vendor Role)

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "vendorId": "vendor_123",
    "businessName": "Delicious Catering Co.",
    "stats": {
      "totalOrders": 500,
      "completedOrders": 495,
      "cancelledOrders": 5,
      "totalRevenue": 1000000.00,
      "averageOrderValue": 2000.00
    },
    "ratings": {
      "averageRating": 4.5,
      "totalReviews": 150,
      "ratingBreakdown": {
        "5star": 100,
        "4star": 35,
        "3star": 12,
        "2star": 2,
        "1star": 1
      }
    },
    "recentOrders": [
      {
        "orderId": "order_789",
        "customerName": "John Doe",
        "eventDate": "2026-06-15T18:00:00Z",
        "totalAmount": 22500.00,
        "status": "CONFIRMED"
      }
    ],
    "upcomingEvents": [
      {
        "orderId": "order_790",
        "customerName": "Jane Smith",
        "eventDate": "2026-07-20T18:00:00Z",
        "guestCount": 200,
        "status": "CONFIRMED"
      }
    ]
  }
}
```

---

## File Upload APIs

**Base Endpoint:** `/upload`

#### 1. Upload Image

**POST** `/upload/image`

**Content-Type:** multipart/form-data

**Authentication:** Required (Bearer Token)

**Form Parameters:**
- `file` (required) - Image file (JPEG, PNG, GIF, WebP - max 5MB)
- `entityType` (optional) - VENDOR, MENU_ITEM, etc.
- `entityId` (optional) - ID of the entity

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Image uploaded successfully",
  "data": {
    "fileId": "file_123",
    "fileName": "vendor_logo.jpg",
    "fileUrl": "https://storage.example.com/uploads/vendor_logo.jpg",
    "fileSize": 256000,
    "uploadedAt": "2026-01-08T12:00:00Z"
  }
}
```

#### 2. Upload Document

**POST** `/upload/document`

**Content-Type:** multipart/form-data

**Authentication:** Required (Bearer Token)

**Form Parameters:**
- `file` (required) - Document file (PDF, DOC, DOCX - max 10MB)
- `entityType` (optional)
- `entityId` (optional)

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Document uploaded successfully",
  "data": {
    "fileId": "file_124",
    "fileName": "gst_certificate.pdf",
    "fileUrl": "https://storage.example.com/uploads/gst_certificate.pdf",
    "fileSize": 512000,
    "uploadedAt": "2026-01-08T12:00:00Z"
  }
}
```

#### 3. Get File Info

**GET** `/upload/{fileId}`

**Authentication:** Required (Bearer Token)

#### 4. Get Files for Entity

**GET** `/upload/entity/{entityType}/{entityId}`

**Authentication:** Required (Bearer Token)

#### 5. Delete File

**DELETE** `/upload/{fileId}`

**Authentication:** Required (Bearer Token)

---

## Vendor Workflow & Integration

### Complete Vendor Registration & Order Workflow

```
1. USER REGISTRATION
   └─ POST /auth/register
   └─ Response: User created with CUSTOMER role

2. VENDOR REGISTRATION
   └─ POST /vendors (with bearer token)
   └─ Status: PENDING_APPROVAL
   └─ Upload documents: POST /upload/document

3. ADMIN APPROVAL
   └─ GET /vendors/admin/pending (admin only)
   └─ POST /vendors/{vendorId}/approve (admin only)
   └─ Status changes to: ACTIVE

4. VENDOR SETUP
   ├─ POST /upload/image (upload logo/banner)
   ├─ POST /menu/vendor-items (add menu items)
   │  └─ Multiple calls for different items
   └─ PATCH /menu/vendor-items/{id}/availability (set availability)

5. CUSTOMER CREATES BID REQUEST
   └─ POST /bids/requests (customer)
   └─ Vendor receives notification

6. VENDOR SUBMITS BID
   ├─ GET /bids/vendor/received (see available requests)
   └─ POST /bids/requests/{bidRequestId}/submit-bid (submit bid)

7. BID ACCEPTANCE
   ├─ Customer: GET /bids/requests/{bidRequestId}/bids (see all bids)
   └─ Customer: POST /bids/{bidId}/accept (accept best bid)

8. ORDER CREATION
   └─ POST /orders?bidRequestId={bidId} (create order)
   └─ Status: PENDING_TOKEN_PAYMENT

9. PAYMENT PROCESSING
   ├─ POST /payments/initiate (start payment)
   ├─ Customer pays via payment gateway
   └─ POST /payments/verify (verify payment)

10. ORDER CONFIRMATION
    ├─ Status: CONFIRMED
    ├─ Vendor: GET /orders/vendor (see vendor orders)
    └─ Customer: GET /orders/{orderId} (view order details)

11. FULFILLMENT
    ├─ Vendor: PATCH /orders/{orderId}/status (update status)
    └─ Customer: GET /orders/upcoming (track upcoming orders)

12. COMPLETION
    ├─ Order status: DELIVERED
    ├─ Customer: POST /reviews (submit review)
    ├─ Vendor: POST /reviews/{reviewId}/vendor-response (respond)
    └─ Vendor ratings updated

13. ANALYTICS
    ├─ Vendor: GET /analytics/vendor/dashboard (view performance)
    ├─ Admin: GET /analytics/overview (platform metrics)
    └─ Admin: GET /analytics/vendors (vendor metrics)
```

### Postman Collection Template

For each endpoint, use this structure in Postman:

```json
{
  "info": {
    "name": "Bidzaro Vendor APIs",
    "description": "Complete vendor ecosystem APIs",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Vendor Registration",
      "item": [
        {
          "name": "Register as Vendor",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{accessToken}}",
                "type": "text"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\"businessName\": \"...\", ...}"
            },
            "url": {
              "raw": "http://localhost:8080/api/v1/vendors",
              "protocol": "http",
              "host": ["localhost"],
              "port": "8080",
              "path": ["api", "v1", "vendors"]
            }
          }
        }
      ]
    }
  ]
}
```

### Environment Variables for Postman

```json
{
  "baseUrl": "http://localhost:8080/api/v1",
  "accessToken": "{{generated-jwt-token}}",
  "refreshToken": "{{refresh-token}}",
  "userId": "{{user-id}}",
  "vendorId": "{{vendor-id}}",
  "orderId": "{{order-id}}",
  "bidRequestId": "{{bid-request-id}}",
  "bidId": "{{bid-id}}"
}
```

---

## Error Handling

### Standard Error Response Format

```json
{
  "success": false,
  "message": "Error message",
  "data": null,
  "errors": [
    {
      "field": "fieldName",
      "message": "Detailed error message"
    }
  ]
}
```

### HTTP Status Codes

| Code | Meaning | Example |
|------|---------|---------|
| 200 | OK | Request successful |
| 201 | Created | Resource created |
| 400 | Bad Request | Invalid input |
| 401 | Unauthorized | Invalid/missing token |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Duplicate resource |
| 500 | Server Error | Internal error |

### Common Vendor Validation Errors

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    {
      "field": "businessName",
      "message": "Business name is required"
    },
    {
      "field": "businessEmail",
      "message": "Email already registered"
    },
    {
      "field": "businessPhone",
      "message": "Invalid phone format"
    },
    {
      "field": "businessAddress.city",
      "message": "City is required"
    },
    {
      "field": "capacity.minGuests",
      "message": "Minimum guests must be at least 10"
    },
    {
      "field": "pricing.startingPricePerPlate",
      "message": "Price must be positive"
    }
  ]
}
```

---

## Database Models

### MongoDB Collections Structure

```
bidzaro (Database)
├── users
│   ├── userId (indexed)
│   ├── email (indexed, unique)
│   ├── roles []
│   ├── status
│   └── createdAt
│
├── vendors
│   ├── vendorId (indexed, unique)
│   ├── userId (indexed)
│   ├── businessName
│   ├── businessEmail (indexed, unique)
│   ├── status
│   ├── approvalStatus
│   ├── verified
│   └── ratings {}
│
├── menu_items
│   ├── itemId (indexed)
│   ├── name
│   ├── category
│   └── status
│
├── vendor_menu_items
│   ├── vendorItemId (indexed)
│   ├── vendorId (indexed)
│   ├── itemId
│   ├── pricePerUnit
│   └── isAvailable
│
├── bid_requests
│   ├── bidRequestId (indexed)
│   ├── userId (indexed)
│   ├── eventDate
│   ├── guestCount
│   ├── status
│   └── totalBidsReceived
│
├── vendor_bids
│   ├── bidId (indexed)
│   ├── vendorId (indexed)
│   ├── bidRequestId (indexed)
│   ├── quotedPrice
│   └── status
│
├── orders
│   ├── orderId (indexed)
│   ├── userId (indexed)
│   ├── vendorId (indexed)
│   ├── eventDate
│   ├── status
│   ├── totalAmount
│   └── createdAt
│
├── transactions
│   ├── transactionId (indexed)
│   ├── orderId (indexed)
│   ├── userId (indexed)
│   ├── amount
│   ├── status
│   └── createdAt
│
├── reviews
│   ├── reviewId (indexed)
│   ├── orderId (indexed, unique)
│   ├── vendorId (indexed)
│   ├── userId (indexed)
│   ├── rating
│   └── vendorResponse {}
│
├── cart_items
│   ├── userId (indexed)
│   ├── vendorItemId
│   ├── quantity
│   └── addedAt
│
└── files
    ├── fileId (indexed)
    ├── userId (indexed)
    ├── entityType
    ├── entityId
    └── uploadedAt
```

---

## Testing Checklist

### Vendor Registration Flow
- [ ] Register new vendor
- [ ] Admin approves vendor
- [ ] Vendor profile updated
- [ ] Vendor can upload documents

### Menu Management
- [ ] Vendor adds menu items
- [ ] Vendor updates prices
- [ ] Vendor toggles availability
- [ ] Customer sees vendor menu

### Bidding System
- [ ] Customer creates bid request
- [ ] Vendor sees request
- [ ] Vendor submits bid
- [ ] Customer compares bids
- [ ] Customer accepts bid

### Order Management
- [ ] Order created from bid
- [ ] Order payments initiated
- [ ] Order confirmed
- [ ] Vendor tracks order
- [ ] Order delivered

### Reviews & Ratings
- [ ] Customer leaves review
- [ ] Vendor responds
- [ ] Ratings updated
- [ ] Reviews visible on profile

### Analytics
- [ ] Admin sees dashboard
- [ ] Vendor sees dashboard
- [ ] Revenue tracked
- [ ] Orders tracked

---

## Notes & Best Practices

1. **Authentication**
   - All protected endpoints require valid JWT token
   - Token expires in 15 minutes
   - Use refresh token to get new access token

2. **Pagination**
   - All list endpoints support pagination
   - Default page size: 20
   - Maximum page size: 100

3. **Sorting**
   - Most endpoints support `sortBy` and `sortDir` parameters
   - Default sort: createdAt descending

4. **File Uploads**
   - Image max size: 5MB (JPEG, PNG, GIF, WebP)
   - Document max size: 10MB (PDF, DOC, DOCX)
   - Files stored on cloud storage with CDN

5. **Payment Integration**
   - Razorpay for India
   - Stripe for USA
   - Token payment: 25% of total
   - Final payment: Remaining 75%

6. **Vendor Approval**
   - Required before accepting orders
   - Admin reviews business documents
   - Can be rejected with reason

7. **Ratings & Reviews**
   - Only after order completion
   - Automatically updates vendor rating
   - Vendor can respond to reviews

8. **Timezone**
   - All timestamps in UTC (ISO 8601)
   - Client should convert to local timezone

---

**Last Updated:** January 19, 2026  
**API Version:** v1  
**Documentation Version:** 2.0.0

