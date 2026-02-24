# 👤 USER API DOCUMENTATION
## Bidzaro Catering Platform — Complete User Guide

**Version:** 1.0.0
**Base URL:** `http://localhost:8080/api/v1`
**Target Audience:** Customer / End-User (Mobile App & Web)
**Authentication:** JWT Bearer Token

---

## 📋 Table of Contents

1. [Authentication & Registration](#1-authentication--registration)
2. [User Profile Management](#2-user-profile-management)
3. [Address Management](#3-address-management)
4. [Vendor Discovery](#4-vendor-discovery)
5. [Menu Browsing](#5-menu-browsing)
6. [Cart Management](#6-cart-management)
7. [Bidding System](#7-bidding-system)
8. [Order Management](#8-order-management)
9. [Payment](#9-payment)
10. [Chat with Vendors](#10-chat-with-vendors)
11. [Reviews & Ratings](#11-reviews--ratings)
12. [Support Tickets](#12-support-tickets)
13. [Promo Codes](#13-promo-codes)
14. [Loyalty Points](#14-loyalty-points)
15. [Referral Program](#15-referral-program)
16. [Wishlist](#16-wishlist)
17. [Notifications](#17-notifications)

---

## 🔐 How Authentication Works

```
1. Register  →  POST /auth/register       → Get accessToken + refreshToken
2. Login     →  POST /auth/login          → Get accessToken + refreshToken
3. Use APIs  →  Add Header: Authorization: Bearer {accessToken}
4. Expired?  →  POST /auth/refresh-token  → Get new accessToken
```

---

# 1. Authentication & Registration

## 1.1 Register New User

**Use:** Create a new customer account.

```http
POST /auth/register
Content-Type: application/json
```

### Request Payload
```json
{
  "email": "john.doe@gmail.com",
  "phone": "+917890123456",
  "password": "MyPass@123",
  "firstName": "John",
  "lastName": "Doe",
  "userType": "USER",
  "country": "INDIA",
  "fcmToken": "firebase_device_token_here",
  "deviceInfo": "Samsung Galaxy S24 - Android 14"
}
```

### Field Rules
| Field | Required | Rules |
|-------|----------|-------|
| `email` | ✅ | Valid email format, must be unique |
| `phone` | ✅ | E.164 format (+91XXXXXXXXXX), must be unique |
| `password` | ✅ | Min 8 chars, 1 uppercase, 1 number, 1 special char |
| `firstName` | ✅ | Max 100 chars |
| `lastName` | ✅ | Max 100 chars |
| `userType` | ✅ | Must be `USER` for customers |
| `country` | ✅ | `INDIA` or `USA` |
| `fcmToken` | ❌ | Firebase token for push notifications |
| `deviceInfo` | ❌ | Device details string |

### Success Response `201 Created`
```json
{
  "success": true,
  "statusCode": 201,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiI1NTBlODQwMCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiI1NTBlODQwMCJ9...",
    "expiresIn": 604800,
    "user": {
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "email": "john.doe@gmail.com",
      "phone": "+917890123456",
      "firstName": "John",
      "lastName": "Doe",
      "fullName": "John Doe",
      "userType": "USER",
      "status": "PENDING_VERIFICATION",
      "country": "INDIA",
      "preferredCurrency": "INR",
      "emailVerified": false,
      "phoneVerified": false,
      "createdAt": "2026-02-24T10:30:45.123Z"
    }
  },
  "timestamp": "2026-02-24T10:30:45.123Z"
}
```

### Error Responses
```json
// 409 — Email already registered
{
  "success": false,
  "statusCode": 409,
  "error": { "code": "EMAIL_EXISTS", "message": "Email is already registered" }
}

// 409 — Phone already registered
{
  "success": false,
  "statusCode": 409,
  "error": { "code": "PHONE_EXISTS", "message": "Phone number is already registered" }
}

// 422 — Validation failed
{
  "success": false,
  "statusCode": 422,
  "error": {
    "code": "VALIDATION_ERROR",
    "fieldErrors": {
      "password": "Password must have at least 1 uppercase letter",
      "phone": "Phone must be in E.164 format e.g. +917890123456"
    }
  }
}
```

---

## 1.2 Login

**Use:** Sign in with email or phone number.

```http
POST /auth/login
Content-Type: application/json
```

### Request Payload
```json
{
  "identifier": "john.doe@gmail.com",
  "password": "MyPass@123",
  "fcmToken": "firebase_device_token_here",
  "deviceInfo": "Samsung Galaxy S24 - Android 14"
}
```

> **Note:** `identifier` can be either email OR phone number.

### Field Rules
| Field | Required | Rules |
|-------|----------|-------|
| `identifier` | ✅ | Email or phone number |
| `password` | ✅ | User's password |
| `fcmToken` | ❌ | Updated push notification token |
| `deviceInfo` | ❌ | Device info string |

### Success Response `200 OK`
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 604800,
    "user": {
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "email": "john.doe@gmail.com",
      "firstName": "John",
      "lastName": "Doe",
      "userType": "USER",
      "status": "ACTIVE",
      "country": "INDIA",
      "preferredCurrency": "INR",
      "lastLoginAt": "2026-02-24T10:30:45.123Z"
    }
  }
}
```

### Error Responses
```json
// 401 — Wrong credentials
{
  "success": false,
  "statusCode": 401,
  "error": { "code": "INVALID_CREDENTIALS", "message": "Email or phone invalid" }
}

// 401 — Account locked after 5 failed attempts
{
  "success": false,
  "statusCode": 401,
  "error": {
    "code": "ACCOUNT_LOCKED",
    "message": "Account is locked due to multiple failed login attempts. Please reset your password."
  }
}

// 401 — Suspended account
{
  "success": false,
  "statusCode": 401,
  "error": { "code": "ACCOUNT_SUSPENDED", "message": "Your account has been suspended" }
}
```

---

## 1.3 Refresh Access Token

**Use:** Get a new access token when the current one expires (401 response).

```http
POST /auth/refresh-token
Content-Type: application/json
```

### Request Payload
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "statusCode": 200,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...(new token)...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...(new token)...",
    "expiresIn": 604800,
    "tokenType": "Bearer"
  }
}
```

---

## 1.4 Send OTP

**Use:** Send OTP to verify email or phone number.

```http
POST /auth/send-otp
Content-Type: application/json
```

### Request Payload
```json
{
  "identifier": "john.doe@gmail.com",
  "type": "EMAIL"
}
```

| Field | Required | Values |
|-------|----------|--------|
| `identifier` | ✅ | Email or phone |
| `type` | ✅ | `EMAIL` or `PHONE` |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "expiresIn": 600,
    "message": "OTP sent to john.doe@gmail.com. Valid for 10 minutes."
  }
}
```

---

## 1.5 Verify OTP

**Use:** Verify the OTP received on email or phone.

```http
POST /auth/verify-otp
Content-Type: application/json
```

### Request Payload
```json
{
  "identifier": "john.doe@gmail.com",
  "otp": "482910",
  "type": "EMAIL"
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "verified": true,
    "message": "Email verified successfully"
  }
}
```

---

## 1.6 Forgot Password

**Use:** Request a password reset link via email.

```http
POST /auth/forgot-password
Content-Type: application/json
```

### Request Payload
```json
{
  "email": "john.doe@gmail.com"
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "message": "Password reset link sent to john.doe@gmail.com"
  }
}
```

---

## 1.7 Reset Password

**Use:** Set a new password using the reset token from email.

```http
POST /auth/reset-password
Content-Type: application/json
```

### Request Payload
```json
{
  "token": "reset-token-from-email-link",
  "newPassword": "NewPass@456"
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "message": "Password reset successfully. Please login with new password."
  }
}
```

---

## 1.8 Change Password (While Logged In)

**Use:** Change password when the user is already authenticated.

```http
POST /auth/change-password
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "currentPassword": "MyPass@123",
  "newPassword": "NewPass@456"
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": { "message": "Password changed successfully" }
}
```

---

## 1.9 Recover Forgotten Email

**Use:** Find your registered email using your phone number.

```http
POST /auth/recover/forgot-email
Content-Type: application/json
```

### Request Payload
```json
{
  "phone": "+917890123456"
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "maskedEmail": "j***@gmail.com",
    "message": "Email hint sent to your phone"
  }
}
```

---

# 2. User Profile Management

## 2.1 Get My Profile

**Use:** Fetch current user's full profile information.

```http
GET /users/profile
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
    "profilePictureUrl": "http://localhost:8080/uploads/images/profile-123.jpg",
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
    "lastLoginAt": "2026-02-24T10:30:45.123Z",
    "createdAt": "2026-01-10T08:00:00.000Z",
    "updatedAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 2.2 Update My Profile

**Use:** Update name, gender, date of birth, language, currency preference.

```http
PUT /users/profile
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "gender": "MALE",
  "dateOfBirth": "1992-06-15",
  "preferredLanguage": "en",
  "preferredCurrency": "INR"
}
```

| Field | Required | Values |
|-------|----------|--------|
| `gender` | ❌ | `MALE`, `FEMALE`, `OTHER`, `PREFER_NOT_TO_SAY` |
| `dateOfBirth` | ❌ | Format: `YYYY-MM-DD` |
| `preferredCurrency` | ❌ | `INR` or `USD` |

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "firstName": "John",
    "lastName": "Doe",
    "gender": "MALE",
    "updatedAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 2.3 Update Profile Picture

**Use:** Update profile picture URL after uploading via file upload API.

```http
PATCH /users/profile-picture?imageUrl=http://localhost:8080/uploads/images/profile-123.jpg
Authorization: Bearer {accessToken}
```

### Query Parameters
| Param | Required | Description |
|-------|----------|-------------|
| `imageUrl` | ✅ | URL from File Upload API response |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "profilePictureUrl": "http://localhost:8080/uploads/images/profile-123.jpg",
    "message": "Profile picture updated"
  }
}
```

---

# 3. Address Management

## 3.1 Add New Address

**Use:** Save a delivery/event address for quick selection.

```http
POST /users/addresses
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "label": "Home",
  "streetAddress": "42, MG Road, Indiranagar",
  "city": "Bangalore",
  "state": "Karnataka",
  "postalCode": "560038",
  "country": "India",
  "latitude": 12.9716,
  "longitude": 77.5946,
  "isDefault": true
}
```

| Field | Required | Rules |
|-------|----------|-------|
| `label` | ✅ | e.g. Home, Office, Marriage Hall |
| `streetAddress` | ✅ | Full address |
| `city` | ✅ | City name |
| `state` | ✅ | State name |
| `postalCode` | ✅ | PIN code |
| `country` | ✅ | Country name |
| `latitude` | ❌ | From GPS or maps |
| `longitude` | ❌ | From GPS or maps |
| `isDefault` | ✅ | true/false |

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "addressId": "addr-abc123-def456",
    "label": "Home",
    "streetAddress": "42, MG Road, Indiranagar",
    "city": "Bangalore",
    "state": "Karnataka",
    "postalCode": "560038",
    "country": "India",
    "latitude": 12.9716,
    "longitude": 77.5946,
    "isDefault": true,
    "createdAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 3.2 Get All My Addresses

```http
GET /users/addresses
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "addressId": "addr-abc123-def456",
      "label": "Home",
      "streetAddress": "42, MG Road, Indiranagar",
      "city": "Bangalore",
      "state": "Karnataka",
      "postalCode": "560038",
      "isDefault": true
    },
    {
      "addressId": "addr-xyz789-uvw012",
      "label": "Office",
      "streetAddress": "10, Whitefield Main Road",
      "city": "Bangalore",
      "state": "Karnataka",
      "postalCode": "560066",
      "isDefault": false
    }
  ]
}
```

---

## 3.3 Delete Address

```http
DELETE /users/addresses/{addressId}
Authorization: Bearer {accessToken}
```

### Success Response `204 No Content`
```
HTTP 204 No Content
```

---

# 4. Vendor Discovery

## 4.1 Browse All Vendors (Public)

**Use:** Browse all active catering vendors with filters.

```http
GET /vendors?page=0&size=20&city=Bangalore&cuisine=Indian
```

### Query Parameters
| Param | Description |
|-------|-------------|
| `page` | Page number (default 0) |
| `size` | Items per page (default 20) |
| `city` | Filter by city |
| `cuisine` | Filter by cuisine |
| `status` | Filter by status (default ACTIVE) |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "vendorId": "vendor-12345",
      "businessName": "Spice Garden Catering",
      "logoUrl": "http://localhost:8080/uploads/images/logo-spice.jpg",
      "description": "Authentic South Indian catering since 2010",
      "cuisinesOffered": ["South Indian", "North Indian"],
      "ratings": {
        "averageRating": 4.7,
        "totalReviews": 312
      },
      "pricing": {
        "startingPricePerPlate": 350,
        "averagePricePerPlate": 500,
        "currency": "INR"
      },
      "capacity": { "maxGuests": 2000 },
      "serviceAreas": [{ "city": "Bangalore" }],
      "verified": true,
      "featured": false
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 85,
    "totalPages": 5,
    "hasNext": true
  }
}
```

---

## 4.2 Search Vendors

**Use:** Search vendors by name, city, cuisine, or minimum rating.

```http
GET /vendors/search?query=spice&city=Bangalore&cuisines=Indian,Chinese&rating=4&page=0&size=20
```

### Query Parameters
| Param | Description |
|-------|-------------|
| `query` | Search by name or keyword |
| `city` | City to filter |
| `cuisines` | Comma-separated list e.g. `Indian,Chinese` |
| `rating` | Minimum rating (1-5) |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "vendorId": "vendor-12345",
      "businessName": "Spice Garden Catering",
      "cuisinesOffered": ["South Indian", "North Indian"],
      "ratings": { "averageRating": 4.7, "totalReviews": 312 },
      "pricing": { "startingPricePerPlate": 350, "currency": "INR" }
    }
  ],
  "pageInfo": { "totalElements": 3, "totalPages": 1 }
}
```

---

## 4.3 Get Vendor Details (Public)

**Use:** View full vendor profile, menu, ratings, service areas.

```http
GET /vendors/{vendorId}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "vendorId": "vendor-12345",
    "businessName": "Spice Garden Catering",
    "businessEmail": "info@spicegarden.com",
    "businessPhone": "+917890123456",
    "logoUrl": "http://localhost:8080/uploads/images/logo-spice.jpg",
    "bannerUrl": "http://localhost:8080/uploads/images/banner-spice.jpg",
    "description": "Authentic South Indian catering since 2010",
    "establishedYear": 2010,
    "verified": true,
    "featured": false,
    "status": "ACTIVE",
    "country": "INDIA",
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
    "businessAddress": {
      "streetAddress": "25, 3rd Cross, Jayanagar",
      "city": "Bangalore",
      "state": "Karnataka",
      "postalCode": "560041"
    },
    "serviceAreas": [
      { "city": "Bangalore", "state": "Karnataka", "radiusKm": 30 }
    ],
    "cuisinesOffered": ["South Indian", "North Indian", "Chinese"],
    "specialties": ["Weddings", "Corporate Lunch", "Birthday Parties"],
    "capacity": { "minGuests": 20, "maxGuests": 2000, "concurrentEvents": 3 },
    "pricing": {
      "startingPricePerPlate": 350,
      "averagePricePerPlate": 500,
      "currency": "INR"
    },
    "stats": {
      "totalOrders": 600,
      "completedOrders": 596,
      "responseTimeMinutes": 12
    }
  }
}
```

---

# 5. Menu Browsing

## 5.1 Get All Menu Categories (Public)

```http
GET /menu/categories
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "categoryId": "cat-001",
      "categoryName": "Starters",
      "categoryNameHindi": "स्टार्टर",
      "iconUrl": "http://localhost:8080/icons/starters.svg",
      "displayOrder": 1,
      "status": "ACTIVE"
    },
    {
      "categoryId": "cat-002",
      "categoryName": "Main Course",
      "categoryNameHindi": "मुख्य व्यंजन",
      "iconUrl": "http://localhost:8080/icons/maincourse.svg",
      "displayOrder": 2,
      "status": "ACTIVE"
    },
    {
      "categoryId": "cat-003",
      "categoryName": "Desserts",
      "categoryNameHindi": "मिठाई",
      "iconUrl": "http://localhost:8080/icons/desserts.svg",
      "displayOrder": 3,
      "status": "ACTIVE"
    }
  ]
}
```

---

## 5.2 Get Menu Items (Public)

```http
GET /menu/items?page=0&size=20
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
      "cuisineType": "North Indian",
      "foodType": "VEGETARIAN",
      "spiceLevel": "MEDIUM",
      "dietaryTags": ["VEGETARIAN", "GLUTEN_FREE"],
      "allergens": ["DAIRY"],
      "imageUrls": ["http://localhost:8080/uploads/images/paneer-tikka.jpg"],
      "isPopular": true,
      "status": "ACTIVE"
    }
  ],
  "pageInfo": { "totalElements": 250, "totalPages": 13 }
}
```

---

## 5.3 Get Vendor's Menu Items (Public)

```http
GET /menu/vendor-items?vendorId=vendor-12345&page=0&size=20
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "vendorItemId": "vitem-001",
      "masterItemId": "item-001",
      "vendorId": "vendor-12345",
      "customName": "Spice Garden Special Paneer Tikka",
      "basePrice": 180,
      "effectivePrice": 162,
      "discountPercentage": 10,
      "isAvailable": true,
      "preparationTime": 25,
      "customizationOptions": [
        {
          "name": "Spice Level",
          "options": ["Mild", "Medium", "Spicy"],
          "required": false
        }
      ],
      "status": "ACTIVE"
    }
  ]
}
```

---

## 5.4 Search Menu Items (Public)

```http
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
      "categoryId": "cat-001",
      "foodType": "VEGETARIAN",
      "imageUrls": ["http://localhost:8080/uploads/images/paneer-tikka.jpg"]
    }
  ]
}
```

---

## 5.5 Get Simplified Vendor Menu

**Use:** Quick view of vendor's items with names and prices only.

```http
GET /vendors/{vendorId}/menu/simple
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    { "vendorItemId": "vitem-001", "itemName": "Paneer Tikka", "pricePerPlate": 162 },
    { "vendorItemId": "vitem-002", "itemName": "Butter Chicken", "pricePerPlate": 220 },
    { "vendorItemId": "vitem-003", "itemName": "Dal Makhani", "pricePerPlate": 140 }
  ]
}
```

---

# 6. Cart Management

## 6.1 Get My Cart

```http
GET /cart
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "cartItemId": "citem-001",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "vendorId": "vendor-12345",
      "vendorItemId": "vitem-001",
      "itemName": "Spice Garden Special Paneer Tikka",
      "quantity": 50,
      "pricePerPlate": 162,
      "totalPrice": 8100,
      "addedAt": "2026-02-24T09:00:00.000Z",
      "expiresAt": "2026-03-25T09:00:00.000Z"
    }
  ]
}
```

---

## 6.2 Add Item to Cart

```http
POST /cart/items
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "vendorItemId": "vitem-001",
  "quantity": 50
}
```

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "cartItemId": "citem-001",
    "vendorItemId": "vitem-001",
    "itemName": "Spice Garden Special Paneer Tikka",
    "quantity": 50,
    "pricePerPlate": 162,
    "totalPrice": 8100
  }
}
```

---

## 6.3 Batch Add Items to Cart

**Use:** After vendor selection from matching screen, add multiple items at once.

```http
POST /cart/items/batch
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "vendorId": "vendor-12345",
  "items": [
    { "masterItemId": "item-001", "quantity": 100 },
    { "masterItemId": "item-002", "quantity": 100 },
    { "masterItemId": "item-005", "quantity": 100 }
  ]
}
```

### Success Response `201 Created`
```json
{
  "success": true,
  "data": [
    {
      "cartItemId": "citem-001",
      "vendorItemId": "vitem-001",
      "itemName": "Paneer Tikka",
      "quantity": 100,
      "pricePerPlate": 162,
      "totalPrice": 16200
    },
    {
      "cartItemId": "citem-002",
      "vendorItemId": "vitem-002",
      "itemName": "Butter Chicken",
      "quantity": 100,
      "pricePerPlate": 220,
      "totalPrice": 22000
    }
  ]
}
```

---

## 6.4 Update Cart Item Quantity

```http
PUT /cart/items/{cartItemId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "quantity": 75
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "cartItemId": "citem-001",
    "quantity": 75,
    "totalPrice": 12150
  }
}
```

---

## 6.5 Remove Cart Item

```http
DELETE /cart/items/{cartItemId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Item removed from cart"
}
```

---

## 6.6 Clear Entire Cart

```http
DELETE /cart
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Cart cleared successfully"
}
```

---

# 7. Bidding System

## 7.1 Create Bid Request

**Use:** Post your event requirements so vendors can quote prices.

```http
POST /bids/requests
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "eventDetails": {
    "eventType": "WEDDING",
    "eventName": "Priya & Rahul Wedding",
    "eventDate": "2026-05-20T18:00:00Z",
    "eventStartTime": "18:00",
    "eventEndTime": "23:30",
    "numberOfGuests": 500,
    "venueAddress": {
      "streetAddress": "Palace Grounds, Jayamahal",
      "city": "Bangalore",
      "state": "Karnataka",
      "postalCode": "560080",
      "country": "India",
      "latitude": 12.9850,
      "longitude": 77.5950
    }
  },
  "menuItems": [
    { "masterItemId": "item-001", "itemName": "Paneer Tikka", "quantity": 500 },
    { "masterItemId": "item-002", "itemName": "Butter Chicken", "quantity": 400 },
    { "masterItemId": "item-010", "itemName": "Gulab Jamun", "quantity": 500 }
  ],
  "additionalRequirements": {
    "serviceStaffNeeded": true,
    "numberOfStaff": 25,
    "decorationNeeded": false,
    "liveCounters": true,
    "specialInstructions": "Separate veg and non-veg counters. Food ready by 6:30 PM."
  },
  "budget": {
    "estimatedBudget": 200000,
    "budgetRange": "200000-250000",
    "currency": "INR"
  },
  "targetedVendors": []
}
```

### Field Rules
| Field | Required | Values / Rules |
|-------|----------|----------------|
| `eventType` | ✅ | `WEDDING`, `CORPORATE`, `BIRTHDAY`, `ANNIVERSARY`, `PRIVATE_PARTY` |
| `eventDate` | ✅ | ISO 8601, must be future date |
| `numberOfGuests` | ✅ | Integer > 0 |
| `menuItems` | ✅ | Min 1 item |
| `budget.estimatedBudget` | ✅ | Number > 0 |
| `targetedVendors` | ❌ | Specific vendor IDs, or empty `[]` for all |

### Success Response `201 Created`
```json
{
  "success": true,
  "message": "Bid request created successfully",
  "data": {
    "bidRequestId": "breq-88990-77665",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "ACTIVE",
    "eventDetails": {
      "eventType": "WEDDING",
      "eventName": "Priya & Rahul Wedding",
      "eventDate": "2026-05-20T18:00:00Z",
      "numberOfGuests": 500,
      "venueAddress": { "city": "Bangalore", "state": "Karnataka" }
    },
    "menuItems": [
      { "masterItemId": "item-001", "itemName": "Paneer Tikka", "quantity": 500 }
    ],
    "budget": { "estimatedBudget": 200000, "currency": "INR" },
    "competitivePeriod": {
      "startTime": "2026-02-24T10:30:45.123Z",
      "endTime": "2026-02-27T10:30:45.123Z",
      "status": "ACTIVE"
    },
    "expiresAt": "2026-03-03T10:30:45.123Z",
    "createdAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 7.2 Create Bid Requests from Cart

**Use:** Automatically create bid requests using items currently in your cart.

```http
POST /bids/requests/from-cart
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "eventDetails": {
    "eventType": "BIRTHDAY",
    "eventName": "Rahul's 30th Birthday",
    "eventDate": "2026-03-15T19:00:00Z",
    "numberOfGuests": 100,
    "venueAddress": {
      "streetAddress": "42, MG Road",
      "city": "Bangalore",
      "state": "Karnataka",
      "postalCode": "560001",
      "country": "India"
    }
  },
  "additionalRequirements": {
    "serviceStaffNeeded": true,
    "numberOfStaff": 5,
    "specialInstructions": "Birthday cake service needed at 9 PM"
  },
  "budget": {
    "estimatedBudget": 25000,
    "currency": "INR"
  }
}
```

### Success Response `201 Created`
```json
{
  "success": true,
  "message": "Bid requests created from cart",
  "data": [
    {
      "bidRequestId": "breq-11111-22222",
      "targetedVendors": ["vendor-12345"],
      "status": "ACTIVE",
      "menuItems": [
        { "itemName": "Paneer Tikka", "quantity": 100 }
      ]
    }
  ]
}
```

---

## 7.3 Get My Bid Requests

```http
GET /bids/requests?page=0&size=20&status=ACTIVE
Authorization: Bearer {accessToken}
```

| Status Values | Description |
|---------------|-------------|
| `ACTIVE` | Open for vendor bids |
| `ACCEPTED` | Bid accepted, cooling period |
| `EXPIRED` | No vendor responded |
| `CANCELLED` | User cancelled |

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "bidRequestId": "breq-88990-77665",
      "status": "ACTIVE",
      "eventDetails": {
        "eventType": "WEDDING",
        "eventName": "Priya & Rahul Wedding",
        "eventDate": "2026-05-20T18:00:00Z",
        "numberOfGuests": 500
      },
      "budget": { "estimatedBudget": 200000, "currency": "INR" },
      "competitivePeriod": {
        "status": "ACTIVE",
        "endTime": "2026-02-27T10:30:45.123Z"
      },
      "createdAt": "2026-02-24T10:30:45.123Z"
    }
  ],
  "pageInfo": { "totalElements": 3, "totalPages": 1 }
}
```

---

## 7.4 View Bids Received for My Request

**Use:** See all vendor quotes/bids for your bid request.

```http
GET /bids/requests/{bidRequestId}/bids
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "bidId": "bid-vendor-001",
      "vendorId": "vendor-12345",
      "vendorName": "Spice Garden Catering",
      "vendorRating": 4.7,
      "status": "PENDING",
      "quotedPrice": {
        "subtotal": 175000,
        "serviceCharge": 17500,
        "taxAmount": 17500,
        "totalAmount": 210000,
        "currency": "INR"
      },
      "itemizedPricing": [
        {
          "itemName": "Paneer Tikka",
          "quantity": 500,
          "pricePerPlate": 175,
          "totalPrice": 87500
        },
        {
          "itemName": "Butter Chicken",
          "quantity": 400,
          "pricePerPlate": 220,
          "totalPrice": 88000
        }
      ],
      "validityPeriodHours": 48,
      "submittedAt": "2026-02-24T12:00:00.000Z",
      "revisionCount": 0
    },
    {
      "bidId": "bid-vendor-002",
      "vendorId": "vendor-67890",
      "vendorName": "Royal Feast Catering",
      "vendorRating": 4.5,
      "status": "PENDING",
      "quotedPrice": {
        "totalAmount": 195000,
        "currency": "INR"
      },
      "submittedAt": "2026-02-24T13:30:00.000Z"
    }
  ]
}
```

---

## 7.5 Accept a Bid

**Use:** Accept vendor's quote. This starts the 24-hour cooling period.

```http
POST /bids/{bidId}/accept
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Bid accepted. Cooling period started.",
  "data": {
    "bidId": "bid-vendor-001",
    "bidRequestId": "breq-88990-77665",
    "status": "ACCEPTED",
    "acceptedAt": "2026-02-24T10:30:45.123Z",
    "coolingPeriod": {
      "startTime": "2026-02-24T10:30:45.123Z",
      "endTime": "2026-02-25T10:30:45.123Z",
      "durationHours": 24
    },
    "message": "You can proceed with payment after the 24-hour cooling period ends at 2026-02-25T10:30:45.123Z"
  }
}
```

---

## 7.6 Cancel Bid Request

```http
DELETE /bids/requests/{bidRequestId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Bid request cancelled"
}
```

---

# 8. Order Management

## 8.1 Create Order (After Cooling Period + Payment)

**Use:** Confirm and create an order after bid acceptance and token payment.

```http
POST /orders?bidRequestId=breq-88990-77665
Authorization: Bearer {accessToken}
```

### Success Response `201 Created`
```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "orderId": "order-54321-12345",
    "bidRequestId": "breq-88990-77665",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
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
        "state": "Karnataka"
      }
    },
    "vendorOrders": [
      {
        "vendorOrderId": "vorder-001",
        "vendorId": "vendor-12345",
        "vendorName": "Spice Garden Catering",
        "vendorStatus": "ACCEPTED",
        "deliveryStatus": "PENDING",
        "items": [
          {
            "itemName": "Paneer Tikka",
            "quantity": 500,
            "pricePerPlate": 175,
            "totalPrice": 87500
          }
        ],
        "totalAmount": 210000
      }
    ],
    "pricing": {
      "subtotal": 175000,
      "serviceCharges": 17500,
      "taxAmount": 17500,
      "platformFee": 4200,
      "discountAmount": 0,
      "totalAmount": 214200,
      "currency": "INR"
    },
    "paymentDetails": {
      "tokenAmount": 53550,
      "tokenPaid": true,
      "balanceDue": 160650,
      "paymentStatus": "TOKEN_PAID"
    },
    "createdAt": "2026-02-25T11:00:00.000Z"
  }
}
```

---

## 8.2 Get All My Orders

```http
GET /orders?page=0&size=20&status=CONFIRMED
Authorization: Bearer {accessToken}
```

### Order Status Values
| Status | Meaning |
|--------|---------|
| `CONFIRMED` | Order confirmed, awaiting preparation |
| `IN_PREPARATION` | Vendor preparing food |
| `READY_FOR_DELIVERY` | Food ready to dispatch |
| `DELIVERED` | Food delivered to venue |
| `COMPLETED` | Event done, all confirmed |
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
        "eventName": "Priya & Rahul Wedding",
        "eventDate": "2026-05-20",
        "numberOfGuests": 500
      },
      "vendorOrders": [
        { "vendorName": "Spice Garden Catering", "deliveryStatus": "PENDING" }
      ],
      "pricing": { "totalAmount": 214200, "currency": "INR" },
      "paymentDetails": { "paymentStatus": "TOKEN_PAID", "balanceDue": 160650 },
      "createdAt": "2026-02-25T11:00:00.000Z"
    }
  ],
  "pageInfo": { "totalElements": 5, "totalPages": 1 }
}
```

---

## 8.3 Get Order Details

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
    "userId": "550e8400-e29b-41d4-a716-446655440000",
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
        "state": "Karnataka",
        "postalCode": "560080"
      }
    },
    "vendorOrders": [
      {
        "vendorOrderId": "vorder-001",
        "vendorName": "Spice Garden Catering",
        "vendorStatus": "ACCEPTED",
        "deliveryStatus": "PENDING",
        "items": [
          { "itemName": "Paneer Tikka", "quantity": 500, "pricePerPlate": 175, "totalPrice": 87500 },
          { "itemName": "Butter Chicken", "quantity": 400, "pricePerPlate": 220, "totalPrice": 88000 }
        ]
      }
    ],
    "pricing": {
      "subtotal": 175000,
      "serviceCharges": 17500,
      "taxAmount": 17500,
      "platformFee": 4200,
      "totalAmount": 214200,
      "currency": "INR"
    },
    "paymentDetails": {
      "tokenAmount": 53550,
      "tokenPaid": true,
      "tokenPaidAt": "2026-02-25T11:00:00.000Z",
      "totalPaid": 53550,
      "balanceDue": 160650,
      "paymentStatus": "TOKEN_PAID"
    },
    "specialInstructions": "Separate veg and non-veg counters.",
    "createdAt": "2026-02-25T11:00:00.000Z"
  }
}
```

---

## 8.4 Cancel Order

```http
POST /orders/{orderId}/cancel?reason=Event+postponed
Authorization: Bearer {accessToken}
```

### Query Parameters
| Param | Required | Description |
|-------|----------|-------------|
| `reason` | ✅ | Cancellation reason |

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Order cancelled",
  "data": {
    "orderId": "order-54321-12345",
    "status": "CANCELLED",
    "cancellation": {
      "cancelledBy": "USER",
      "cancellationReason": "Event postponed",
      "cancelledAt": "2026-02-25T11:00:00.000Z",
      "refundStatus": "INITIATED",
      "refundAmount": 40162.50
    }
  }
}
```

---

# 9. Payment

## 9.1 Initiate Payment

**Use:** Start the payment process for token payment or full payment.

```http
POST /payments/initiate
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload — Token Payment (25% upfront)
```json
{
  "bidId": "bid-vendor-001",
  "amount": 53550,
  "paymentType": "TOKEN"
}
```

### Request Payload — Full/Balance Payment
```json
{
  "orderId": "order-54321-12345",
  "amount": 160650,
  "paymentType": "FULL"
}
```

| `paymentType` | Description |
|---------------|-------------|
| `TOKEN` | 25% token amount to confirm bid |
| `FULL` | Full order payment |
| `PARTIAL` | Partial balance payment |

### Success Response `200 OK` — India (Razorpay)
```json
{
  "success": true,
  "data": {
    "transactionId": "txn-99887-66554",
    "gatewayOrderId": "order_RazorpayOrderId123",
    "gatewayName": "RAZORPAY",
    "amount": 53550,
    "currency": "INR",
    "keyId": "rzp_live_xxxxxxxxxxxx",
    "message": "Use Razorpay SDK with this order ID and key ID to complete payment"
  }
}
```

### Success Response `200 OK` — USA (Stripe)
```json
{
  "success": true,
  "data": {
    "transactionId": "txn-99887-66554",
    "gatewayOrderId": "pi_StripePaymentIntentId",
    "gatewayName": "STRIPE",
    "amount": 641.94,
    "currency": "USD",
    "clientSecret": "pi_xxxxx_secret_yyyyy",
    "publishableKey": "pk_live_xxxxxxxxxxxx",
    "message": "Use Stripe SDK with clientSecret to complete payment"
  }
}
```

---

## 9.2 Verify Payment

**Use:** Verify payment after the user completes payment on gateway.

```http
POST /payments/verify
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload — Razorpay
```json
{
  "gatewayOrderId": "order_RazorpayOrderId123",
  "gatewayPaymentId": "pay_RazorpayPaymentId456",
  "signature": "razorpay_signature_hash_here"
}
```

### Request Payload — Stripe
```json
{
  "gatewayOrderId": "pi_StripePaymentIntentId",
  "gatewayPaymentId": "pi_StripePaymentIntentId"
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Payment verified. Order confirmed.",
  "data": {
    "transactionId": "txn-99887-66554",
    "orderId": "order-54321-12345",
    "status": "SUCCESS",
    "amount": 53550,
    "currency": "INR",
    "processedAt": "2026-02-25T11:15:00.000Z"
  }
}
```

---

## 9.3 Get Payment History

```http
GET /payments/my-transactions?page=0&size=20
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "transactionId": "txn-99887-66554",
      "orderId": "order-54321-12345",
      "paymentType": "TOKEN",
      "amount": 53550,
      "currency": "INR",
      "status": "SUCCESS",
      "paymentGateway": "RAZORPAY",
      "processedAt": "2026-02-25T11:15:00.000Z"
    }
  ],
  "pageInfo": { "totalElements": 8, "totalPages": 1 }
}
```

---

# 10. Chat with Vendors

## 10.1 Start Conversation

```http
POST /chat/conversations
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "vendorId": "vendor-12345",
  "type": "USER_VENDOR"
}
```

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "conversationId": "conv-77665-88990",
    "conversationType": "USER_VENDOR",
    "status": "ACTIVE",
    "participants": [
      { "userId": "550e8400-e29b-41d4-a716-446655440000", "userType": "USER", "name": "John Doe" },
      { "userId": "vendor-user-12345", "userType": "VENDOR", "name": "Spice Garden Catering" }
    ],
    "createdAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 10.2 Get All Conversations

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
        { "name": "Spice Garden Catering", "userType": "VENDOR" }
      ],
      "lastMessage": {
        "message": "Yes, we can do 500 guests!",
        "senderId": "vendor-user-12345",
        "timestamp": "2026-02-24T11:00:00.000Z"
      },
      "unreadCount": 1
    }
  ]
}
```

---

## 10.3 Get Chat History

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
      "senderId": "550e8400-e29b-41d4-a716-446655440000",
      "senderType": "USER",
      "message": "Hi, can you cater for 500 guests on May 20?",
      "messageType": "TEXT",
      "timestamp": "2026-02-24T10:30:00.000Z"
    },
    {
      "messageId": "msg-002",
      "senderId": "vendor-user-12345",
      "senderType": "VENDOR",
      "message": "Yes, we can do 500 guests! Our pricing starts at ₹500/plate.",
      "messageType": "TEXT",
      "timestamp": "2026-02-24T11:00:00.000Z"
    }
  ]
}
```

---

## 10.4 Send Message (WebSocket)

**Connect:** `ws://localhost:8080/api/v1/ws`
**Headers:** `Authorization: Bearer {accessToken}`

```json
// SEND to: /app/chat.sendMessage
{
  "conversationId": "conv-77665-88990",
  "message": "What is included in the service charge?",
  "messageType": "TEXT"
}

// SUBSCRIBE to: /topic/conversations.conv-77665-88990
// Receive live messages in real-time
```

---

# 11. Reviews & Ratings

## 11.1 Submit Review for Completed Order

```http
POST /reviews
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "orderId": "order-54321-12345",
  "rating": 5,
  "foodQualityRating": 5,
  "serviceQualityRating": 4,
  "hygieneRating": 5,
  "valueForMoneyRating": 4,
  "punctualityRating": 5,
  "reviewText": "Excellent food quality and very professional service. The staff was courteous and the food was fresh and delicious. Highly recommend for weddings!",
  "images": [
    "http://localhost:8080/uploads/images/wedding-food-1.jpg",
    "http://localhost:8080/uploads/images/wedding-food-2.jpg"
  ]
}
```

| Rating Field | Required | Rules |
|---|---|---|
| `rating` | ✅ | 1 to 5 (overall) |
| `foodQualityRating` | ❌ | 1 to 5 |
| `serviceQualityRating` | ❌ | 1 to 5 |
| `hygieneRating` | ❌ | 1 to 5 |
| `valueForMoneyRating` | ❌ | 1 to 5 |
| `punctualityRating` | ❌ | 1 to 5 |
| `reviewText` | ❌ | Max 2000 chars |
| `images` | ❌ | Max 5 images |

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "reviewId": "rev-11223-44556",
    "orderId": "order-54321-12345",
    "vendorId": "vendor-12345",
    "rating": 5,
    "reviewText": "Excellent food quality...",
    "status": "APPROVED",
    "createdAt": "2026-02-26T10:00:00.000Z"
  }
}
```

---

## 11.2 Get Vendor's Reviews (Public)

```http
GET /reviews/vendor/{vendorId}?page=0&size=20
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "reviewId": "rev-11223-44556",
      "userName": "John Doe",
      "rating": 5,
      "reviewText": "Excellent food quality...",
      "images": ["http://localhost:8080/uploads/images/wedding-food-1.jpg"],
      "helpfulCount": 5,
      "vendorResponse": {
        "responseText": "Thank you for the kind words!",
        "respondedAt": "2026-02-26T12:00:00.000Z"
      },
      "createdAt": "2026-02-26T10:00:00.000Z"
    }
  ]
}
```

---

## 11.3 Mark Review as Helpful

```http
POST /reviews/{reviewId}/helpful
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": { "reviewId": "rev-11223-44556", "helpfulCount": 6 }
}
```

---

# 12. Support Tickets

## 12.1 Create Support Ticket

```http
POST /support/tickets
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "category": "ORDER",
  "subcategory": "DELIVERY_ISSUE",
  "priority": "HIGH",
  "subject": "Food arrived 2 hours late for wedding",
  "description": "The catering team was supposed to arrive by 5 PM but arrived at 7 PM, causing significant inconvenience to our guests.",
  "orderId": "order-54321-12345",
  "vendorId": "vendor-12345",
  "attachmentUrls": [
    "http://localhost:8080/uploads/images/complaint-photo-1.jpg"
  ]
}
```

| Field | Required | Values |
|-------|----------|--------|
| `category` | ✅ | `ORDER`, `PAYMENT`, `VENDOR`, `ACCOUNT`, `OTHER` |
| `priority` | ✅ | `LOW`, `MEDIUM`, `HIGH`, `URGENT` |
| `subject` | ✅ | Max 255 chars |
| `description` | ✅ | Max 5000 chars |

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "ticketId": "tkt-99001-22334",
    "ticketNumber": "TKT-20260224123456",
    "status": "OPEN",
    "priority": "HIGH",
    "category": "ORDER",
    "subject": "Food arrived 2 hours late for wedding",
    "assignedTo": "agent-support-001",
    "assignedToName": "Priya Sharma",
    "sla": {
      "responseDeadline": "2026-02-24T11:30:00.000Z",
      "resolutionDeadline": "2026-02-25T10:30:00.000Z",
      "status": "ON_TRACK"
    },
    "createdAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 12.2 Get My Support Tickets

```http
GET /support/tickets?page=0&size=20&status=OPEN
Authorization: Bearer {accessToken}
```

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
      "subject": "Food arrived 2 hours late for wedding",
      "assignedToName": "Priya Sharma",
      "createdAt": "2026-02-24T10:30:45.123Z"
    }
  ]
}
```

---

# 13. Promo Codes

## 13.1 Apply Promo Code

```http
POST /promos/apply
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "code": "WELCOME2026",
  "orderTotal": 50000
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "valid": true,
    "code": "WELCOME2026",
    "orderTotal": 50000,
    "discountAmount": 2500,
    "finalAmount": 47500,
    "message": "Promo applied! You saved ₹2500"
  }
}
```

### Invalid Code Response
```json
{
  "success": true,
  "data": {
    "valid": false,
    "errorCode": "INVALID_CODE",
    "message": "Invalid or expired promo code"
  }
}
```

---

## 13.2 Get Active Promo Codes

```http
GET /promos/active
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "promoCodeId": "promo-001",
      "code": "WELCOME2026",
      "title": "Welcome Offer",
      "description": "5% off on your first order",
      "type": "PERCENTAGE",
      "value": 5,
      "minOrderAmount": 10000,
      "validTo": "2026-03-31T23:59:59Z",
      "firstOrderOnly": true
    }
  ]
}
```

---

# 14. Loyalty Points

## 14.1 Get My Loyalty Balance

```http
GET /loyalty/balance
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "pointsBalance": 1250,
    "lifetimePoints": 3500,
    "tier": "GOLD",
    "earnMultiplier": 1.5,
    "rupeesEquivalent": 312.50,
    "nextTierRequirement": {
      "tier": "PLATINUM",
      "pointsNeeded": 5000,
      "pointsToGo": 1500
    },
    "pointsExpiringOn": "2027-02-24T10:30:45.123Z"
  }
}
```

---

## 14.2 Get My Loyalty Transaction History

```http
GET /loyalty/transactions?page=0&size=20
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "transactionId": "ltxn-001",
      "type": "EARN",
      "points": 500,
      "balanceAfter": 1250,
      "description": "Points earned from order completion",
      "orderId": "order-54321-12345",
      "createdAt": "2026-02-26T10:00:00.000Z"
    },
    {
      "transactionId": "ltxn-002",
      "type": "BONUS",
      "points": 200,
      "balanceAfter": 750,
      "description": "Welcome bonus for joining via referral",
      "createdAt": "2026-01-10T08:00:00.000Z"
    }
  ]
}
```

---

# 15. Referral Program

## 15.1 Get My Referral Code

```http
GET /referrals/my-code
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "codeId": "rcode-77889-00112",
    "code": "REF550E8400",
    "status": "ACTIVE",
    "totalReferrals": 5,
    "totalEarnings": 2500,
    "referralUrl": "https://bidzaro.com/signup?ref=REF550E8400",
    "createdAt": "2026-01-10T08:00:00.000Z"
  }
}
```

---

## 15.2 Get My Referral Statistics

```http
GET /referrals/stats
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "totalReferrals": 5,
    "completedReferrals": 3,
    "pendingReferrals": 2,
    "totalEarningsPoints": 1500,
    "referralDetails": [
      {
        "referredUserName": "Rahul Sharma",
        "status": "COMPLETED",
        "pointsEarned": 500,
        "referredAt": "2026-01-20T10:00:00.000Z"
      }
    ]
  }
}
```

---

# 16. Wishlist

## 16.1 Get My Wishlist

```http
GET /wishlist
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "wishlistItemId": "wish-001",
      "masterItemId": "item-001",
      "itemName": "Paneer Tikka",
      "description": "Grilled cottage cheese with spices",
      "categoryId": "cat-001",
      "cuisineType": "North Indian",
      "foodType": "VEGETARIAN",
      "imageUrl": "http://localhost:8080/uploads/images/paneer-tikka.jpg",
      "addedAt": "2026-02-10T09:00:00.000Z"
    }
  ]
}
```

---

## 16.2 Add to Wishlist

```http
POST /wishlist/{masterItemId}
Authorization: Bearer {accessToken}
```

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "wishlistItemId": "wish-002",
    "masterItemId": "item-002",
    "itemName": "Butter Chicken",
    "addedAt": "2026-02-24T10:30:45.123Z"
  }
}
```

---

## 16.3 Remove from Wishlist

```http
DELETE /wishlist/{wishlistItemId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Item removed from wishlist"
}
```

---

# 17. Notifications

## 17.1 Get My Notifications

```http
GET /notifications?page=0&size=20&unreadOnly=false
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "notificationId": "notif-001",
      "title": "New Bid Received",
      "message": "Spice Garden Catering has submitted a quote for your wedding request.",
      "type": "BID_RECEIVED",
      "isRead": false,
      "referenceId": "bid-vendor-001",
      "referenceType": "BID",
      "createdAt": "2026-02-24T12:00:00.000Z"
    },
    {
      "notificationId": "notif-002",
      "title": "Order Confirmed",
      "message": "Your order for Priya & Rahul Wedding has been confirmed.",
      "type": "ORDER_CONFIRMED",
      "isRead": true,
      "referenceId": "order-54321-12345",
      "referenceType": "ORDER",
      "createdAt": "2026-02-25T11:00:00.000Z"
    }
  ],
  "pageInfo": { "totalElements": 12, "totalPages": 1 }
}
```

---

## 17.2 Mark Notification as Read

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

## 17.3 Mark All as Read

```http
PATCH /notifications/read-all
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "All notifications marked as read"
}
```

---

## 17.4 Update FCM Token

**Use:** Update push notification token when it refreshes on device.

```http
PATCH /users/fcm-token
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "fcmToken": "new_firebase_device_token_here"
}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "FCM token updated"
}
```

---

# 📌 User API — Complete Error Code Reference

| Error Code | HTTP Status | Description |
|------------|-------------|-------------|
| `EMAIL_EXISTS` | 409 | Email already registered |
| `PHONE_EXISTS` | 409 | Phone number already registered |
| `INVALID_CREDENTIALS` | 401 | Wrong email/phone or password |
| `ACCOUNT_LOCKED` | 401 | Too many failed login attempts |
| `ACCOUNT_SUSPENDED` | 401 | Account suspended by admin |
| `TOKEN_EXPIRED` | 401 | Access token expired |
| `INVALID_TOKEN` | 401 | Invalid JWT token |
| `FORBIDDEN` | 403 | Action not allowed |
| `USER_NOT_FOUND` | 404 | User does not exist |
| `ORDER_NOT_FOUND` | 404 | Order does not exist |
| `BID_NOT_FOUND` | 404 | Bid does not exist |
| `CART_EMPTY` | 400 | Cart is empty |
| `ITEM_UNAVAILABLE` | 400 | Menu item not available |
| `INVALID_QUANTITY` | 400 | Quantity must be >= 1 |
| `ORDER_NOT_COMPLETED` | 400 | Cannot review incomplete order |
| `REVIEW_EXISTS` | 400 | Already reviewed this order |
| `INSUFFICIENT_POINTS` | 400 | Not enough loyalty points |
| `INVALID_CODE` | 400 | Invalid promo code |
| `PAYMENT_FAILED` | 400 | Payment processing failed |
| `INVALID_SIGNATURE` | 400 | Payment signature mismatch |
| `VALIDATION_ERROR` | 422 | Field validation failed |
| `RATE_LIMIT_EXCEEDED` | 429 | Too many requests |

---

# 📌 Important Notes for Users

1. **Token Expiry:** Access token valid for 7 days. Use refresh token to get a new one.
2. **Phone Format:** Always use E.164 format: `+91XXXXXXXXXX` for India, `+1XXXXXXXXXX` for USA.
3. **Currency:** INR for India users, USD for USA users. Auto-detected from country.
4. **Bidding Flow:** Create Bid → Vendors Submit Bids → Accept Bid → 24hr Cooling → Pay Token → Order Confirmed.
5. **Payment Flow:** INR payments use Razorpay. USD payments use Stripe.
6. **Cancellation Refunds:** >30 days = 100%, 15-30 = 75%, 7-15 = 50%, 3-7 = 25%, <3 days = 0%.
7. **Loyalty Points:** 1 point per ₹1 spent. 4 points = ₹1 discount. Max 50% discount via points.
8. **Referral Rewards:** You earn 500 pts per successful referral. New user gets 200 pts.
9. **Review Policy:** Can only review after order is COMPLETED or DELIVERED.
10. **File Uploads:** Images max 5MB, documents max 10MB. Upload first, then use URL in other APIs.

---

*User API Documentation — Bidzaro Catering Platform v1.0.0 | Updated: February 24, 2026*

