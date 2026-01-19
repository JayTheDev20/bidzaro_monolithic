# Vendor API Documentation

This document lists all API endpoints available for **Vendors**, including profile management, menu management, bid handling, and order processing.

## Authentication (`/auth`)

### Register as Vendor
**POST** `/auth/register`
```json
{
  "email": "vendor@example.com",
  "phone": "+1234567890",
  "password": "Password@123",
  "firstName": "Vendor",
  "lastName": "User",
  "userType": "VENDOR"
}
```
*Note: After registering as a user, you must create a vendor profile.*

### Login
**POST** `/auth/login`

## Vendor Profile (`/vendors`)

### Create Vendor Profile
**POST** `/vendors`
```json
{
  "businessName": "Tasty Catering",
  "businessEmail": "contact@tasty.com",
  "businessPhone": "+1234567890",
  "businessType": "CATERING",
  "businessRegistrationNumber": "REG123456",
  "taxId": "TAX123",
  "description": "Best catering in town",
  "establishedYear": 2010,
  "cuisinesOffered": ["Italian", "French"],
  "specialties": ["Pasta", "Desserts"],
  "country": "USA",
  "businessAddress": {
    "streetAddress": "123 Food St",
    "city": "New York",
    "state": "NY",
    "postalCode": "10001",
    "country": "USA",
    "latitude": 40.7128,
    "longitude": -74.0060
  },
  "ownerInfo": {
    "firstName": "Vendor",
    "lastName": "Owner",
    "phone": "+1234567890",
    "email": "owner@tasty.com",
    "idProofType": "PASSPORT",
    "idProofNumber": "P1234567"
  },
  "serviceAreas": [
    { "city": "New York", "state": "NY", "radiusKm": 50 }
  ],
  "capacity": {
    "minGuests": 10,
    "maxGuests": 500,
    "concurrentEvents": 2
  },
  "pricing": {
    "currency": "USD",
    "startingPricePerPlate": 20.00,
    "averagePricePerPlate": 35.00
  }
}
```
*Business Types: CATERING, RESTAURANT, CLOUD_KITCHEN, HOME_CHEF, BAKERY*

**Response:**
```json
{
  "success": true,
  "message": "Vendor registered successfully. Pending approval.",
  "data": {
    "vendorId": "vendor_1",
    "businessName": "Tasty Catering",
    "status": "PENDING"
  }
}
```

### Manage Profile
*   **GET** `/vendors/me` - Get my vendor profile
*   **PUT** `/vendors/{vendorId}` - Update profile details

## Menu Management (`/menu`)

### Add Item to Menu
**POST** `/menu/vendor-items`
```json
{
  "masterItemId": "item_1",
  "customName": "Special Pasta",
  "customDescription": "With secret sauce",
  "pricePerPlate": 15.50,
  "minimumOrderQuantity": 10,
  "discountPercentage": 0.0,
  "isAvailable": true,
  "advanceNoticeHours": 24,
  "maxDailyCapacity": 100,
  "preparationTimeMinutes": 60,
  "customizationOptions": [
    {
      "optionName": "Spice Level",
      "choices": ["Mild", "Medium", "Hot"],
      "additionalCost": 0.0,
      "isRequired": true
    }
  ]
}
```
**Response:**
```json
{
  "success": true,
  "message": "Item added to menu",
  "data": {
    "vendorItemId": "v_item_2",
    "name": "Special Pasta",
    "price": 15.50
  }
}
```

### Manage Items
*   **GET** `/menu/vendor-items?vendorId={myId}` - List my items
*   **PUT** `/menu/vendor-items/{id}` - Update item price/details
*   **PATCH** `/menu/vendor-items/{id}/availability?isAvailable=false` - Toggle availability
*   **DELETE** `/menu/vendor-items/{id}` - Remove item

## Bidding (`/bids`)

### Find Opportunities
*   **GET** `/bids/active` - View all active bid requests
*   **GET** `/bids/vendor/received` - View requests specifically targeted to me

### Submit Bid
**POST** `/bids/requests/{requestId}/submit-bid`
```json
{
  "quotedPrice": {
    "currency": "USD",
    "subtotal": 4000.00,
    "serviceCharge": 200.00,
    "taxPercentage": 5.0,
    "taxAmount": 210.00,
    "totalAmount": 4410.00
  },
  "itemizedPricing": [
    {
      "vendorItemId": "v_item_1",
      "itemName": "Pasta",
      "quantity": 50,
      "pricePerPlate": 15.00,
      "totalPrice": 750.00
    }
  ],
  "deliveryDetails": {
    "estimatedSetupTime": "2 hours",
    "foodReadyTime": "17:30",
    "cleanupTime": "22:30"
  },
  "staffProvided": {
    "chefs": 2,
    "servers": 4,
    "cleaners": 1
  },
  "termsAndConditions": "50% advance required",
  "validityPeriodHours": 48
}
```
**Response:**
```json
{
  "success": true,
  "message": "Bid submitted successfully",
  "data": {
    "bidId": "bid_789",
    "amount": 4410.00,
    "status": "SUBMITTED"
  }
}
```

### Manage Bids
*   **GET** `/bids/vendor/submitted` - View my submitted bids
*   **PUT** `/bids/{bidId}` - Revise a bid
*   **DELETE** `/bids/{bidId}` - Withdraw a bid

## Order Management (`/orders`)

### View Orders
*   **GET** `/orders/vendor` - List all orders assigned to me
*   **GET** `/orders/{orderId}` - View specific order details

### Update Status
**PATCH** `/orders/{orderId}/status?status=PREPARING`
*Statuses: CONFIRMED, PREPARING, OUT_FOR_DELIVERY, COMPLETED*

## Analytics (`/analytics`)

### Vendor Dashboard
**GET** `/analytics/vendor/dashboard`
*Returns sales data, order counts, and ratings.*

## Reviews (`/reviews`)

### View Reviews
*   **GET** `/reviews/vendor/{myVendorId}` - See what customers are saying

### Respond to Review
**POST** `/reviews/{reviewId}/vendor-response`
*Query Param: responseText*

## Chat (`/chat`)

*   **GET** `/chat/conversations` - List customer chats
*   **POST** `/chat/messages` - Reply to customers
