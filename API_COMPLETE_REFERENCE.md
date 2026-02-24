# 🚀 Bidzaro API Complete Reference Guide

**Version:** 1.0.0  
**Base URL:** `http://localhost:8080/api/v1`  
**Authentication:** Bearer JWT Token in `Authorization` header  
**Content-Type:** `application/json`

---

## 📋 Table of Contents

1. [Response Format & Status Codes](#response-format--status-codes)
2. [Authentication Module](#authentication-module)
3. [User Management Module](#user-management-module)
4. [Vendor Management Module](#vendor-management-module)
5. [Menu Management Module](#menu-management-module)
6. [Bidding System Module](#bidding-system-module)
7. [Order Management Module](#order-management-module)
8. [Payment Module](#payment-module)
9. [Chat Module](#chat-module)
10. [Support Tickets Module](#support-tickets-module)
11. [Reviews Module](#reviews-module)
12. [Promo Codes Module](#promo-codes-module)
13. [Loyalty Points Module](#loyalty-points-module)
14. [Referral Module](#referral-module)
15. [Admin Dashboard Module](#admin-dashboard-module)
16. [Analytics Module](#analytics-module)
17. [File Upload Module](#file-upload-module)

---

## Response Format & Status Codes

### ✅ Standard Success Response Format

All successful responses follow this wrapper:

```json
{
  "success": true,
  "statusCode": 200,
  "message": "Operation successful",
  "data": {
    // Actual response data
  },
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 150,
    "totalPages": 8,
    "isFirst": true,
    "isLast": false,
    "hasNext": true,
    "hasPrevious": false
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

### ❌ Error Response Format

```json
{
  "success": false,
  "statusCode": 400,
  "message": "Validation failed",
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "One or more fields have validation errors",
    "details": "Validation failed for one or more fields",
    "path": "/api/v1/auth/register",
    "timestamp": "2024-02-24T10:30:45.123Z",
    "fieldErrors": {
      "email": "Email is already registered",
      "password": "Password must be at least 8 characters"
    }
  }
}
```

### HTTP Status Codes

| Code | Meaning | Usage |
|------|---------|-------|
| **200** | OK | Successful GET, PUT request |
| **201** | Created | Successful POST request (resource created) |
| **204** | No Content | Successful DELETE request |
| **400** | Bad Request | Invalid input validation failed |
| **401** | Unauthorized | Missing or invalid JWT token |
| **403** | Forbidden | User doesn't have permission |
| **404** | Not Found | Resource doesn't exist |
| **409** | Conflict | Resource already exists (duplicate email, etc.) |
| **422** | Unprocessable Entity | Validation failed on fields |
| **429** | Too Many Requests | Rate limit exceeded |
| **500** | Server Error | Internal server error |

---

# 1️⃣ Authentication Module

**Base Path:** `/auth`

## 1.1 User Registration

### Endpoint
```http
POST /auth/register
Content-Type: application/json
```

### Request Payload
```json
{
  "email": "john@example.com",
  "phone": "+919876543210",
  "password": "SecurePass@123",
  "firstName": "John",
  "lastName": "Doe",
  "userType": "USER",
  "country": "USA",
  "fcmToken": "dZHB4qEfOgk:APA91bGx...",
  "deviceInfo": "iPhone 15 Pro - iOS 17.2"
}
```

### Request Validation Rules
| Field | Type | Required | Rules |
|-------|------|----------|-------|
| `email` | string | ✅ | Valid email, unique, max 255 chars |
| `phone` | string | ✅ | E.164 format (+country code), unique |
| `password` | string | ✅ | Min 8 chars, 1 uppercase, 1 digit, 1 special |
| `firstName` | string | ✅ | Max 100 chars |
| `lastName` | string | ✅ | Max 100 chars |
| `userType` | enum | ✅ | `USER` or `VENDOR` |
| `country` | string | ✅ | `USA` or `INDIA` |
| `fcmToken` | string | ❌ | Firebase Cloud Messaging token |
| `deviceInfo` | string | ❌ | Device information (OS, model, version) |

### Success Response (201 Created)
```json
{
  "success": true,
  "statusCode": 201,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 604800,
    "user": {
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "email": "john@example.com",
      "phone": "+919876543210",
      "firstName": "John",
      "lastName": "Doe",
      "fullName": "John Doe",
      "userType": "USER",
      "status": "PENDING_VERIFICATION",
      "country": "USA",
      "preferredCurrency": "USD",
      "profilePictureUrl": null,
      "emailVerified": false,
      "phoneVerified": false,
      "createdAt": "2024-02-24T10:30:45.123Z"
    }
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

### Error Response Examples

**409 Conflict - Email Already Exists**
```json
{
  "success": false,
  "statusCode": 409,
  "message": "Email is already registered",
  "error": {
    "code": "EMAIL_EXISTS",
    "message": "Email is already registered",
    "details": null,
    "path": "/api/v1/auth/register",
    "timestamp": "2024-02-24T10:30:45.123Z"
  }
}
```

**422 Unprocessable Entity - Validation Failed**
```json
{
  "success": false,
  "statusCode": 422,
  "message": "Validation failed",
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed",
    "details": "One or more fields have validation errors",
    "path": "/api/v1/auth/register",
    "fieldErrors": {
      "password": "Password must contain at least one uppercase letter",
      "phone": "Phone must be in E.164 format"
    },
    "timestamp": "2024-02-24T10:30:45.123Z"
  }
}
```

---

## 1.2 User Login

### Endpoint
```http
POST /auth/login
Content-Type: application/json
```

### Request Payload
```json
{
  "identifier": "john@example.com",
  "password": "SecurePass@123",
  "fcmToken": "dZHB4qEfOgk:APA91bGx...",
  "deviceInfo": "iPhone 15 Pro - iOS 17.2"
}
```

### Request Validation Rules
| Field | Type | Required | Rules |
|-------|------|----------|-------|
| `identifier` | string | ✅ | Email or phone number |
| `password` | string | ✅ | Min 8 characters |
| `fcmToken` | string | ❌ | Firebase token |
| `deviceInfo` | string | ❌ | Device details |

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 604800,
    "user": {
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "email": "john@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "userType": "USER",
      "status": "ACTIVE",
      "country": "USA",
      "preferredCurrency": "USD",
      "lastLoginAt": "2024-02-24T10:30:45.123Z"
    }
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

### Error Response Examples

**401 Unauthorized - Invalid Credentials**
```json
{
  "success": false,
  "statusCode": 401,
  "message": "Invalid credentials",
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "Email or phone invalid",
    "details": null,
    "path": "/api/v1/auth/login",
    "timestamp": "2024-02-24T10:30:45.123Z"
  }
}
```

**401 Unauthorized - Account Locked**
```json
{
  "success": false,
  "statusCode": 401,
  "message": "Account locked",
  "error": {
    "code": "ACCOUNT_LOCKED",
    "message": "Account is locked due to multiple failed login attempts. Please reset your password to unlock your account.",
    "details": null,
    "path": "/api/v1/auth/login",
    "timestamp": "2024-02-24T10:30:45.123Z"
  }
}
```

**401 Unauthorized - Vendor Not Approved**
```json
{
  "success": false,
  "statusCode": 401,
  "message": "Vendor not approved",
  "error": {
    "code": "VENDOR_NOT_APPROVED",
    "message": "Your vendor account is currently PENDING. Please wait for admin approval.",
    "details": null,
    "path": "/api/v1/auth/login",
    "timestamp": "2024-02-24T10:30:45.123Z"
  }
}
```

---

## 1.3 Refresh Access Token

### Endpoint
```http
POST /auth/refresh-token
Content-Type: application/json
```

### Request Payload
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Token refreshed successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 604800,
    "tokenType": "Bearer"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 1.4 Send OTP

### Endpoint
```http
POST /auth/send-otp
Content-Type: application/json
```

### Request Payload
```json
{
  "identifier": "john@example.com",
  "type": "EMAIL"
}
```

### Request Validation Rules
| Field | Type | Required | Rules |
|-------|------|----------|-------|
| `identifier` | string | ✅ | Email or phone |
| `type` | enum | ✅ | `EMAIL` or `PHONE` |

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "OTP sent successfully",
  "data": {
    "expiresIn": 600,
    "message": "OTP has been sent to john@example.com"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 1.5 Verify OTP

### Endpoint
```http
POST /auth/verify-otp
Content-Type: application/json
```

### Request Payload
```json
{
  "identifier": "john@example.com",
  "otp": "123456",
  "type": "EMAIL"
}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "OTP verified successfully",
  "data": {
    "verified": true,
    "message": "Email verified successfully"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 1.6 Forgot Password

### Endpoint
```http
POST /auth/forgot-password
Content-Type: application/json
```

### Request Payload
```json
{
  "email": "john@example.com"
}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Reset link sent to email",
  "data": {
    "message": "Password reset link has been sent to john@example.com"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 1.7 Reset Password

### Endpoint
```http
POST /auth/reset-password
Content-Type: application/json
```

### Request Payload
```json
{
  "token": "reset-token-from-email",
  "newPassword": "NewSecurePass@456"
}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Password reset successfully",
  "data": {
    "message": "Your password has been reset successfully. Please login with your new password."
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

# 2️⃣ User Management Module

**Base Path:** `/users`  
**Authentication:** ✅ Required

## 2.1 Get User Profile

### Endpoint
```http
GET /users/profile
Authorization: Bearer {accessToken}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Profile retrieved successfully",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "john@example.com",
    "phone": "+919876543210",
    "firstName": "John",
    "lastName": "Doe",
    "fullName": "John Doe",
    "profilePictureUrl": "https://storage.googleapis.com/bucket/profiles/john.jpg",
    "dateOfBirth": "1990-05-15",
    "gender": "MALE",
    "userType": "USER",
    "status": "ACTIVE",
    "country": "USA",
    "preferredCurrency": "USD",
    "preferredLanguage": "en",
    "emailVerified": true,
    "phoneVerified": true,
    "twoFactorEnabled": false,
    "lastLoginAt": "2024-02-24T10:30:45.123Z",
    "createdAt": "2024-01-15T08:00:00.000Z",
    "updatedAt": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 2.2 Update User Profile

### Endpoint
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
  "dateOfBirth": "1990-05-15",
  "preferredLanguage": "en",
  "preferredCurrency": "USD"
}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Profile updated successfully",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "firstName": "John",
    "lastName": "Doe",
    "gender": "MALE",
    "dateOfBirth": "1990-05-15",
    "updatedAt": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 2.3 Add Address

### Endpoint
```http
POST /users/addresses
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "label": "Home",
  "streetAddress": "123 Main Street",
  "city": "San Francisco",
  "state": "CA",
  "postalCode": "94102",
  "country": "USA",
  "latitude": 37.7749,
  "longitude": -122.4194,
  "isDefault": true
}
```

### Request Validation Rules
| Field | Type | Required | Rules |
|-------|------|----------|-------|
| `label` | string | ✅ | Max 50 chars (Home, Office, etc.) |
| `streetAddress` | string | ✅ | Max 255 chars |
| `city` | string | ✅ | Max 100 chars |
| `state` | string | ✅ | Max 100 chars |
| `postalCode` | string | ✅ | Max 20 chars |
| `country` | string | ✅ | Max 100 chars |
| `latitude` | number | ❌ | -90 to 90 |
| `longitude` | number | ❌ | -180 to 180 |
| `isDefault` | boolean | ✅ | true/false |

### Success Response (201 Created)
```json
{
  "success": true,
  "statusCode": 201,
  "message": "Address added successfully",
  "data": {
    "addressId": "addr-12345-67890",
    "label": "Home",
    "streetAddress": "123 Main Street",
    "city": "San Francisco",
    "state": "CA",
    "postalCode": "94102",
    "country": "USA",
    "latitude": 37.7749,
    "longitude": -122.4194,
    "isDefault": true,
    "createdAt": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 2.4 Get All Addresses

### Endpoint
```http
GET /users/addresses
Authorization: Bearer {accessToken}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Addresses retrieved successfully",
  "data": [
    {
      "addressId": "addr-12345-67890",
      "label": "Home",
      "streetAddress": "123 Main Street",
      "city": "San Francisco",
      "state": "CA",
      "postalCode": "94102",
      "country": "USA",
      "latitude": 37.7749,
      "longitude": -122.4194,
      "isDefault": true,
      "createdAt": "2024-02-24T10:30:45.123Z"
    },
    {
      "addressId": "addr-98765-43210",
      "label": "Office",
      "streetAddress": "456 Tech Avenue",
      "city": "San Francisco",
      "state": "CA",
      "postalCode": "94105",
      "country": "USA",
      "latitude": 37.7897,
      "longitude": -122.3971,
      "isDefault": false,
      "createdAt": "2024-02-10T14:20:30.000Z"
    }
  ],
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 2.5 Delete Address

### Endpoint
```http
DELETE /users/addresses/{addressId}
Authorization: Bearer {accessToken}
```

### Success Response (204 No Content)
```
HTTP/1.1 204 No Content
```

---

# 3️⃣ Vendor Management Module

**Base Path:** `/vendors`  
**Authentication:** ✅ Required (for most endpoints)

## 3.1 Register as Vendor

### Endpoint
```http
POST /vendors
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "businessName": "Gourmet Catering Co.",
  "businessEmail": "info@gourmetcatering.com",
  "businessPhone": "+919876543210",
  "businessType": "CATERING",
  "businessRegistrationNumber": "REG-2024-12345",
  "taxId": "TAX-2024-98765",
  "description": "Premium catering service for all occasions",
  "establishedYear": 2015,
  "country": "INDIA",
  "businessAddress": {
    "streetAddress": "456 Food Street",
    "city": "Mumbai",
    "state": "Maharashtra",
    "postalCode": "400001",
    "country": "India",
    "latitude": 19.0760,
    "longitude": 72.8777
  },
  "ownerInfo": {
    "firstName": "Rajesh",
    "lastName": "Kumar",
    "phone": "+919876543210",
    "email": "rajesh@gourmetcatering.com",
    "idProofType": "AADHAR",
    "idProofNumber": "1234-5678-9012"
  },
  "serviceAreas": [
    {
      "city": "Mumbai",
      "state": "Maharashtra",
      "radiusKm": 25
    }
  ],
  "cuisinesOffered": ["Indian", "Continental", "Chinese"],
  "specialties": ["Weddings", "Corporate Events", "Birthday Parties"],
  "capacity": {
    "minGuests": 10,
    "maxGuests": 1000,
    "concurrentEvents": 3
  },
  "pricing": {
    "startingPricePerPlate": 500,
    "averagePricePerPlate": 750,
    "currency": "INR"
  },
  "documents": [
    {
      "documentType": "LICENSE",
      "documentName": "Business License",
      "documentUrl": "https://storage.googleapis.com/bucket/docs/license.pdf",
      "documentNumber": "LIC-2024-123",
      "issueDate": "2024-01-15",
      "expiryDate": "2025-01-15"
    },
    {
      "documentType": "HEALTH_CERTIFICATE",
      "documentName": "Health Certificate",
      "documentUrl": "https://storage.googleapis.com/bucket/docs/health.pdf",
      "documentNumber": "HC-2024-456",
      "issueDate": "2024-02-01",
      "expiryDate": "2025-02-01"
    }
  ]
}
```

### Request Validation Rules
| Field | Type | Required | Rules |
|-------|------|----------|-------|
| `businessName` | string | ✅ | Max 255 chars |
| `businessEmail` | string | ✅ | Valid email, unique |
| `businessPhone` | string | ✅ | E.164 format, unique |
| `businessType` | enum | ✅ | `CATERING`, `RESTAURANT`, `CLOUD_KITCHEN`, `HOME_CHEF`, `BAKERY` |
| `businessRegistrationNumber` | string | ✅ | Max 100 chars |
| `taxId` | string | ✅ | Max 50 chars |
| `country` | string | ✅ | `USA` or `INDIA` |
| `documents` | array | ✅ | Min 2 required |

### Success Response (201 Created)
```json
{
  "success": true,
  "statusCode": 201,
  "message": "Vendor registered successfully. Pending approval.",
  "data": {
    "vendorId": "vendor-12345-67890",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "businessName": "Gourmet Catering Co.",
    "businessEmail": "info@gourmetcatering.com",
    "businessType": "CATERING",
    "approvalStatus": "PENDING",
    "status": "PENDING_APPROVAL",
    "country": "INDIA",
    "verified": false,
    "featured": false,
    "ratings": {
      "averageRating": 0,
      "totalReviews": 0,
      "ratingBreakdown": {
        "fiveStars": 0,
        "fourStars": 0,
        "threeStars": 0,
        "twoStars": 0,
        "oneStar": 0
      }
    },
    "createdAt": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 3.2 Get All Vendors (Public)

### Endpoint
```http
GET /vendors?page=0&size=20&status=ACTIVE&city=Mumbai&cuisine=Indian
```

### Query Parameters
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | integer | 0 | Page number (0-indexed) |
| `size` | integer | 20 | Items per page |
| `status` | string | - | Filter by status (ACTIVE, PENDING_APPROVAL, etc.) |
| `city` | string | - | Filter by service city |
| `cuisine` | string | - | Filter by cuisine type |

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Vendors retrieved successfully",
  "data": [
    {
      "vendorId": "vendor-12345-67890",
      "businessName": "Gourmet Catering Co.",
      "businessEmail": "info@gourmetcatering.com",
      "logoUrl": "https://storage.googleapis.com/bucket/logos/gourmet.png",
      "description": "Premium catering service for all occasions",
      "country": "INDIA",
      "verified": true,
      "featured": true,
      "status": "ACTIVE",
      "ratings": {
        "averageRating": 4.8,
        "totalReviews": 245
      },
      "pricing": {
        "startingPricePerPlate": 500,
        "averagePricePerPlate": 750,
        "currency": "INR"
      },
      "cuisinesOffered": ["Indian", "Continental", "Chinese"],
      "capacity": {
        "maxGuests": 1000
      },
      "createdAt": "2024-02-24T10:30:45.123Z"
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 150,
    "totalPages": 8,
    "isFirst": true,
    "isLast": false,
    "hasNext": true,
    "hasPrevious": false
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 3.3 Get Vendor by ID (Public)

### Endpoint
```http
GET /vendors/{vendorId}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Vendor retrieved successfully",
  "data": {
    "vendorId": "vendor-12345-67890",
    "businessName": "Gourmet Catering Co.",
    "businessEmail": "info@gourmetcatering.com",
    "businessPhone": "+919876543210",
    "logoUrl": "https://storage.googleapis.com/bucket/logos/gourmet.png",
    "bannerUrl": "https://storage.googleapis.com/bucket/banners/gourmet.jpg",
    "description": "Premium catering service for all occasions",
    "establishedYear": 2015,
    "country": "INDIA",
    "verified": true,
    "featured": true,
    "status": "ACTIVE",
    "approvalStatus": "APPROVED",
    "ratings": {
      "averageRating": 4.8,
      "totalReviews": 245,
      "ratingBreakdown": {
        "fiveStars": 200,
        "fourStars": 35,
        "threeStars": 8,
        "twoStars": 2,
        "oneStar": 0
      }
    },
    "stats": {
      "totalOrders": 500,
      "completedOrders": 498,
      "cancelledOrders": 2,
      "totalRevenue": 500000,
      "averageOrderValue": 1000,
      "responseTimeMinutes": 15
    },
    "businessAddress": {
      "streetAddress": "456 Food Street",
      "city": "Mumbai",
      "state": "Maharashtra",
      "postalCode": "400001",
      "country": "India",
      "gpsCoordinates": {
        "type": "Point",
        "coordinates": [72.8777, 19.0760]
      }
    },
    "serviceAreas": [
      {
        "city": "Mumbai",
        "state": "Maharashtra",
        "radiusKm": 25
      }
    ],
    "cuisinesOffered": ["Indian", "Continental", "Chinese"],
    "specialties": ["Weddings", "Corporate Events", "Birthday Parties"],
    "capacity": {
      "minGuests": 10,
      "maxGuests": 1000,
      "concurrentEvents": 3
    },
    "pricing": {
      "startingPricePerPlate": 500,
      "averagePricePerPlate": 750,
      "currency": "INR"
    },
    "createdAt": "2024-02-24T10:30:45.123Z",
    "updatedAt": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 3.4 Search Vendors

### Endpoint
```http
GET /vendors/search?query=gourmet&city=Mumbai&cuisines=Indian,Chinese&rating=4&page=0&size=20
```

### Query Parameters
| Parameter | Type | Description |
|-----------|------|-------------|
| `query` | string | Search by name, city, specialty |
| `city` | string | Filter by city |
| `cuisines` | string | Comma-separated cuisine types |
| `rating` | number | Minimum rating (0-5) |
| `page` | integer | Page number |
| `size` | integer | Items per page |

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Search results retrieved successfully",
  "data": [
    {
      "vendorId": "vendor-12345-67890",
      "businessName": "Gourmet Catering Co.",
      "ratings": {
        "averageRating": 4.8,
        "totalReviews": 245
      },
      "cuisinesOffered": ["Indian", "Continental", "Chinese"],
      "serviceAreas": [
        {
          "city": "Mumbai"
        }
      ]
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 5,
    "totalPages": 1,
    "isFirst": true,
    "isLast": true,
    "hasNext": false,
    "hasPrevious": false
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 3.5 Get My Vendor Profile (Private)

### Endpoint
```http
GET /vendors/me
Authorization: Bearer {accessToken}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Vendor profile retrieved successfully",
  "data": {
    "vendorId": "vendor-12345-67890",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "businessName": "Gourmet Catering Co.",
    "approvalStatus": "APPROVED",
    "status": "ACTIVE"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 3.6 Update Vendor Profile (Private)

### Endpoint
```http
PUT /vendors/{vendorId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "description": "Updated description",
  "logoUrl": "https://storage.googleapis.com/bucket/logos/gourmet-new.png",
  "bannerUrl": "https://storage.googleapis.com/bucket/banners/gourmet-new.jpg",
  "cuisinesOffered": ["Indian", "Continental"],
  "specialties": ["Weddings", "Corporate Events"],
  "capacity": {
    "minGuests": 20,
    "maxGuests": 1500,
    "concurrentEvents": 5
  },
  "pricing": {
    "startingPricePerPlate": 600,
    "averagePricePerPlate": 800,
    "currency": "INR"
  }
}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Vendor profile updated successfully",
  "data": {
    "vendorId": "vendor-12345-67890",
    "businessName": "Gourmet Catering Co.",
    "description": "Updated description",
    "updatedAt": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 3.7 Get Pending Vendors (Admin Only)

### Endpoint
```http
GET /vendors/admin/pending?page=0&size=20
Authorization: Bearer {adminToken}
X-Admin-Role: ADMIN
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Pending vendors retrieved successfully",
  "data": [
    {
      "vendorId": "vendor-pending-123",
      "businessName": "New Catering Startup",
      "approvalStatus": "PENDING",
      "businessAddress": {
        "city": "Delhi",
        "state": "Delhi"
      },
      "documents": [
        {
          "documentType": "LICENSE",
          "verificationStatus": "PENDING"
        }
      ],
      "createdAt": "2024-02-20T10:30:45.123Z"
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 15,
    "totalPages": 1
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 3.8 Approve Vendor (Admin Only)

### Endpoint
```http
POST /vendors/{vendorId}/approve
Authorization: Bearer {adminToken}
Content-Type: application/json
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Vendor approved successfully",
  "data": {
    "vendorId": "vendor-pending-123",
    "businessName": "New Catering Startup",
    "approvalStatus": "APPROVED",
    "approvalDate": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 3.9 Reject Vendor (Admin Only)

### Endpoint
```http
POST /vendors/{vendorId}/reject?reason=Insufficient%20documents
Authorization: Bearer {adminToken}
Content-Type: application/json
```

### Query Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `reason` | string | ✅ | Rejection reason |

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Vendor rejected successfully",
  "data": {
    "vendorId": "vendor-pending-123",
    "approvalStatus": "REJECTED",
    "rejectionReason": "Insufficient documents"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

# 4️⃣ Menu Management Module

**Base Path:** `/menu`  
**Authentication:** ✅ Required (for POST/PUT/DELETE)

## 4.1 Get All Categories

### Endpoint
```http
GET /menu/categories
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Categories retrieved successfully",
  "data": [
    {
      "categoryId": "cat-001",
      "categoryName": "Appetizers",
      "categoryNameHindi": "आहार शुरुआत",
      "description": "Starters and appetizers",
      "iconUrl": "https://storage.googleapis.com/bucket/icons/appetizers.svg",
      "displayOrder": 1,
      "status": "ACTIVE",
      "createdAt": "2024-01-15T08:00:00.000Z"
    },
    {
      "categoryId": "cat-002",
      "categoryName": "Main Course",
      "categoryNameHindi": "मुख्य पाठ्यक्रम",
      "description": "Main dishes",
      "iconUrl": "https://storage.googleapis.com/bucket/icons/maincourse.svg",
      "displayOrder": 2,
      "status": "ACTIVE",
      "createdAt": "2024-01-15T08:00:00.000Z"
    }
  ],
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 4.2 Get Menu Items by Category

### Endpoint
```http
GET /menu/items?categoryId=cat-001&page=0&size=20
```

### Query Parameters
| Parameter | Type | Description |
|-----------|------|-------------|
| `categoryId` | string | Category ID to filter by |
| `page` | integer | Page number |
| `size` | integer | Items per page |

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Menu items retrieved successfully",
  "data": [
    {
      "masterItemId": "item-001",
      "itemName": "Paneer Pakora",
      "itemNameHindi": "पनीर पकौड़े",
      "description": "Deep-fried cheese fritters",
      "categoryId": "cat-001",
      "cuisineType": "Indian",
      "foodType": "VEGETARIAN",
      "spiceLevel": "MEDIUM",
      "dietaryTags": ["VEGETARIAN", "GLUTEN_FREE"],
      "allergens": ["DAIRY", "GLUTEN"],
      "imageUrls": [
        "https://storage.googleapis.com/bucket/items/paneer-1.jpg",
        "https://storage.googleapis.com/bucket/items/paneer-2.jpg"
      ],
      "isPopular": true,
      "status": "ACTIVE",
      "createdAt": "2024-01-15T08:00:00.000Z"
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 50,
    "totalPages": 3
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 4.3 Search Menu Items

### Endpoint
```http
GET /menu/items/search?query=paneer&page=0&size=20
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Search results retrieved successfully",
  "data": [
    {
      "masterItemId": "item-001",
      "itemName": "Paneer Pakora",
      "cuisineType": "Indian",
      "imageUrls": ["https://storage.googleapis.com/bucket/items/paneer-1.jpg"]
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 5,
    "totalPages": 1
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 4.4 Get Vendor Menu Items

### Endpoint
```http
GET /menu/vendor-items?vendorId=vendor-12345-67890&page=0&size=20
Authorization: Bearer {accessToken}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Vendor menu items retrieved successfully",
  "data": [
    {
      "vendorItemId": "vitem-001",
      "masterItemId": "item-001",
      "vendorId": "vendor-12345-67890",
      "customName": "Special Paneer Pakora",
      "basePrice": 150,
      "effectivePrice": 150,
      "discountPercentage": 0,
      "isAvailable": true,
      "preparationTime": 20,
      "customizationOptions": [
        {
          "name": "Spice Level",
          "options": ["Mild", "Medium", "Hot"],
          "required": false
        }
      ],
      "status": "ACTIVE",
      "createdAt": "2024-02-24T10:30:45.123Z"
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 120,
    "totalPages": 6
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

# 5️⃣ Bidding System Module

**Base Path:** `/bids`  
**Authentication:** ✅ Required

## 5.1 Create Bid Request

### Endpoint
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
    "eventName": "John & Jane Wedding",
    "eventDate": "2024-06-15T18:00:00Z",
    "eventStartTime": "18:00",
    "eventEndTime": "23:00",
    "numberOfGuests": 250,
    "venueAddress": {
      "streetAddress": "Grand Hotel, 789 Wedding Lane",
      "city": "Mumbai",
      "state": "Maharashtra",
      "postalCode": "400001",
      "country": "India",
      "latitude": 19.0760,
      "longitude": 72.8777
    }
  },
  "menuItems": [
    {
      "masterItemId": "item-001",
      "itemName": "Paneer Pakora",
      "quantity": 250
    },
    {
      "masterItemId": "item-005",
      "itemName": "Butter Chicken",
      "quantity": 250
    }
  ],
  "additionalRequirements": {
    "serviceStaffNeeded": true,
    "numberOfStaff": 15,
    "decorationNeeded": true,
    "liveCounters": true,
    "specialInstructions": "Need food ready by 6 PM sharp. Vegetarian and non-vegetarian items to be kept separate."
  },
  "budget": {
    "estimatedBudget": 75000,
    "budgetRange": "75000-100000",
    "currency": "INR"
  },
  "targetedVendors": [
    "vendor-12345-67890",
    "vendor-98765-43210"
  ]
}
```

### Request Validation Rules
| Field | Type | Required | Rules |
|-------|------|----------|-------|
| `eventDetails.eventType` | enum | ✅ | `WEDDING`, `CORPORATE`, `BIRTHDAY`, `ANNIVERSARY`, `PRIVATE_PARTY` |
| `eventDetails.numberOfGuests` | integer | ✅ | > 0 |
| `eventDetails.eventDate` | ISO8601 | ✅ | Future date |
| `menuItems` | array | ✅ | Min 1 item |
| `budget.estimatedBudget` | number | ✅ | > 0 |
| `targetedVendors` | array | ❌ | Max 10 vendors |

### Success Response (201 Created)
```json
{
  "success": true,
  "statusCode": 201,
  "message": "Bid request created successfully",
  "data": {
    "bidRequestId": "breq-12345-67890",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "ACTIVE",
    "eventDetails": {
      "eventType": "WEDDING",
      "eventName": "John & Jane Wedding",
      "eventDate": "2024-06-15T18:00:00Z",
      "numberOfGuests": 250,
      "venueAddress": {
        "city": "Mumbai",
        "state": "Maharashtra"
      }
    },
    "competitivePeriod": {
      "startTime": "2024-02-24T10:30:45.123Z",
      "endTime": "2024-02-27T10:30:45.123Z",
      "status": "ACTIVE"
    },
    "menuItems": [
      {
        "masterItemId": "item-001",
        "itemName": "Paneer Pakora",
        "quantity": 250
      },
      {
        "masterItemId": "item-005",
        "itemName": "Butter Chicken",
        "quantity": 250
      }
    ],
    "budget": {
      "estimatedBudget": 75000,
      "budgetRange": "75000-100000",
      "currency": "INR"
    },
    "expiresAt": "2024-03-03T10:30:45.123Z",
    "createdAt": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 5.2 Get My Bid Requests

### Endpoint
```http
GET /bids/requests?page=0&size=20&status=ACTIVE
Authorization: Bearer {accessToken}
```

### Query Parameters
| Parameter | Type | Description |
|-----------|------|-------------|
| `page` | integer | Page number |
| `size` | integer | Items per page |
| `status` | string | Filter by status (ACTIVE, ACCEPTED, EXPIRED, CANCELLED) |

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Bid requests retrieved successfully",
  "data": [
    {
      "bidRequestId": "breq-12345-67890",
      "status": "ACTIVE",
      "eventDetails": {
        "eventType": "WEDDING",
        "eventName": "John & Jane Wedding",
        "numberOfGuests": 250,
        "eventDate": "2024-06-15T18:00:00Z"
      },
      "budget": {
        "estimatedBudget": 75000,
        "currency": "INR"
      },
      "competitivePeriod": {
        "status": "ACTIVE",
        "endTime": "2024-02-27T10:30:45.123Z"
      },
      "createdAt": "2024-02-24T10:30:45.123Z"
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 5,
    "totalPages": 1
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 5.3 Get Bids for a Request (Customer Only)

### Endpoint
```http
GET /bids/requests/{bidRequestId}/bids
Authorization: Bearer {accessToken}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Bids for request retrieved successfully",
  "data": [
    {
      "bidId": "bid-vendor-001",
      "vendorId": "vendor-12345-67890",
      "vendorName": "Gourmet Catering Co.",
      "status": "PENDING",
      "quotedPrice": {
        "subtotal": 62500,
        "serviceCharge": 6250,
        "taxAmount": 6250,
        "totalAmount": 75000,
        "currency": "INR"
      },
      "itemizedPricing": [
        {
          "vendorItemId": "vitem-001",
          "itemName": "Paneer Pakora",
          "quantity": 250,
          "pricePerPlate": 150,
          "totalPrice": 37500
        },
        {
          "vendorItemId": "vitem-005",
          "itemName": "Butter Chicken",
          "quantity": 250,
          "pricePerPlate": 200,
          "totalPrice": 50000
        }
      ],
      "validityPeriodHours": 48,
      "submittedAt": "2024-02-24T11:00:00.000Z",
      "revisionCount": 0
    },
    {
      "bidId": "bid-vendor-002",
      "vendorId": "vendor-98765-43210",
      "vendorName": "Royal Feasts Catering",
      "status": "PENDING",
      "quotedPrice": {
        "subtotal": 65000,
        "serviceCharge": 6500,
        "taxAmount": 6500,
        "totalAmount": 78000,
        "currency": "INR"
      },
      "submittedAt": "2024-02-24T12:30:00.000Z"
    }
  ],
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 5.4 Submit Bid (Vendor Only)

### Endpoint
```http
POST /bids/requests/{bidRequestId}/submit-bid
Authorization: Bearer {vendorToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "quotedPrice": {
    "subtotal": 62500,
    "serviceCharge": 6250,
    "taxAmount": 6250,
    "totalAmount": 75000,
    "currency": "INR"
  },
  "itemizedPricing": [
    {
      "vendorItemId": "vitem-001",
      "itemName": "Paneer Pakora",
      "quantity": 250,
      "pricePerPlate": 150,
      "totalPrice": 37500
    },
    {
      "vendorItemId": "vitem-005",
      "itemName": "Butter Chicken",
      "quantity": 250,
      "pricePerPlate": 200,
      "totalPrice": 50000
    }
  ],
  "validityPeriodHours": 48
}
```

### Success Response (201 Created)
```json
{
  "success": true,
  "statusCode": 201,
  "message": "Bid submitted successfully",
  "data": {
    "bidId": "bid-vendor-12345",
    "bidRequestId": "breq-12345-67890",
    "vendorId": "vendor-12345-67890",
    "status": "PENDING",
    "quotedPrice": {
      "totalAmount": 75000,
      "currency": "INR"
    },
    "submittedAt": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 5.5 Accept Bid (Customer Only)

### Endpoint
```http
POST /bids/{bidId}/accept
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Bid accepted. Cooling period started.",
  "data": {
    "bidId": "bid-vendor-12345",
    "bidRequestId": "breq-12345-67890",
    "status": "ACCEPTED",
    "acceptedAt": "2024-02-24T10:30:45.123Z",
    "coolingPeriod": {
      "startTime": "2024-02-24T10:30:45.123Z",
      "endTime": "2024-02-25T10:30:45.123Z",
      "durationHours": 24
    },
    "message": "Cooling period (24 hours) has started. You can confirm the order after this period."
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 5.6 Revise Bid (Vendor Only)

### Endpoint
```http
PUT /bids/{bidId}
Authorization: Bearer {vendorToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "quotedPrice": {
    "subtotal": 60000,
    "serviceCharge": 6000,
    "taxAmount": 6000,
    "totalAmount": 72000,
    "currency": "INR"
  },
  "itemizedPricing": [
    {
      "vendorItemId": "vitem-001",
      "itemName": "Paneer Pakora",
      "quantity": 250,
      "pricePerPlate": 144,
      "totalPrice": 36000
    },
    {
      "vendorItemId": "vitem-005",
      "itemName": "Butter Chicken",
      "quantity": 250,
      "pricePerPlate": 192,
      "totalPrice": 48000
    }
  ]
}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Bid revised successfully",
  "data": {
    "bidId": "bid-vendor-12345",
    "quotedPrice": {
      "totalAmount": 72000
    },
    "revisionCount": 1,
    "revisedAt": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 5.7 Withdraw Bid (Vendor Only)

### Endpoint
```http
DELETE /bids/{bidId}
Authorization: Bearer {vendorToken}
Content-Type: application/json
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Bid withdrawn",
  "data": {
    "bidId": "bid-vendor-12345",
    "status": "WITHDRAWN",
    "withdrawnAt": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

# 6️⃣ Order Management Module

**Base Path:** `/orders`  
**Authentication:** ✅ Required

## 6.1 Create Order from Bid

### Endpoint
```http
POST /orders?bidRequestId=breq-12345-67890
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Query Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `bidRequestId` | string | ✅ | Bid request ID |

### Success Response (201 Created)
```json
{
  "success": true,
  "statusCode": 201,
  "message": "Order created successfully",
  "data": {
    "orderId": "order-12345-67890",
    "bidRequestId": "breq-12345-67890",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "CONFIRMED",
    "confirmedAt": "2024-02-24T10:30:45.123Z",
    "eventDetails": {
      "eventType": "WEDDING",
      "eventName": "John & Jane Wedding",
      "eventDate": "2024-06-15",
      "eventTime": "18:00",
      "numberOfGuests": 250,
      "venueAddress": {
        "city": "Mumbai",
        "state": "Maharashtra"
      }
    },
    "vendorOrders": [
      {
        "vendorOrderId": "vorder-001",
        "vendorId": "vendor-12345-67890",
        "vendorName": "Gourmet Catering Co.",
        "vendorStatus": "ACCEPTED",
        "deliveryStatus": "PENDING",
        "items": [
          {
            "vendorItemId": "vitem-001",
            "itemName": "Paneer Pakora",
            "quantity": 250,
            "pricePerPlate": 150,
            "totalPrice": 37500
          }
        ],
        "subtotal": 62500,
        "serviceCharge": 6250,
        "taxAmount": 6250,
        "totalAmount": 75000
      }
    ],
    "pricing": {
      "subtotal": 62500,
      "serviceCharges": 6250,
      "taxAmount": 6250,
      "platformFee": 1500,
      "discountAmount": 0,
      "totalAmount": 76250,
      "currency": "INR"
    },
    "paymentDetails": {
      "tokenAmount": 19062.50,
      "tokenPaid": true,
      "tokenPaymentId": "txn-12345",
      "tokenPaidAt": "2024-02-24T10:30:45.123Z",
      "balanceDue": 57187.50,
      "paymentStatus": "TOKEN_PAID"
    },
    "createdAt": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 6.2 Get All Orders (User)

### Endpoint
```http
GET /orders?page=0&size=20&status=CONFIRMED
Authorization: Bearer {accessToken}
```

### Query Parameters
| Parameter | Type | Description |
|-----------|------|-------------|
| `page` | integer | Page number |
| `size` | integer | Items per page |
| `status` | string | Filter by status |

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Orders retrieved successfully",
  "data": [
    {
      "orderId": "order-12345-67890",
      "status": "CONFIRMED",
      "eventDetails": {
        "eventName": "John & Jane Wedding",
        "eventDate": "2024-06-15"
      },
      "vendorOrders": [
        {
          "vendorName": "Gourmet Catering Co.",
          "deliveryStatus": "PENDING"
        }
      ],
      "pricing": {
        "totalAmount": 76250,
        "currency": "INR"
      },
      "createdAt": "2024-02-24T10:30:45.123Z"
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 15,
    "totalPages": 1
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 6.3 Get Order Details

### Endpoint
```http
GET /orders/{orderId}
Authorization: Bearer {accessToken}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Order details retrieved successfully",
  "data": {
    "orderId": "order-12345-67890",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "bidRequestId": "breq-12345-67890",
    "status": "CONFIRMED",
    "confirmedAt": "2024-02-24T10:30:45.123Z",
    "eventDetails": {
      "eventType": "WEDDING",
      "eventName": "John & Jane Wedding",
      "eventDate": "2024-06-15",
      "eventTime": "18:00",
      "numberOfGuests": 250,
      "venueAddress": {
        "streetAddress": "Grand Hotel, 789 Wedding Lane",
        "city": "Mumbai",
        "state": "Maharashtra",
        "postalCode": "400001",
        "country": "India",
        "gpsCoordinates": {
          "latitude": 19.0760,
          "longitude": 72.8777
        }
      }
    },
    "vendorOrders": [
      {
        "vendorOrderId": "vorder-001",
        "vendorId": "vendor-12345-67890",
        "vendorUserId": "vendor-user-123",
        "vendorName": "Gourmet Catering Co.",
        "vendorStatus": "ACCEPTED",
        "deliveryStatus": "PENDING",
        "items": [
          {
            "vendorItemId": "vitem-001",
            "itemName": "Paneer Pakora",
            "quantity": 250,
            "pricePerPlate": 150,
            "totalPrice": 37500
          }
        ],
        "subtotal": 62500,
        "serviceCharge": 6250,
        "taxAmount": 6250,
        "totalAmount": 75000,
        "acceptedAt": "2024-02-24T10:30:45.123Z"
      }
    ],
    "pricing": {
      "subtotal": 62500,
      "serviceCharges": 6250,
      "taxAmount": 6250,
      "platformFee": 1500,
      "discountAmount": 0,
      "totalAmount": 76250,
      "currency": "INR"
    },
    "paymentDetails": {
      "tokenAmount": 19062.50,
      "tokenPaid": true,
      "tokenPaymentId": "txn-12345",
      "tokenPaidAt": "2024-02-24T10:30:45.123Z",
      "totalPaid": 19062.50,
      "balanceDue": 57187.50,
      "paymentStatus": "TOKEN_PAID"
    },
    "specialInstructions": "Need food ready by 6 PM sharp. Vegetarian and non-vegetarian items to be kept separate.",
    "createdAt": "2024-02-24T10:30:45.123Z",
    "updatedAt": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 6.4 Update Order Status

### Endpoint
```http
PATCH /orders/{orderId}/status?status=IN_PREPARATION
Authorization: Bearer {vendorToken}
Content-Type: application/json
```

### Query Parameters
| Parameter | Type | Required | Allowed Values |
|-----------|------|----------|----------------|
| `status` | string | ✅ | `CONFIRMED`, `IN_PREPARATION`, `READY_FOR_DELIVERY`, `DELIVERED`, `COMPLETED`, `CANCELLED` |

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Order status updated successfully",
  "data": {
    "orderId": "order-12345-67890",
    "status": "IN_PREPARATION",
    "updatedAt": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 6.5 Cancel Order

### Endpoint
```http
POST /orders/{orderId}/cancel?reason=Emergency%20came%20up
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Query Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `reason` | string | ✅ | Cancellation reason |

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Order cancelled successfully",
  "data": {
    "orderId": "order-12345-67890",
    "status": "CANCELLED",
    "cancellation": {
      "isCancelled": true,
      "cancelledByType": "USER",
      "cancelledBy": "550e8400-e29b-41d4-a716-446655440000",
      "cancellationReason": "Emergency came up",
      "cancelledAt": "2024-02-24T10:30:45.123Z",
      "refundStatus": "INITIATED",
      "refundAmount": 57187.50
    }
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

# 7️⃣ Payment Module

**Base Path:** `/payments`  
**Authentication:** ✅ Required (for POST endpoints)

## 7.1 Initiate Payment

### Endpoint
```http
POST /payments/initiate
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "orderId": "order-12345-67890",
  "amount": 76250,
  "paymentType": "TOKEN"
}
```

### Request Validation Rules
| Field | Type | Required | Rules |
|-------|------|----------|-------|
| `orderId` | string | ❌ | Order ID (for full payment) |
| `bidId` | string | ❌ | Bid ID (for token payment) |
| `amount` | number | ✅ | > 0 |
| `paymentType` | enum | ✅ | `TOKEN`, `FULL`, `PARTIAL` |

**Note:** Either `orderId` or `bidId` must be provided.

### Success Response (200 OK)

**For Razorpay (India - INR):**
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Payment initiated successfully",
  "data": {
    "transactionId": "txn-12345-67890",
    "orderId": "order-12345-67890",
    "gatewayOrderId": "order_LuSijuBiuj123456",
    "gatewayName": "RAZORPAY",
    "amount": 76250,
    "currency": "INR",
    "keyId": "rzp_live_1234567890abcd",
    "message": "Payment intent created. Use this to proceed with Razorpay payment.",
    "redirectUrl": "https://checkout.razorpay.com/v2/checkout.js"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

**For Stripe (USA - USD):**
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Payment initiated successfully",
  "data": {
    "transactionId": "txn-12345-67890",
    "orderId": "order-12345-67890",
    "gatewayOrderId": "pi_1A0000000000000000000000",
    "gatewayName": "STRIPE",
    "amount": 912.5,
    "currency": "USD",
    "clientSecret": "pi_1A0000000000000000000000_secret_EXAMPLESecretKey123456",
    "publishableKey": "pk_live_123456789abcdef",
    "message": "Payment intent created. Use clientSecret to complete payment.",
    "redirectUrl": "https://checkout.stripe.com/pay/cs_test_..."
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 7.2 Verify Payment

### Endpoint
```http
POST /payments/verify
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload

**For Razorpay:**
```json
{
  "gatewayOrderId": "order_LuSijuBiuj123456",
  "gatewayPaymentId": "pay_LuSijuBiuj123456",
  "signature": "9ef4dffbfd84f1318f6739a3ce19f9d85851857ae648f114332d8401e0949a3d"
}
```

**For Stripe:**
```json
{
  "gatewayOrderId": "pi_1A0000000000000000000000",
  "paymentIntentId": "pi_1A0000000000000000000000"
}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Payment verified successfully",
  "data": {
    "transactionId": "txn-12345-67890",
    "orderId": "order-12345-67890",
    "status": "SUCCESS",
    "amount": 76250,
    "currency": "INR",
    "processedAt": "2024-02-24T10:30:45.123Z",
    "message": "Payment successful. Order has been confirmed."
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

### Error Response - Payment Failed
```json
{
  "success": false,
  "statusCode": 400,
  "message": "Payment verification failed",
  "error": {
    "code": "INVALID_SIGNATURE",
    "message": "Payment verification failed",
    "details": "The payment signature does not match",
    "path": "/api/v1/payments/verify",
    "timestamp": "2024-02-24T10:30:45.123Z"
  }
}
```

---

# 8️⃣ Chat Module

**Base Path:** `/chat`  
**Authentication:** ✅ Required  
**WebSocket:** ✅ Available at `/ws`

## 8.1 Get Conversations

### Endpoint
```http
GET /chat/conversations?page=0&size=20
Authorization: Bearer {accessToken}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Conversations retrieved successfully",
  "data": [
    {
      "conversationId": "conv-12345-67890",
      "conversationType": "USER_VENDOR",
      "status": "ACTIVE",
      "participants": [
        {
          "userId": "550e8400-e29b-41d4-a716-446655440000",
          "userType": "USER",
          "name": "John Doe",
          "profilePictureUrl": "https://storage.googleapis.com/bucket/profiles/john.jpg"
        },
        {
          "userId": "vendor-12345-67890",
          "userType": "VENDOR",
          "name": "Gourmet Catering Co.",
          "profilePictureUrl": "https://storage.googleapis.com/bucket/logos/gourmet.png"
        }
      ],
      "lastMessage": {
        "message": "When can we confirm the order?",
        "senderId": "550e8400-e29b-41d4-a716-446655440000",
        "timestamp": "2024-02-24T10:30:45.123Z"
      },
      "unreadCount": 2,
      "createdAt": "2024-02-20T08:00:00.000Z"
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 8,
    "totalPages": 1
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 8.2 Get Conversation History

### Endpoint
```http
GET /chat/conversations/{conversationId}/messages?page=0&size=50
Authorization: Bearer {accessToken}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Messages retrieved successfully",
  "data": [
    {
      "messageId": "msg-12345",
      "conversationId": "conv-12345-67890",
      "senderId": "550e8400-e29b-41d4-a716-446655440000",
      "senderType": "USER",
      "message": "Hi, can you provide a quote for 250 guests?",
      "messageType": "TEXT",
      "timestamp": "2024-02-24T10:00:00.000Z",
      "readReceipts": [
        {
          "userId": "vendor-12345-67890",
          "readAt": "2024-02-24T10:05:00.000Z"
        }
      ]
    },
    {
      "messageId": "msg-12346",
      "conversationId": "conv-12345-67890",
      "senderId": "vendor-12345-67890",
      "senderType": "VENDOR",
      "message": "Yes, we can handle 250 guests easily. Our pricing starts at ₹750 per plate.",
      "messageType": "TEXT",
      "timestamp": "2024-02-24T10:15:00.000Z",
      "readReceipts": [
        {
          "userId": "550e8400-e29b-41d4-a716-446655440000",
          "readAt": "2024-02-24T10:20:00.000Z"
        }
      ]
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 50,
    "totalElements": 25,
    "totalPages": 1
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 8.3 Start Conversation

### Endpoint
```http
POST /chat/conversations
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "otherUserId": "vendor-12345-67890",
  "type": "USER_VENDOR"
}
```

### Alternative Payload (Using Vendor ID)
```json
{
  "vendorId": "vendor-12345-67890",
  "type": "USER_VENDOR"
}
```

### Success Response (201 Created)
```json
{
  "success": true,
  "statusCode": 201,
  "message": "Conversation created or retrieved",
  "data": {
    "conversationId": "conv-12345-67890",
    "conversationType": "USER_VENDOR",
    "status": "ACTIVE",
    "participants": [
      {
        "userId": "550e8400-e29b-41d4-a716-446655440000",
        "userType": "USER",
        "name": "John Doe"
      },
      {
        "userId": "vendor-12345-67890",
        "userType": "VENDOR",
        "name": "Gourmet Catering Co."
      }
    ],
    "createdAt": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 8.4 WebSocket Chat (STOMP Protocol)

### Connect to WebSocket
```
WebSocket URL: ws://localhost:8080/api/v1/ws
Headers: Authorization: Bearer {accessToken}
```

### Subscribe to Conversation
```
SUBSCRIBE
id: sub-1
destination: /topic/conversations.{conversationId}
```

### Send Message
```
SEND
destination: /app/chat.sendMessage
content-length: 123

{
  "conversationId": "conv-12345-67890",
  "message": "What about dietary restrictions?",
  "messageType": "TEXT"
}
```

### Receive Message
```
MESSAGE
destination: /topic/conversations.conv-12345-67890
message-id: msg-id-123
timestamp: 1234567890

{
  "messageId": "msg-12347",
  "senderId": "vendor-12345-67890",
  "senderType": "VENDOR",
  "message": "We can accommodate all dietary needs. Just let us know!",
  "messageType": "TEXT",
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

# 9️⃣ Support Tickets Module

**Base Path:** `/support/tickets`  
**Authentication:** ✅ Required

## 9.1 Create Support Ticket

### Endpoint
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
  "subject": "Food arrived late for wedding event",
  "description": "The catering was supposed to arrive at 6 PM but came at 7 PM. This caused significant delay in serving guests.",
  "orderId": "order-12345-67890",
  "vendorId": "vendor-12345-67890",
  "attachmentUrls": [
    "https://storage.googleapis.com/bucket/tickets/photo-1.jpg",
    "https://storage.googleapis.com/bucket/tickets/photo-2.jpg"
  ]
}
```

### Request Validation Rules
| Field | Type | Required | Rules |
|-------|------|----------|-------|
| `category` | enum | ✅ | `ORDER`, `PAYMENT`, `VENDOR`, `ACCOUNT`, `OTHER` |
| `subcategory` | string | ✅ | Based on category |
| `priority` | enum | ✅ | `LOW`, `MEDIUM`, `HIGH`, `URGENT` |
| `subject` | string | ✅ | Max 255 chars |
| `description` | string | ✅ | Max 5000 chars |
| `attachmentUrls` | array | ❌ | Max 5 files |

### Success Response (201 Created)
```json
{
  "success": true,
  "statusCode": 201,
  "message": "Support ticket created successfully",
  "data": {
    "ticketId": "tkt-12345-67890",
    "ticketNumber": "TKT-2024021234567",
    "status": "OPEN",
    "priority": "HIGH",
    "category": "ORDER",
    "subject": "Food arrived late for wedding event",
    "description": "The catering was supposed to arrive at 6 PM but came at 7 PM.",
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "createdByName": "John Doe",
    "assignedTo": "agent-98765",
    "assignedToName": "Sarah Smith",
    "sla": {
      "priority": "HIGH",
      "responseTimeMinutes": 60,
      "resolutionTimeMinutes": 1440,
      "responseDeadline": "2024-02-24T11:30:45.123Z",
      "resolutionDeadline": "2024-02-25T10:30:45.123Z",
      "status": "ON_TRACK"
    },
    "createdAt": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 9.2 Get My Tickets (User)

### Endpoint
```http
GET /support/tickets?page=0&size=20&status=OPEN
Authorization: Bearer {accessToken}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Tickets retrieved successfully",
  "data": [
    {
      "ticketId": "tkt-12345-67890",
      "ticketNumber": "TKT-2024021234567",
      "status": "OPEN",
      "priority": "HIGH",
      "subject": "Food arrived late for wedding event",
      "createdByName": "John Doe",
      "assignedToName": "Sarah Smith",
      "createdAt": "2024-02-24T10:30:45.123Z",
      "lastActivityAt": "2024-02-24T10:50:00.000Z"
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 3,
    "totalPages": 1
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 9.3 Get All Tickets (Admin/Support Agent)

### Endpoint
```http
GET /support/admin/tickets?page=0&size=20&status=OPEN&priority=HIGH
Authorization: Bearer {adminToken}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Support tickets retrieved successfully",
  "data": [
    {
      "ticketId": "tkt-12345-67890",
      "ticketNumber": "TKT-2024021234567",
      "status": "OPEN",
      "priority": "HIGH",
      "category": "ORDER",
      "subject": "Food arrived late for wedding event",
      "createdByName": "John Doe",
      "createdByEmail": "john@example.com",
      "assignedToName": "Sarah Smith",
      "sla": {
        "status": "ON_TRACK",
        "responseDeadline": "2024-02-24T11:30:45.123Z",
        "resolutionDeadline": "2024-02-25T10:30:45.123Z"
      },
      "createdAt": "2024-02-24T10:30:45.123Z"
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 45,
    "totalPages": 3
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

# 🔟 Reviews Module

**Base Path:** `/reviews`

## 10.1 Create Review

### Endpoint
```http
POST /reviews
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "orderId": "order-12345-67890",
  "rating": 5,
  "foodQualityRating": 5,
  "serviceQualityRating": 4,
  "hygieneRating": 5,
  "valueForMoneyRating": 4,
  "punctualityRating": 5,
  "reviewText": "Excellent catering service! The food was delicious and the service staff was very professional. Highly recommended for any event.",
  "images": [
    "https://storage.googleapis.com/bucket/reviews/photo-1.jpg",
    "https://storage.googleapis.com/bucket/reviews/photo-2.jpg"
  ]
}
```

### Request Validation Rules
| Field | Type | Required | Rules |
|-------|------|----------|-------|
| `orderId` | string | ✅ | Valid order ID |
| `rating` | integer | ✅ | 1-5 stars |
| `foodQualityRating` | integer | ❌ | 1-5 stars |
| `serviceQualityRating` | integer | ❌ | 1-5 stars |
| `hygieneRating` | integer | ❌ | 1-5 stars |
| `valueForMoneyRating` | integer | ❌ | 1-5 stars |
| `punctualityRating` | integer | ❌ | 1-5 stars |
| `reviewText` | string | ❌ | Max 2000 chars |
| `images` | array | ❌ | Max 5 images |

### Success Response (201 Created)
```json
{
  "success": true,
  "statusCode": 201,
  "message": "Review created successfully",
  "data": {
    "reviewId": "rev-12345-67890",
    "orderId": "order-12345-67890",
    "vendorId": "vendor-12345-67890",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "rating": 5,
    "foodQualityRating": 5,
    "serviceQualityRating": 4,
    "hygieneRating": 5,
    "valueForMoneyRating": 4,
    "punctualityRating": 5,
    "reviewText": "Excellent catering service!...",
    "status": "APPROVED",
    "createdAt": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 10.2 Get Vendor Reviews

### Endpoint
```http
GET /reviews?vendorId=vendor-12345-67890&page=0&size=20
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Reviews retrieved successfully",
  "data": [
    {
      "reviewId": "rev-12345-67890",
      "vendorId": "vendor-12345-67890",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "userName": "John Doe",
      "rating": 5,
      "reviewText": "Excellent catering service!...",
      "images": [
        "https://storage.googleapis.com/bucket/reviews/photo-1.jpg"
      ],
      "helpfulCount": 12,
      "createdAt": "2024-02-24T10:30:45.123Z"
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 245,
    "totalPages": 13
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 10.3 Add Vendor Response to Review

### Endpoint
```http
POST /reviews/{reviewId}/vendor-response
Authorization: Bearer {vendorToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "responseText": "Thank you for the wonderful feedback! We're thrilled that you enjoyed our service. We look forward to catering for more of your events."
}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Vendor response added successfully",
  "data": {
    "reviewId": "rev-12345-67890",
    "vendorResponse": {
      "responseText": "Thank you for the wonderful feedback!...",
      "respondedAt": "2024-02-24T10:30:45.123Z"
    }
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

# 1️⃣1️⃣ Promo Codes Module

**Base Path:** `/promos`

## 11.1 Create Promo Code (Admin)

### Endpoint
```http
POST /promos
Authorization: Bearer {adminToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "code": "WELCOME2024",
  "title": "Welcome Offer",
  "description": "20% off on your first order",
  "type": "PERCENTAGE",
  "value": 20,
  "maxDiscountAmount": 500,
  "minOrderAmount": 1000,
  "validFrom": "2024-02-24T00:00:00Z",
  "validTo": "2024-03-31T23:59:59Z",
  "usageLimitGlobal": 1000,
  "usageLimitPerUser": 1,
  "applicableTo": "ALL",
  "applicableVendorIds": [],
  "applicableUserIds": [],
  "applicableCuisines": [],
  "firstOrderOnly": true
}
```

### Request Validation Rules
| Field | Type | Required | Rules |
|-------|------|----------|-------|
| `code` | string | ✅ | Unique, uppercase, max 50 chars |
| `type` | enum | ✅ | `PERCENTAGE`, `FIXED_AMOUNT` |
| `value` | number | ✅ | > 0 |
| `minOrderAmount` | number | ✅ | > 0 |
| `validTo` | ISO8601 | ✅ | Future date |
| `usageLimitPerUser` | integer | ✅ | >= 1 |

### Success Response (201 Created)
```json
{
  "success": true,
  "statusCode": 201,
  "message": "Promo code created successfully",
  "data": {
    "promoCodeId": "promo-12345-67890",
    "code": "WELCOME2024",
    "title": "Welcome Offer",
    "type": "PERCENTAGE",
    "value": 20,
    "status": "ACTIVE",
    "usageCount": 0,
    "validFrom": "2024-02-24T00:00:00Z",
    "validTo": "2024-03-31T23:59:59Z",
    "createdAt": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 11.2 Apply Promo Code

### Endpoint
```http
POST /promos/apply
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request Payload
```json
{
  "code": "WELCOME2024",
  "orderTotal": 2500
}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Promo code applied successfully",
  "data": {
    "promoCodeId": "promo-12345-67890",
    "code": "WELCOME2024",
    "orderTotal": 2500,
    "discountAmount": 500,
    "finalAmount": 2000,
    "valid": true,
    "message": "Promo code applied successfully"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

### Error Response - Invalid Code
```json
{
  "success": false,
  "statusCode": 200,
  "message": "Invalid promo code",
  "data": {
    "code": "INVALID2024",
    "orderTotal": 2500,
    "valid": false,
    "errorCode": "INVALID_CODE",
    "message": "Invalid or expired promo code"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

# 1️⃣2️⃣ Loyalty Points Module

**Base Path:** `/loyalty`  
**Authentication:** ✅ Required

## 12.1 Get Loyalty Balance

### Endpoint
```http
GET /loyalty/balance
Authorization: Bearer {accessToken}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Loyalty balance retrieved successfully",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "pointsBalance": 1250,
    "lifetimePoints": 3500,
    "tier": "GOLD",
    "earnMultiplier": 1.5,
    "redeemMultiplier": 1.0,
    "rupeesEquivalent": 312.50,
    "nextTierRequirement": {
      "tier": "PLATINUM",
      "pointsNeeded": 5000,
      "pointsToGo": 1500
    },
    "pointsExpiringOn": "2025-02-24T10:30:45.123Z",
    "pointsExpiringCount": 100
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 12.2 Get Loyalty Transactions

### Endpoint
```http
GET /loyalty/transactions?page=0&size=20
Authorization: Bearer {accessToken}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Loyalty transactions retrieved successfully",
  "data": [
    {
      "transactionId": "ltxn-12345",
      "type": "EARN",
      "points": 500,
      "balanceAfter": 1250,
      "description": "Points earned from order completion",
      "orderId": "order-12345-67890",
      "createdAt": "2024-02-24T10:30:45.123Z"
    },
    {
      "transactionId": "ltxn-12346",
      "type": "REDEEM",
      "points": -250,
      "balanceAfter": 750,
      "description": "Points redeemed for order discount",
      "orderId": "order-12345-67891",
      "createdAt": "2024-02-20T08:00:00.000Z"
    },
    {
      "transactionId": "ltxn-12347",
      "type": "BONUS",
      "points": 200,
      "balanceAfter": 950,
      "description": "Welcome bonus for joining referral",
      "createdAt": "2024-02-15T12:00:00.000Z"
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 45,
    "totalPages": 3
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

# 1️⃣3️⃣ Referral Module

**Base Path:** `/referrals`  
**Authentication:** ✅ Required

## 13.1 Get or Create Referral Code

### Endpoint
```http
GET /referrals/my-code
Authorization: Bearer {accessToken}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Referral code retrieved successfully",
  "data": {
    "codeId": "rcode-12345-67890",
    "code": "REF550E8400E29B41D4",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "ACTIVE",
    "totalReferrals": 5,
    "totalEarnings": 2500,
    "referralUrl": "https://bidzaro.com/ref?code=REF550E8400E29B41D4",
    "referralLinkQR": "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=REF550E8400E29B41D4",
    "createdAt": "2024-01-15T08:00:00.000Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 13.2 Get Referral Statistics

### Endpoint
```http
GET /referrals/stats
Authorization: Bearer {accessToken}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Referral statistics retrieved successfully",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "totalReferrals": 5,
    "completedReferrals": 3,
    "pendingReferrals": 2,
    "totalEarningsPoints": 2500,
    "totalEarningsAmount": 625,
    "referralDetails": [
      {
        "referredUserId": "user-referred-001",
        "referredUserName": "Jane Smith",
        "status": "COMPLETED",
        "rewardStatus": "DISTRIBUTED",
        "pointsEarned": 500,
        "referredAt": "2024-02-20T08:00:00.000Z",
        "completedAt": "2024-02-24T10:30:45.123Z"
      },
      {
        "referredUserId": "user-referred-002",
        "referredUserName": "Mike Johnson",
        "status": "PENDING",
        "rewardStatus": "PENDING",
        "pointsToEarn": 500,
        "referredAt": "2024-02-22T14:00:00.000Z"
      }
    ]
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

# 1️⃣4️⃣ Admin Dashboard Module

**Base Path:** `/admin`  
**Authentication:** ✅ Required (Admin Role)

## 14.1 Get Dashboard Statistics

### Endpoint
```http
GET /admin/dashboard/stats
Authorization: Bearer {adminToken}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Dashboard statistics retrieved successfully",
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
      "totalRevenue": 4250000,
      "revenueToday": 45000,
      "revenueThisWeek": 325000,
      "revenueThisMonth": 1250000,
      "platformFees": 85000,
      "pendingPayouts": 120000,
      "currency": "INR"
    },
    "bidStats": {
      "totalBidRequests": 2300,
      "activeBidRequests": 156,
      "totalBidsSubmitted": 8500,
      "acceptedBids": 2150,
      "averageBidsPerRequest": 3.7
    }
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

# 1️⃣5️⃣ Analytics Module

**Base Path:** `/analytics`  
**Authentication:** ✅ Required

## 15.1 Get Platform Overview (Admin)

### Endpoint
```http
GET /analytics/platform/overview
Authorization: Bearer {adminToken}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Platform overview retrieved successfully",
  "data": {
    "platformMetrics": {
      "totalUsers": 5420,
      "totalVendors": 342,
      "totalOrders": 12450,
      "totalRevenue": 4250000,
      "platformEarnings": 85000,
      "averageOrderValue": 341.25,
      "conversionRate": 22.5
    },
    "growthMetrics": {
      "userGrowthPercentage": 15.5,
      "vendorGrowthPercentage": 8.2,
      "orderGrowthPercentage": 18.7,
      "revenueGrowthPercentage": 22.3
    },
    "topVendors": [
      {
        "vendorId": "vendor-top-001",
        "businessName": "Gourmet Catering Co.",
        "totalOrders": 145,
        "totalRevenue": 145000,
        "averageRating": 4.8
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
      {
        "date": "2024-02-24",
        "revenue": 45000,
        "orders": 32
      }
    ]
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 15.2 Get Vendor Dashboard

### Endpoint
```http
GET /analytics/vendor/{vendorId}/dashboard
Authorization: Bearer {vendorToken}
```

### Success Response (200 OK)
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Vendor dashboard retrieved successfully",
  "data": {
    "vendorId": "vendor-12345-67890",
    "vendorName": "Gourmet Catering Co.",
    "metrics": {
      "totalOrders": 145,
      "completedOrders": 142,
      "pendingOrders": 3,
      "cancelledOrders": 0,
      "totalRevenue": 145000,
      "pendingPayouts": 12000,
      "thisMonthRevenue": 35000,
      "currency": "INR"
    },
    "bidMetrics": {
      "totalBidsSubmitted": 250,
      "acceptedBids": 145,
      "pendingBids": 23,
      "acceptanceRate": 58,
      "averageBidAmount": 580,
      "currency": "INR"
    },
    "performance": {
      "averageRating": 4.8,
      "totalReviews": 145,
      "responseRate": 98.5,
      "onTimeDeliveryRate": 99.3,
      "repeatCustomers": 42
    },
    "recentOrders": [
      {
        "orderId": "order-12345-67890",
        "customerName": "John Doe",
        "status": "COMPLETED",
        "totalAmount": 1000,
        "completedAt": "2024-02-24T10:30:45.123Z"
      }
    ]
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

# 1️⃣6️⃣ File Upload Module

**Base Path:** `/uploads`

## 16.1 Upload Image

### Endpoint
```http
POST /uploads/image
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data
```

### Form Data
```
file: <binary image data>
entityType: PROFILE  (or VENDOR, MENU_ITEM, REVIEW, TICKET, etc.)
entityId: user-12345-67890
```

### Success Response (201 Created)
```json
{
  "success": true,
  "statusCode": 201,
  "message": "Image uploaded successfully",
  "data": {
    "fileId": "file-12345-67890",
    "fileName": "profile-1708773045123.jpg",
    "originalName": "profile.jpg",
    "fileType": "IMAGE",
    "contentType": "image/jpeg",
    "fileSize": 245678,
    "fileUrl": "https://storage.googleapis.com/bucket/images/profile-1708773045123.jpg",
    "entityType": "PROFILE",
    "entityId": "user-12345-67890",
    "createdAt": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 16.2 Upload Document

### Endpoint
```http
POST /uploads/document
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data
```

### Form Data
```
file: <binary PDF/DOC data>
entityType: VENDOR_LICENSE
entityId: vendor-12345-67890
```

### Success Response (201 Created)
```json
{
  "success": true,
  "statusCode": 201,
  "message": "Document uploaded successfully",
  "data": {
    "fileId": "file-12345-67891",
    "fileName": "license-1708773045123.pdf",
    "originalName": "business_license.pdf",
    "fileType": "DOCUMENT",
    "contentType": "application/pdf",
    "fileSize": 512345,
    "fileUrl": "https://storage.googleapis.com/bucket/documents/license-1708773045123.pdf",
    "entityType": "VENDOR_LICENSE",
    "entityId": "vendor-12345-67890",
    "createdAt": "2024-02-24T10:30:45.123Z"
  },
  "timestamp": "2024-02-24T10:30:45.123Z"
}
```

---

## 16.3 Delete File

### Endpoint
```http
DELETE /uploads/{fileId}
Authorization: Bearer {accessToken}
```

### Success Response (204 No Content)
```
HTTP/1.1 204 No Content
```

---

# 🎯 Common Query Parameters

All list endpoints support these query parameters:

```
GET /api/v1/resource?page=0&size=20&sort=createdAt&sortDir=desc
```

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | integer | 0 | Page number (0-indexed) |
| `size` | integer | 20 | Items per page (max 100) |
| `sort` | string | createdAt | Field to sort by |
| `sortDir` | string | desc | Sort direction: `asc` or `desc` |

---

# 🔐 Authentication Headers

All protected endpoints require:

```http
Authorization: Bearer {accessToken}
Content-Type: application/json
```

**Where `{accessToken}`** is received from `/auth/login` or `/auth/refresh-token`

---

# ⏰ Timestamp Format

All timestamps use **ISO 8601** format:

```
2024-02-24T10:30:45.123Z
```

---

# 🚀 Example cURL Requests

### Register User
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "phone": "+919876543210",
    "password": "SecurePass@123",
    "firstName": "John",
    "lastName": "Doe",
    "userType": "USER",
    "country": "USA"
  }'
```

### Login User
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "john@example.com",
    "password": "SecurePass@123"
  }'
```

### Get User Profile (Protected)
```bash
curl -X GET http://localhost:8080/api/v1/users/profile \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Create Bid Request
```bash
curl -X POST http://localhost:8080/api/v1/bids/requests \
  -H "Authorization: Bearer {accessToken}" \
  -H "Content-Type: application/json" \
  -d '{
    "eventDetails": {
      "eventType": "WEDDING",
      "eventName": "John & Jane Wedding",
      "eventDate": "2024-06-15T18:00:00Z",
      "numberOfGuests": 250,
      "venueAddress": {
        "streetAddress": "Grand Hotel, 789 Wedding Lane",
        "city": "Mumbai",
        "state": "Maharashtra",
        "postalCode": "400001",
        "country": "India",
        "latitude": 19.0760,
        "longitude": 72.8777
      }
    },
    "menuItems": [
      {
        "masterItemId": "item-001",
        "itemName": "Paneer Pakora",
        "quantity": 250
      }
    ],
    "budget": {
      "estimatedBudget": 75000,
      "currency": "INR"
    }
  }'
```

### Upload Image
```bash
curl -X POST http://localhost:8080/api/v1/uploads/image \
  -H "Authorization: Bearer {accessToken}" \
  -F "file=@/path/to/image.jpg" \
  -F "entityType=PROFILE" \
  -F "entityId=user-12345"
```

---

# 📌 Important Notes

1. **Rate Limiting:** 100 requests per minute per IP
2. **Token Expiry:** Access token valid for 7 days, refresh token for 7 days
3. **Password Requirements:** Min 8 chars, 1 uppercase, 1 digit, 1 special char
4. **Phone Format:** E.164 international format (e.g., +919876543210)
5. **File Size Limits:** Images 5MB, Documents 10MB
6. **Pagination:** Default size 20, max 100
7. **Sorting:** Default by `createdAt` descending
8. **Timezone:** All times in UTC (Z suffix)
9. **Currency:** Determined by user's country (USD for USA, INR for India)
10. **CORS:** Enabled for `http://localhost:3000`, `http://localhost:5173`

---

**Last Updated:** February 24, 2026  
**API Version:** 1.0.0  
**Status:** Production Ready ✅

