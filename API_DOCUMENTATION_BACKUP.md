# Catering Platform API Documentation

**Version:** 1.0.0  
**Base URL:** `http://localhost:8080/api/v1`  
**Date:** January 8, 2026

## Table of Contents
1. [Authentication](#authentication)
2. [Admin](#admin)
3. [Bids](#bids)
4. [Cart](#cart)
5. [Menu](#menu)
6. [Orders](#orders)
7. [Payments](#payments)
8. [Vendors](#vendors)
9. [Users](#users)
10. [Reviews](#reviews)
11. [Promo Codes](#promo-codes)
12. [Notifications](#notifications)
13. [Loyalty](#loyalty)
14. [Referral](#referral)
15. [Support](#support)
16. [Analytics](#analytics)
17. [File Upload](#file-upload)
18. [Wishlist](#wishlist)
19. [Chat](#chat)

---

## Authentication

### 1. Register User
**POST** `/auth/register`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+919876543210",
  "userType": "CUSTOMER"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Registration successful. Please verify your email.",
  "data": {
    "userId": "user_123456",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+919876543210",
    "userType": "CUSTOMER",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "refresh_token_here",
    "expiresIn": 900000
  }
}
```

### 2. Login
**POST** `/auth/login`

**Request Body:**
```json
{
  "identifier": "user@example.com",
  "password": "SecurePass123!"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": "user_123456",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "userType": "CUSTOMER",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "refresh_token_here",
    "expiresIn": 900000
  }
}
```

### 3. Refresh Token
**POST** `/auth/refresh-token`

**Request Body:**
```json
{
  "refreshToken": "refresh_token_here"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "accessToken": "new_access_token",
    "refreshToken": "new_refresh_token",
    "expiresIn": 900000
  }
}
```

### 4. Logout
**POST** `/auth/logout`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Logged out successfully",
  "data": null
}
```

### 5. Send OTP
**POST** `/auth/send-otp`

**Request Body:**
```json
{
  "identifier": "user@example.com",
  "type": "EMAIL"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "OTP sent successfully",
  "data": {
    "identifier": "user@example.com",
    "type": "EMAIL",
    "expiresAt": "2026-01-08T12:15:00Z",
    "verified": false
  }
}
```

### 6. Verify OTP
**POST** `/auth/verify-otp`

**Request Body:**
```json
{
  "identifier": "user@example.com",
  "otp": "123456",
  "type": "EMAIL"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "OTP verified successfully",
  "data": {
    "identifier": "user@example.com",
    "type": "EMAIL",
    "verified": true
  }
}
```

### 7. Forgot Password
**POST** `/auth/forgot-password`

**Request Body:**
```json
{
  "email": "user@example.com"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Password reset OTP sent to your email",
  "data": {
    "identifier": "user@example.com",
    "type": "EMAIL",
    "expiresAt": "2026-01-08T12:15:00Z"
  }
}
```

### 8. Reset Password
**POST** `/auth/reset-password`

**Request Body:**
```json
{
  "email": "user@example.com",
  "otp": "123456",
  "newPassword": "NewSecurePass123!"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Password reset successfully",
  "data": null
}
```

### 9. Change Password
**POST** `/auth/change-password`

**Headers:** `Authorization: Bearer {accessToken}`

**Request Body:**
```json
{
  "currentPassword": "OldPassword123!",
  "newPassword": "NewPassword123!"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Password changed successfully",
  "data": null
}
```

### 10. Get Current User
**GET** `/auth/me`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": null,
  "data": {
    "userId": "user_123456",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+919876543210",
    "userType": "CUSTOMER",
    "status": "ACTIVE",
    "profilePicture": "https://storage.example.com/profiles/user_123456.jpg",
    "createdAt": "2026-01-01T10:00:00Z"
  }
}
```

---

## Admin

### 1. Get Dashboard Stats
**GET** `/admin/dashboard`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "totalUsers": 5000,
    "totalVendors": 250,
    "totalOrders": 1500,
    "totalRevenue": 5000000.00,
    "pendingVendors": 15,
    "activeOrders": 45,
    "recentOrders": [],
    "revenueByMonth": []
  }
}
```

### 2. Get All Users
**GET** `/admin/users?page=0&size=20&status=ACTIVE&userType=CUSTOMER&sortBy=createdAt&sortDir=desc`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Users retrieved",
  "data": [
    {
      "userId": "user_123456",
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "userType": "CUSTOMER",
      "status": "ACTIVE"
    }
  ],
  "pageInfo": {
    "currentPage": 0,
    "totalPages": 10,
    "totalElements": 200,
    "pageSize": 20,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

### 3. Update User Status
**PATCH** `/admin/users/{userId}/status?status=SUSPENDED&reason=Violation`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "User status updated",
  "data": {
    "userId": "user_123456",
    "status": "SUSPENDED"
  }
}
```

### 4. Get Pending Vendors
**GET** `/admin/vendors/pending?page=0&size=20`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Pending vendors retrieved",
  "data": [
    {
      "vendorId": "vendor_123",
      "businessName": "Delicious Catering Co.",
      "status": "PENDING_APPROVAL"
    }
  ]
}
```

### 5. Approve Vendor
**POST** `/admin/vendors/{vendorId}/approve`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Vendor approved successfully",
  "data": {
    "vendorId": "vendor_123",
    "status": "ACTIVE"
  }
}
```

### 6. Reject Vendor
**POST** `/admin/vendors/{vendorId}/reject?reason=Incomplete documentation`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Vendor rejected",
  "data": {
    "vendorId": "vendor_123",
    "status": "REJECTED"
  }
}
```

### 7. Get Platform Config
**GET** `/admin/platform-config?country=India`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "country": "India",
    "currency": "INR",
    "platformFeePercentage": 2.0,
    "gstPercentage": 18.0,
    "minOrderAmount": 5000.00
  }
}
```

### 8. Update Platform Config
**PUT** `/admin/platform-config?country=India`

**Headers:** `Authorization: Bearer {accessToken}`

**Request Body:**
```json
{
  "platformFeePercentage": 2.5,
  "gstPercentage": 18.0,
  "minOrderAmount": 5000.00
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Configuration updated",
  "data": {
    "country": "India",
    "platformFeePercentage": 2.5
  }
}
```

---

## Bids

### 1. Create Bid Request
**POST** `/bids/requests`

**Headers:** `Authorization: Bearer {accessToken}`

**Request Body:**
```json
{
  "eventDetails": {
    "eventType": "WEDDING",
    "eventDate": "2026-06-15T18:00:00Z",
    "guestCount": 500,
    "venue": {
      "addressLine1": "Grand Hotel",
      "city": "Mumbai",
      "state": "Maharashtra",
      "pincode": "400001"
    }
  },
  "requirements": {
    "cuisinePreferences": ["North Indian", "South Indian"],
    "dietaryRestrictions": ["Vegetarian"],
    "serviceType": "BUFFET",
    "specialRequests": "Need live counter"
  },
  "budget": {
    "minBudget": 250000.00,
    "maxBudget": 350000.00
  }
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Bid request created successfully",
  "data": {
    "bidRequestId": "bid_req_123",
    "userId": "user_123456",
    "eventDetails": {
      "eventType": "WEDDING",
      "eventDate": "2026-06-15T18:00:00Z",
      "guestCount": 500
    },
    "status": "OPEN",
    "bidCount": 0,
    "createdAt": "2026-01-08T10:00:00Z"
  }
}
```

### 2. Get User's Bid Requests
**GET** `/bids/requests?page=0&size=20`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Bid requests retrieved",
  "data": [
    {
      "bidRequestId": "bid_req_123",
      "eventType": "WEDDING",
      "eventDate": "2026-06-15T18:00:00Z",
      "status": "OPEN",
      "bidCount": 5
    }
  ]
}
```

### 3. Get Bid Request Details
**GET** `/bids/requests/{bidRequestId}`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "bidRequestId": "bid_req_123",
    "eventDetails": {
      "eventType": "WEDDING",
      "eventDate": "2026-06-15T18:00:00Z",
      "guestCount": 500
    },
    "status": "OPEN",
    "bidCount": 5
  }
}
```

### 4. Submit Bid (Vendor)
**POST** `/bids/requests/{bidRequestId}/submit-bid`

**Headers:** `Authorization: Bearer {accessToken}`

**Request Body:**
```json
{
  "totalAmount": 300000.00,
  "menuItems": [
    {
      "vendorMenuItemId": "item_123",
      "quantity": 500
    }
  ],
  "proposal": "We offer authentic cuisine with 20 years of experience",
  "validUntil": "2026-02-08T23:59:59Z"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Bid submitted successfully",
  "data": {
    "bidId": "bid_456",
    "bidRequestId": "bid_req_123",
    "vendorId": "vendor_123",
    "totalAmount": 300000.00,
    "status": "SUBMITTED",
    "submittedAt": "2026-01-08T10:30:00Z"
  }
}
```

### 5. Get Bids for Request
**GET** `/bids/requests/{bidRequestId}/bids`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "bidId": "bid_456",
      "vendorId": "vendor_123",
      "vendorName": "Delicious Catering",
      "totalAmount": 300000.00,
      "status": "SUBMITTED",
      "rating": 4.5
    }
  ]
}
```

### 6. Accept Bid
**POST** `/bids/{bidId}/accept`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Bid accepted. Cooling period started.",
  "data": {
    "bidRequestId": "bid_req_123",
    "status": "BID_ACCEPTED",
    "acceptedBidId": "bid_456"
  }
}
```

### 7. Revise Bid (Vendor)
**PUT** `/bids/{bidId}?reason=Price adjustment`

**Headers:** `Authorization: Bearer {accessToken}`

**Request Body:**
```json
{
  "totalAmount": 280000.00,
  "proposal": "Revised proposal with discounted pricing"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Bid revised successfully",
  "data": {
    "bidId": "bid_456",
    "totalAmount": 280000.00,
    "revisionCount": 1
  }
}
```

### 8. Withdraw Bid (Vendor)
**DELETE** `/bids/{bidId}`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Bid withdrawn",
  "data": null
}
```

---

## Cart

### 1. Get Cart
**GET** `/cart`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "cartItemId": "cart_item_1",
      "vendorItemId": "vendor_item_123",
      "itemName": "Paneer Tikka",
      "quantity": 100,
      "pricePerUnit": 150.00,
      "totalPrice": 15000.00
    }
  ]
}
```

### 2. Get Cart Grouped by Vendor
**GET** `/cart/grouped`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "vendor_123": [
      {
        "cartItemId": "cart_item_1",
        "itemName": "Paneer Tikka",
        "quantity": 100,
        "totalPrice": 15000.00
      }
    ]
  }
}
```

### 3. Add to Cart
**POST** `/cart/items?vendorItemId=vendor_item_123&quantity=100`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Item added to cart",
  "data": {
    "cartItemId": "cart_item_1",
    "vendorItemId": "vendor_item_123",
    "quantity": 100,
    "totalPrice": 15000.00
  }
}
```

### 4. Update Cart Item
**PUT** `/cart/items/{cartItemId}?quantity=150`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Cart item updated",
  "data": {
    "cartItemId": "cart_item_1",
    "quantity": 150,
    "totalPrice": 22500.00
  }
}
```

### 5. Remove from Cart
**DELETE** `/cart/items/{cartItemId}`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Item removed from cart",
  "data": null
}
```

### 6. Clear Cart
**DELETE** `/cart`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Cart cleared",
  "data": null
}
```

### 7. Get Cart Count
**GET** `/cart/count`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "count": 5
  }
}
```

### 8. Get Cart Total
**GET** `/cart/total`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "total": 75000.00
  }
}
```

---

## Menu

### 1. Get All Categories
**GET** `/menu/categories`

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "categoryId": "cat_1",
      "name": "Starters",
      "description": "Appetizers and starters",
      "icon": "🍢",
      "isActive": true
    }
  ]
}
```

### 2. Get All Menu Items
**GET** `/menu/items?page=0&size=20`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Menu items retrieved",
  "data": [
    {
      "itemId": "item_123",
      "name": "Paneer Tikka",
      "categoryId": "cat_1",
      "categoryName": "Starters",
      "description": "Marinated cottage cheese cubes grilled to perfection",
      "isVeg": true,
      "isVegan": false,
      "spiceLevel": "MEDIUM"
    }
  ]
}
```

### 3. Get Vendor Menu Items
**GET** `/menu/vendor-items?vendorId=vendor_123&page=0&size=20`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Vendor menu items retrieved",
  "data": [
    {
      "vendorItemId": "vendor_item_123",
      "vendorId": "vendor_123",
      "itemId": "item_123",
      "itemName": "Paneer Tikka",
      "pricePerUnit": 150.00,
      "minOrderQuantity": 50,
      "isAvailable": true
    }
  ]
}
```

### 4. Add Item to Vendor Menu (Vendor)
**POST** `/menu/vendor-items`

**Headers:** `Authorization: Bearer {accessToken}`

**Request Body:**
```json
{
  "masterItemId": "item_123",
  "pricePerUnit": 150.00,
  "minOrderQuantity": 50,
  "maxOrderQuantity": 500,
  "preparationTime": 30,
  "customizations": ["Spice level", "Extra cheese"]
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Item added to menu",
  "data": {
    "vendorItemId": "vendor_item_123",
    "itemName": "Paneer Tikka",
    "pricePerUnit": 150.00,
    "isAvailable": true
  }
}
```

### 5. Update Vendor Menu Item (Vendor)
**PUT** `/menu/vendor-items/{vendorItemId}`

**Headers:** `Authorization: Bearer {accessToken}`

**Request Body:**
```json
{
  "pricePerUnit": 160.00,
  "minOrderQuantity": 50
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Item updated",
  "data": {
    "vendorItemId": "vendor_item_123",
    "pricePerUnit": 160.00
  }
}
```

### 6. Update Item Availability (Vendor)
**PATCH** `/menu/vendor-items/{vendorItemId}/availability?isAvailable=false&reason=Out of stock`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Availability updated",
  "data": {
    "vendorItemId": "vendor_item_123",
    "isAvailable": false
  }
}
```

---

## Orders

### 1. Create Order from Bid
**POST** `/orders?bidRequestId=bid_req_123`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Order created. Please complete token payment.",
  "data": {
    "orderId": "order_789",
    "bidRequestId": "bid_req_123",
    "vendorId": "vendor_123",
    "totalAmount": 300000.00,
    "tokenAmount": 75000.00,
    "status": "PENDING_TOKEN_PAYMENT",
    "createdAt": "2026-01-08T11:00:00Z"
  }
}
```

### 2. Get User's Orders
**GET** `/orders?page=0&size=20`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Orders retrieved",
  "data": [
    {
      "orderId": "order_789",
      "vendorName": "Delicious Catering",
      "eventDate": "2026-06-15T18:00:00Z",
      "totalAmount": 300000.00,
      "status": "CONFIRMED",
      "createdAt": "2026-01-08T11:00:00Z"
    }
  ]
}
```

### 3. Get Order Details
**GET** `/orders/{orderId}`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "orderId": "order_789",
    "vendorId": "vendor_123",
    "vendorName": "Delicious Catering",
    "eventDetails": {
      "eventType": "WEDDING",
      "eventDate": "2026-06-15T18:00:00Z",
      "guestCount": 500
    },
    "totalAmount": 300000.00,
    "tokenAmount": 75000.00,
    "status": "CONFIRMED",
    "paymentStatus": "TOKEN_PAID"
  }
}
```

### 4. Update Order Status
**PATCH** `/orders/{orderId}/status?status=IN_PROGRESS`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Order status updated",
  "data": {
    "orderId": "order_789",
    "status": "IN_PROGRESS"
  }
}
```

### 5. Cancel Order
**POST** `/orders/{orderId}/cancel?reason=Event postponed`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Order cancelled",
  "data": {
    "orderId": "order_789",
    "status": "CANCELLED",
    "refundAmount": 70000.00
  }
}
```

---

## Payments

### 1. Initiate Payment
**POST** `/payments/initiate?orderId=order_789&paymentType=TOKEN_PAYMENT&amount=75000.00`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Payment initiated",
  "data": {
    "gatewayOrderId": "rzp_order_123456",
    "amount": 75000.00,
    "currency": "INR",
    "keyId": "rzp_key_xxx"
  }
}
```

### 2. Verify Payment
**POST** `/payments/verify?gatewayOrderId=rzp_order_123456&gatewayPaymentId=rzp_pay_789&signature=abc123`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Payment verified successfully",
  "data": {
    "transactionId": "txn_111",
    "orderId": "order_789",
    "amount": 75000.00,
    "status": "SUCCESS",
    "paymentType": "TOKEN_PAYMENT"
  }
}
```

### 3. Get User Transactions
**GET** `/payments/transactions?page=0&size=20`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Transactions retrieved",
  "data": [
    {
      "transactionId": "txn_111",
      "orderId": "order_789",
      "amount": 75000.00,
      "paymentType": "TOKEN_PAYMENT",
      "status": "SUCCESS",
      "createdAt": "2026-01-08T11:30:00Z"
    }
  ]
}
```

---

## Vendors

### 1. Register as Vendor
**POST** `/vendors`

**Headers:** `Authorization: Bearer {accessToken}`

**Request Body:**
```json
{
  "businessName": "Delicious Catering Co.",
  "description": "Premium catering services for all occasions",
  "cuisineTypes": ["North Indian", "South Indian", "Chinese"],
  "serviceTypes": ["BUFFET", "PLATED", "LIVE_COUNTER"],
  "address": {
    "addressLine1": "123 Main Street",
    "city": "Mumbai",
    "state": "Maharashtra",
    "pincode": "400001"
  },
  "contactDetails": {
    "phone": "+919876543210",
    "email": "contact@deliciouscatering.com"
  },
  "documents": {
    "fssaiLicense": "url_to_fssai",
    "gstNumber": "27XXXXX1234Z1Z5"
  }
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Vendor registered successfully. Pending approval.",
  "data": {
    "vendorId": "vendor_123",
    "businessName": "Delicious Catering Co.",
    "status": "PENDING_APPROVAL",
    "createdAt": "2026-01-08T12:00:00Z"
  }
}
```

### 2. Get All Vendors
**GET** `/vendors?page=0&size=20&city=Mumbai&cuisine=North Indian`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Vendors retrieved successfully",
  "data": [
    {
      "vendorId": "vendor_123",
      "businessName": "Delicious Catering Co.",
      "cuisineTypes": ["North Indian", "South Indian"],
      "rating": 4.5,
      "reviewCount": 150,
      "city": "Mumbai"
    }
  ]
}
```

### 3. Get Vendor by ID
**GET** `/vendors/{vendorId}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "vendorId": "vendor_123",
    "businessName": "Delicious Catering Co.",
    "description": "Premium catering services",
    "cuisineTypes": ["North Indian", "South Indian"],
    "rating": 4.5,
    "reviewCount": 150,
    "minOrderAmount": 10000.00
  }
}
```

### 4. Update Vendor Profile
**PUT** `/vendors/{vendorId}`

**Headers:** `Authorization: Bearer {accessToken}`

**Request Body:**
```json
{
  "description": "Updated description",
  "minOrderAmount": 12000.00
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Vendor updated successfully",
  "data": {
    "vendorId": "vendor_123",
    "description": "Updated description"
  }
}
```

### 5. Search Vendors
**GET** `/vendors/search?query=catering&city=Mumbai&rating=4.0&page=0&size=20`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Search results",
  "data": [
    {
      "vendorId": "vendor_123",
      "businessName": "Delicious Catering Co.",
      "rating": 4.5
    }
  ]
}
```

---

## Users

### 1. Get Profile
**GET** `/users/profile`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "userId": "user_123456",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+919876543210",
    "profilePicture": "https://storage.example.com/profiles/user_123456.jpg"
  }
}
```

### 2. Update Profile
**PUT** `/users/profile`

**Headers:** `Authorization: Bearer {accessToken}`

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Smith",
  "phone": "+919876543210",
  "bio": "Event planner and food enthusiast"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Profile updated",
  "data": {
    "userId": "user_123456",
    "firstName": "John",
    "lastName": "Smith"
  }
}
```

### 3. Get Addresses
**GET** `/users/addresses`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "addressId": "addr_1",
      "label": "Home",
      "addressLine1": "123 Main St",
      "city": "Mumbai",
      "state": "Maharashtra",
      "pincode": "400001",
      "isDefault": true
    }
  ]
}
```

### 4. Add Address
**POST** `/users/addresses`

**Headers:** `Authorization: Bearer {accessToken}`

**Request Body:**
```json
{
  "label": "Office",
  "addressLine1": "456 Business Park",
  "addressLine2": "Floor 3",
  "city": "Mumbai",
  "state": "Maharashtra",
  "pincode": "400002",
  "latitude": 19.0760,
  "longitude": 72.8777
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Address added",
  "data": {
    "addressId": "addr_2",
    "label": "Office",
    "city": "Mumbai"
  }
}
```

### 5. Update Address
**PUT** `/users/addresses/{addressId}`

**Headers:** `Authorization: Bearer {accessToken}`

**Request Body:**
```json
{
  "label": "Work",
  "addressLine1": "456 Business Park - Updated"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Address updated",
  "data": {
    "addressId": "addr_2",
    "label": "Work"
  }
}
```

### 6. Delete Address
**DELETE** `/users/addresses/{addressId}`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Address deleted",
  "data": null
}
```

### 7. Set Default Address
**PATCH** `/users/addresses/{addressId}/default`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Default address set",
  "data": {
    "addressId": "addr_2",
    "isDefault": true
  }
}
```

---

## Reviews

### 1. Create Review
**POST** `/reviews`

**Headers:** `Authorization: Bearer {accessToken}`

**Request Body:**
```json
{
  "orderId": "order_789",
  "vendorId": "vendor_123",
  "rating": 5,
  "foodQuality": 5,
  "serviceQuality": 5,
  "valueForMoney": 4,
  "comment": "Excellent food and service!",
  "images": ["url1", "url2"]
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Review submitted",
  "data": {
    "reviewId": "review_1",
    "orderId": "order_789",
    "vendorId": "vendor_123",
    "rating": 5,
    "comment": "Excellent food and service!"
  }
}
```

### 2. Get Vendor Reviews
**GET** `/reviews/vendor/{vendorId}?page=0&size=20`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Reviews retrieved",
  "data": [
    {
      "reviewId": "review_1",
      "userName": "John Doe",
      "rating": 5,
      "comment": "Excellent food and service!",
      "createdAt": "2026-01-08T15:00:00Z"
    }
  ]
}
```

### 3. Update Review
**PUT** `/reviews/{reviewId}`

**Headers:** `Authorization: Bearer {accessToken}`

**Request Body:**
```json
{
  "rating": 4,
  "comment": "Updated: Very good food"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Review updated",
  "data": {
    "reviewId": "review_1",
    "rating": 4
  }
}
```

### 4. Delete Review
**DELETE** `/reviews/{reviewId}`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Review deleted",
  "data": null
}
```

---

## Promo Codes

### 1. Get Available Promos
**GET** `/promos/available`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "promoCodeId": "promo_1",
      "code": "WELCOME50",
      "discountType": "PERCENTAGE",
      "discountValue": 10.0,
      "maxDiscount": 500.00,
      "minOrderAmount": 5000.00
    }
  ]
}
```

### 2. Apply Promo Code
**POST** `/promos/apply`

**Headers:** `Authorization: Bearer {accessToken}`

**Request Body:**
```json
{
  "code": "WELCOME50",
  "orderAmount": 10000.00
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "promoCodeId": "promo_1",
    "code": "WELCOME50",
    "discountAmount": 500.00,
    "finalAmount": 9500.00
  }
}
```

---

## Notifications

### 1. Get Notifications
**GET** `/notifications?page=0&size=20`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Notifications retrieved",
  "data": [
    {
      "notificationId": "notif_1",
      "title": "Bid Accepted",
      "message": "Your bid has been accepted!",
      "type": "BID_UPDATE",
      "isRead": false,
      "createdAt": "2026-01-08T16:00:00Z"
    }
  ]
}
```

### 2. Get Unread Count
**GET** `/notifications/count`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "unreadCount": 5
  }
}
```

### 3. Mark as Read
**PATCH** `/notifications/{notificationId}/read`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Marked as read",
  "data": null
}
```

### 4. Mark All as Read
**PATCH** `/notifications/read-all`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "All marked as read",
  "data": null
}
```

---

## Loyalty

### 1. Get Loyalty Points
**GET** `/loyalty/points`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "userId": "user_123456",
    "totalPoints": 1500,
    "availablePoints": 1200,
    "redeemedPoints": 300,
    "expiringPoints": 200,
    "expiryDate": "2026-06-30T23:59:59Z"
  }
}
```

### 2. Get Points History
**GET** `/loyalty/transactions?page=0&size=20`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Transactions retrieved",
  "data": [
    {
      "transactionId": "lty_txn_1",
      "type": "EARNED",
      "points": 500,
      "description": "Order #order_789",
      "createdAt": "2026-01-08T17:00:00Z"
    }
  ]
}
```

---

## Referral

### 1. Get My Referral Code
**GET** `/referral/my-code`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "code": "JOHN1234",
    "referralUrl": "https://app.example.com/register?ref=JOHN1234",
    "rewardPoints": 500
  }
}
```

### 2. Get Referral Stats
**GET** `/referral/stats`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "totalReferrals": 10,
    "successfulReferrals": 7,
    "totalPointsEarned": 3500,
    "pendingRewards": 1000
  }
}
```

### 3. Validate Referral Code
**GET** `/referral/validate/{code}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "valid": true
  }
}
```

---

## Support

### 1. Create Support Ticket
**POST** `/support/tickets`

**Headers:** `Authorization: Bearer {accessToken}`

**Request Body:**
```json
{
  "subject": "Payment issue",
  "description": "Unable to complete payment for order",
  "priority": "HIGH",
  "category": "PAYMENT",
  "orderId": "order_789"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Ticket created",
  "data": {
    "ticketId": "ticket_1",
    "ticketNumber": "TKT-2026-001",
    "subject": "Payment issue",
    "status": "OPEN",
    "priority": "HIGH",
    "createdAt": "2026-01-08T18:00:00Z"
  }
}
```

### 2. Get My Tickets
**GET** `/support/tickets?page=0&size=20`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Tickets retrieved",
  "data": [
    {
      "ticketId": "ticket_1",
      "ticketNumber": "TKT-2026-001",
      "subject": "Payment issue",
      "status": "OPEN",
      "createdAt": "2026-01-08T18:00:00Z"
    }
  ]
}
```

### 3. Add Message to Ticket
**POST** `/support/tickets/{ticketId}/messages`

**Headers:** `Authorization: Bearer {accessToken}`

**Request Body:**
```json
{
  "message": "I tried again but still facing the same issue",
  "attachments": ["url1"]
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Message added",
  "data": {
    "messageId": "msg_1",
    "ticketId": "ticket_1",
    "message": "I tried again but still facing the same issue",
    "createdAt": "2026-01-08T18:30:00Z"
  }
}
```

---

## Analytics

### 1. Get User Dashboard
**GET** `/analytics/user/dashboard`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "totalOrders": 15,
    "totalSpent": 450000.00,
    "activeBidRequests": 2,
    "loyaltyPoints": 1500,
    "upcomingEvents": 3
  }
}
```

### 2. Get Vendor Dashboard (Vendor)
**GET** `/analytics/vendor/dashboard`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "totalOrders": 125,
    "totalRevenue": 3750000.00,
    "activeBids": 8,
    "averageRating": 4.5,
    "completionRate": 98.5
  }
}
```

---

## File Upload

### 1. Upload File
**POST** `/upload`

**Headers:** 
- `Authorization: Bearer {accessToken}`
- `Content-Type: multipart/form-data`

**Form Data:**
- `file`: (binary file)
- `type`: "PROFILE_PICTURE" | "DOCUMENT" | "MENU_IMAGE" | "REVIEW_IMAGE"

**Response (201 Created):**
```json
{
  "success": true,
  "message": "File uploaded successfully",
  "data": {
    "fileId": "file_123",
    "fileName": "profile.jpg",
    "fileUrl": "https://storage.example.com/uploads/file_123.jpg",
    "fileSize": 245678,
    "contentType": "image/jpeg",
    "uploadedAt": "2026-01-08T19:00:00Z"
  }
}
```

---

## Wishlist

### 1. Get Wishlist
**GET** `/wishlist`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "wishlistItemId": "wish_1",
      "vendorId": "vendor_123",
      "vendorName": "Delicious Catering Co.",
      "addedAt": "2026-01-08T20:00:00Z"
    }
  ]
}
```

### 2. Add to Wishlist
**POST** `/wishlist?vendorId=vendor_123`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Added to wishlist",
  "data": {
    "wishlistItemId": "wish_1",
    "vendorId": "vendor_123"
  }
}
```

### 3. Remove from Wishlist
**DELETE** `/wishlist/{wishlistItemId}`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Removed from wishlist",
  "data": null
}
```

---

## Chat

### 1. Get Conversations
**GET** `/chat/conversations?page=0&size=20`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "conversationId": "conv_1",
      "participantId": "vendor_123",
      "participantName": "Delicious Catering",
      "lastMessage": "Hello, how can I help?",
      "unreadCount": 2,
      "updatedAt": "2026-01-08T21:00:00Z"
    }
  ]
}
```

### 2. Get Messages
**GET** `/chat/conversations/{conversationId}/messages?page=0&size=50`

**Headers:** `Authorization: Bearer {accessToken}`

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "messageId": "msg_1",
      "senderId": "user_123456",
      "message": "Hi, I need catering for 500 people",
      "timestamp": "2026-01-08T21:00:00Z",
      "isRead": true
    }
  ]
}
```

### 3. Send Message
**POST** `/chat/conversations/{conversationId}/messages`

**Headers:** `Authorization: Bearer {accessToken}`

**Request Body:**
```json
{
  "message": "What's your availability for June 15?",
  "attachments": []
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "messageId": "msg_2",
    "conversationId": "conv_1",
    "message": "What's your availability for June 15?",
    "timestamp": "2026-01-08T21:05:00Z"
  }
}
```

---

## Error Responses

All endpoints may return the following error responses:

### 400 Bad Request
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    {
      "field": "email",
      "message": "Invalid email format"
    }
  ]
}
```

### 401 Unauthorized
```json
{
  "success": false,
  "message": "Unauthorized access. Please login.",
  "timestamp": "2026-01-08T10:00:00Z"
}
```

### 403 Forbidden
```json
{
  "success": false,
  "message": "Access forbidden. Insufficient permissions.",
  "timestamp": "2026-01-08T10:00:00Z"
}
```

### 404 Not Found
```json
{
  "success": false,
  "message": "Resource not found",
  "timestamp": "2026-01-08T10:00:00Z"
}
```

### 409 Conflict
```json
{
  "success": false,
  "message": "Email already exists",
  "timestamp": "2026-01-08T10:00:00Z"
}
```

### 500 Internal Server Error
```json
{
  "success": false,
  "message": "An unexpected error occurred",
  "timestamp": "2026-01-08T10:00:00Z"
}
```

---

## Notes

1. **Authentication**: Most endpoints require a Bearer token in the Authorization header
2. **Pagination**: Use `page` (0-indexed) and `size` parameters for paginated endpoints
3. **Date Format**: All dates are in ISO 8601 format (UTC)
4. **Currency**: All amounts are in INR (Indian Rupees)
5. **File Uploads**: Maximum file size is 10MB for documents and 5MB for images

---

**Last Updated:** January 8, 2026

