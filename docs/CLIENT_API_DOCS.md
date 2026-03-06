# 📱 CLIENT (User) API Documentation
**Bidzaro Catering Platform** | Base URL: `http://localhost:8080/api/v1`

> 🔒 = Requires `Authorization: Bearer <accessToken>` header
> ✅ = Required field | ⬜ = Optional field

---

## 📋 Table of Contents
1. [Enums Reference](#-enums-reference)
2. [Authentication](#-authentication)
3. [User Profile](#-user-profile)
4. [Addresses](#-addresses)
5. [Vendor Discovery](#-vendor-discovery)
6. [Menu & Categories](#-menu--categories)
7. [Cart](#-cart)
8. [Wishlist](#-wishlist)
9. [Bid Requests](#-bid-requests)
10. [Orders](#-orders)
11. [Payments](#-payments)
12. [Reviews](#-reviews)
13. [Loyalty Points](#-loyalty-points)
14. [Promo Codes](#-promo-codes)
15. [Referrals](#-referrals)
16. [Notifications](#-notifications)
17. [Chat](#-chat)
18. [File Upload](#-file-upload)
19. [Support Tickets](#-support-tickets)

---

## 🔢 Enums Reference

### UserType
| Value | Description |
|-------|-------------|
| `USER` | Regular customer |
| `VENDOR` | Catering service provider |
| `ADMIN` | Platform administrator |
| `SUPPORT_AGENT` | Customer support agent |

### UserStatus
| Value | Description |
|-------|-------------|
| `PENDING_VERIFICATION` | Registered but email/phone not verified |
| `ACTIVE` | Fully verified and active |
| `SUSPENDED` | Account suspended by admin |
| `DELETED` | Soft-deleted |

### Gender
| Value |
|-------|
| `MALE` |
| `FEMALE` |
| `OTHER` |
| `PREFER_NOT_TO_SAY` |

### VerificationType (OTP)
| Value | Use Case |
|-------|----------|
| `EMAIL` | Verify registered email |
| `PHONE` | Verify registered phone |
| `PASSWORD_RESET` | Reset password flow |
| `TWO_FACTOR` | 2FA login step |
| `BUSINESS_EMAIL` | Verify vendor business email |
| `BUSINESS_PHONE` | Verify vendor business phone |

### OTP Channel
| Value | Description |
|-------|-------------|
| `AUTO` | WhatsApp first, fallback to SMS |
| `WHATSAPP` | WhatsApp only |
| `SMS` | SMS only |

### OrderStatus
| Value | Description |
|-------|-------------|
| `PENDING_TOKEN_PAYMENT` | Bid accepted, awaiting token payment (24h window) |
| `CONFIRMED` | Token paid, order confirmed |
| `IN_PREPARATION` | Vendor is preparing food |
| `READY_FOR_DELIVERY` | Food ready, awaiting delivery |
| `DELIVERING` | Out for delivery |
| `DELIVERED` | Delivered to venue |
| `COMPLETED` | Event completed, order closed |
| `CANCELLED` | Order cancelled |

### PaymentStatus (inside order)
| Value |
|-------|
| `TOKEN_PENDING` |
| `TOKEN_PAID` |
| `PARTIALLY_PAID` |
| `FULLY_PAID` |

### BidRequestStatus
| Value | Description |
|-------|-------------|
| `DRAFT` | Not yet submitted |
| `ACTIVE` | Accepting bids from vendors |
| `COMPETITIVE` | 3+ bids received |
| `PENDING_TOKEN_PAYMENT` | User accepted a bid, awaiting token payment |
| `COOLING` | In 24h cooling period after accepting |
| `ACCEPTED` | Token paid, order created |
| `EXPIRED` | Bid period expired |
| `CANCELLED` | User cancelled |

### PaymentType (Transaction)
| Value | Description |
|-------|-------------|
| `TOKEN` | Token/advance payment |
| `BALANCE` | Remaining balance |
| `FULL` | Full upfront payment |
| `INSTALLMENT` | Installment payment |
| `REFUND` | Refund transaction |

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
| Value | Country |
|-------|---------|
| `RAZORPAY` | India |
| `STRIPE` | USA |

### PaymentMethod
| Value |
|-------|
| `CARD` |
| `UPI` |
| `NET_BANKING` |
| `WALLET` |

### LoyaltyTier
| Value | Min Lifetime Points | Earn Multiplier |
|-------|--------------------|--------------------|
| `BRONZE` | 0 | 1.0× |
| `SILVER` | 1000 | 1.5× |
| `GOLD` | 5000 | 2.0× |
| `PLATINUM` | 10000 | 3.0× |

### NotificationChannel
| Value |
|-------|
| `EMAIL` |
| `SMS` |
| `PUSH` |
| `IN_APP` |
| `WHATSAPP` |

### ConversationType
| Value | Description |
|-------|-------------|
| `USER_VENDOR` | Customer ↔ Vendor |
| `USER_SUPPORT` | Customer ↔ Support Agent |
| `VENDOR_SUPPORT` | Vendor ↔ Support Agent |

### MessageType
| Value |
|-------|
| `TEXT` |
| `IMAGE` |
| `FILE` |
| `SYSTEM` |

### FoodType (Menu Item)
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

### ReviewStatus
| Value |
|-------|
| `PENDING` |
| `APPROVED` |
| `REJECTED` |
| `HIDDEN` |

### AddressType
| Value |
|-------|
| `HOME` |
| `WORK` |
| `OTHER` |

### Country
| Value | Currency | Payment Gateway |
|-------|----------|----------------|
| `USA` | USD | Stripe |
| `INDIA` | INR | Razorpay |

### TicketPriority
| Value | First Response SLA | Resolution SLA |
|-------|-------------------|----------------|
| `LOW` | 24 hours | 72 hours |
| `MEDIUM` | 8 hours | 48 hours |
| `HIGH` | 4 hours | 24 hours |
| `URGENT` | 1 hour | 4 hours |

---

## 🔐 Authentication

### Register
`POST /auth/register`

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `email` | string | ✅ | Valid email format |
| `phone` | string | ✅ | E.164 format e.g. `+919876543210` |
| `password` | string | ✅ | Min 8 chars, uppercase + lowercase + digit + special char |
| `firstName` | string | ✅ | 1–50 chars |
| `lastName` | string | ⬜ | Max 50 chars |
| `country` | string | ⬜ | `USA` or `INDIA` |
| `userType` | string | ⬜ | `USER` (default) or `VENDOR` |
| `fcmToken` | string | ⬜ | Firebase device token for push notifications |
| `deviceInfo` | string | ⬜ | Device info string |

**Request:**
```json
{
  "email": "rahul@example.com",
  "phone": "+919876543210",
  "password": "SecurePass@123",
  "firstName": "Rahul",
  "lastName": "Sharma",
  "country": "INDIA",
  "userType": "USER",
  "fcmToken": "firebase-device-token"
}
```

**Response `201`:**
```json
{
  "success": true,
  "message": "Registration successful. Please verify your email.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 604800,
    "user": {
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "email": "rahul@example.com",
      "phone": "+919876543210",
      "userType": "USER",
      "firstName": "Rahul",
      "lastName": "Sharma",
      "fullName": "Rahul Sharma",
      "profilePictureUrl": null,
      "dateOfBirth": null,
      "gender": null,
      "emailVerified": false,
      "phoneVerified": false,
      "twoFactorEnabled": false,
      "preferredLanguage": "en",
      "preferredCurrency": "INR",
      "country": "INDIA",
      "status": "PENDING_VERIFICATION",
      "notificationPreferences": null,
      "lastLoginAt": null,
      "createdAt": "2026-03-06T10:00:00Z"
    }
  }
}
```

---

### Login
`POST /auth/login`

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `identifier` | string | ✅ | Email or phone number |
| `password` | string | ✅ | Account password |
| `fcmToken` | string | ⬜ | Updates device push token |

**Request:**
```json
{
  "identifier": "rahul@example.com",
  "password": "SecurePass@123",
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
      "userId": "550e8400-e29b-41d4-a716-446655440000",
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
      "notificationPreferences": {
        "emailNotifications": {
          "orderUpdates": true,
          "bidUpdates": true,
          "promotional": false,
          "newsletter": false,
          "paymentReminders": true,
          "securityAlerts": true
        },
        "smsNotifications": {
          "orderUpdates": true,
          "bidUpdates": true,
          "promotional": false,
          "otpAlerts": true
        },
        "pushNotifications": {
          "orderUpdates": true,
          "bidUpdates": true,
          "chatMessages": true,
          "promotional": false
        },
        "whatsappNotifications": {
          "orderUpdates": true,
          "bidUpdates": true,
          "promotional": false
        }
      },
      "lastLoginAt": "2026-03-06T10:00:00Z",
      "createdAt": "2026-01-15T10:00:00Z"
    }
  }
}
```
> ⚠️ Account locks after **3 failed login attempts**.

---

### Refresh Token
`POST /auth/refresh`

| Field | Type | Required |
|-------|------|----------|
| `refreshToken` | string | ✅ |

**Request:**
```json
{ "refreshToken": "eyJhbGciOiJIUzI1NiJ9..." }
```

**Response `200`:**
```json
{
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 604800
  }
}
```

---

### Logout
`POST /auth/logout` 🔒

**Request:**
```json
{ "refreshToken": "eyJhbGciOiJIUzI1NiJ9..." }
```
**Response `200`:** `{ "success": true, "message": "Logged out successfully" }`

---

### Logout All Devices
`POST /auth/logout-all` 🔒
**Response `200`:** `{ "success": true, "message": "Logged out from all devices" }`

---

### Send OTP
`POST /auth/otp/send`

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `identifier` | string | ✅ | Email or phone |
| `type` | string | ✅ | `EMAIL`, `PHONE`, `PASSWORD_RESET`, `TWO_FACTOR`, `BUSINESS_EMAIL`, `BUSINESS_PHONE` |
| `channel` | string | ⬜ | `AUTO` (default), `WHATSAPP`, `SMS` |

**Request:**
```json
{
  "identifier": "rahul@example.com",
  "type": "EMAIL",
  "channel": "AUTO"
}
```

**Response `200`:**
```json
{
  "data": {
    "verificationId": "uuid",
    "status": "SENT",
    "expiresIn": 600
  }
}
```

---

### Verify OTP
`POST /auth/otp/verify`

| Field | Type | Required |
|-------|------|----------|
| `identifier` | string | ✅ |
| `otp` | string | ✅ |
| `type` | string | ✅ |

**Request:**
```json
{
  "identifier": "rahul@example.com",
  "otp": "123456",
  "type": "EMAIL"
}
```
**Response `200`:** `{ "data": { "status": "VERIFIED" } }`

---

### Forgot Password
`POST /auth/forgot-password`

| Field | Type | Required |
|-------|------|----------|
| `email` | string | ✅ |

**Request:** `{ "email": "rahul@example.com" }`
**Response `200`:** `{ "success": true, "message": "Password reset OTP sent to your email" }`

---

### Reset Password
`POST /auth/reset-password`

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `email` | string | ✅ | |
| `otp` | string | ✅ | 6-digit code |
| `newPassword` | string | ✅ | Min 8 chars with complexity |
| `confirmPassword` | string | ✅ | Must match newPassword |

**Request:**
```json
{
  "email": "rahul@example.com",
  "otp": "123456",
  "newPassword": "NewPass@123",
  "confirmPassword": "NewPass@123"
}
```
**Response `200`:** `{ "success": true, "message": "Password reset successfully" }`

---

### Change Password
`POST /auth/change-password` 🔒

| Field | Type | Required |
|-------|------|----------|
| `currentPassword` | string | ✅ |
| `newPassword` | string | ✅ |
| `confirmPassword` | string | ✅ |

**Request:**
```json
{
  "currentPassword": "OldPass@123",
  "newPassword": "NewPass@123",
  "confirmPassword": "NewPass@123"
}
```
**Response `200`:** `{ "success": true, "message": "Password changed successfully" }`

---

### Get Current User
`GET /auth/me` 🔒
**Response `200`:** Same `user` object as in login response.

---

## 👤 User Profile

### Get Profile
`GET /users/me` 🔒

**Response `200`:**
```json
{
  "success": true,
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "vendorId": null,
    "email": "rahul@example.com",
    "phone": "+919876543210",
    "userType": "USER",
    "firstName": "Rahul",
    "lastName": "Sharma",
    "fullName": "Rahul Sharma",
    "profilePictureUrl": "http://localhost:8080/uploads/images/profile.jpg",
    "dateOfBirth": "1995-06-15",
    "gender": "MALE",
    "emailVerified": true,
    "phoneVerified": true,
    "twoFactorEnabled": false,
    "preferredLanguage": "en",
    "preferredCurrency": "INR",
    "country": "INDIA",
    "status": "ACTIVE",
    "notificationPreferences": {
      "emailNotifications": {
        "orderUpdates": true,
        "bidUpdates": true,
        "promotional": false,
        "newsletter": false,
        "paymentReminders": true,
        "securityAlerts": true
      },
      "smsNotifications": {
        "orderUpdates": true,
        "bidUpdates": true,
        "promotional": false,
        "otpAlerts": true
      },
      "pushNotifications": {
        "orderUpdates": true,
        "bidUpdates": true,
        "chatMessages": true,
        "promotional": false
      },
      "whatsappNotifications": {
        "orderUpdates": true,
        "bidUpdates": true,
        "promotional": false
      }
    },
    "lastLoginAt": "2026-03-06T10:00:00Z",
    "createdAt": "2026-01-15T10:00:00Z"
  }
}
```

---

### Update Profile
`PUT /users/me` 🔒

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `firstName` | string | ⬜ | 1–50 chars |
| `lastName` | string | ⬜ | Max 50 chars |
| `gender` | string | ⬜ | `MALE`, `FEMALE`, `OTHER`, `PREFER_NOT_TO_SAY` |
| `dateOfBirth` | string | ⬜ | ISO date `YYYY-MM-DD` |
| `preferredLanguage` | string | ⬜ | e.g. `en`, `hi` |
| `preferredCurrency` | string | ⬜ | `INR`, `USD` |
| `country` | string | ⬜ | `INDIA`, `USA` |

**Request:**
```json
{
  "firstName": "Rahul",
  "lastName": "Sharma",
  "gender": "MALE",
  "dateOfBirth": "1995-06-15",
  "preferredLanguage": "en",
  "preferredCurrency": "INR",
  "country": "INDIA"
}
```
**Response `200`:** Updated `UserResponse` object.

---

### Update Profile Picture
`PUT /users/me/profile-picture` 🔒

| Field | Type | Required |
|-------|------|----------|
| `imageUrl` | string | ✅ |

**Request:** `{ "imageUrl": "http://localhost:8080/uploads/images/uuid.jpg" }`
**Response `200`:** Updated `UserResponse` object.

---

### Update Notification Preferences
`PUT /users/me/notification-preferences` 🔒

**Request:**
```json
{
  "emailNotifications": {
    "orderUpdates": true,
    "bidUpdates": true,
    "promotional": false,
    "newsletter": false,
    "paymentReminders": true,
    "securityAlerts": true
  },
  "smsNotifications": {
    "orderUpdates": true,
    "bidUpdates": true,
    "promotional": false,
    "otpAlerts": true
  },
  "pushNotifications": {
    "orderUpdates": true,
    "bidUpdates": true,
    "chatMessages": true,
    "promotional": false
  },
  "whatsappNotifications": {
    "orderUpdates": true,
    "bidUpdates": true,
    "promotional": false
  }
}
```
**Response `200`:** Updated `UserResponse` object.

---

### Update FCM Token
`PUT /users/me/fcm-token` 🔒

| Field | Type | Required |
|-------|------|----------|
| `fcmToken` | string | ✅ |

**Request:** `{ "fcmToken": "firebase-device-token" }`
**Response `200`:** `{ "success": true, "message": "FCM token updated" }`

---

## 📍 Addresses

### Get All My Addresses
`GET /users/me/addresses` 🔒

**Response `200`:**
```json
{
  "success": true,
  "data": [
    {
      "addressId": "uuid",
      "userId": "uuid",
      "label": "Home",
      "fullName": "Rahul Sharma",
      "phone": "+919876543210",
      "streetAddress": "123 MG Road",
      "apartment": "Flat 4B",
      "city": "Hyderabad",
      "state": "Telangana",
      "postalCode": "500001",
      "country": "India",
      "landmark": "Near Tank Bund",
      "addressType": "HOME",
      "latitude": 17.385,
      "longitude": 78.4867,
      "isDefault": true,
      "createdAt": "2026-01-20T10:00:00Z"
    }
  ]
}
```

---

### Add Address
`POST /users/me/addresses` 🔒

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `label` | string | ⬜ | e.g. "Home", "Office" |
| `fullName` | string | ✅ | |
| `phone` | string | ✅ | |
| `streetAddress` | string | ✅ | |
| `apartment` | string | ⬜ | Flat/apartment number |
| `city` | string | ✅ | |
| `state` | string | ✅ | |
| `postalCode` | string | ✅ | |
| `country` | string | ✅ | |
| `landmark` | string | ⬜ | |
| `addressType` | string | ⬜ | `HOME`, `WORK`, `OTHER` |
| `latitude` | double | ⬜ | GPS latitude |
| `longitude` | double | ⬜ | GPS longitude |
| `isDefault` | boolean | ⬜ | Set as default address |

**Request:**
```json
{
  "label": "Home",
  "fullName": "Rahul Sharma",
  "phone": "+919876543210",
  "streetAddress": "123 MG Road",
  "apartment": "Flat 4B",
  "city": "Hyderabad",
  "state": "Telangana",
  "postalCode": "500001",
  "country": "India",
  "landmark": "Near Tank Bund",
  "addressType": "HOME",
  "latitude": 17.385,
  "longitude": 78.4867,
  "isDefault": true
}
```
**Response `201`:** Created address object (same structure as Get All Addresses item).

---

### Update Address
`PUT /users/me/addresses/{addressId}` 🔒
Same body as Add Address, all fields optional.

---

### Delete Address
`DELETE /users/me/addresses/{addressId}` 🔒
**Response `200`:** `{ "success": true, "message": "Address deleted successfully" }`
> ⚠️ Cannot delete your default address.

---

### Set Default Address
`PUT /users/me/addresses/{addressId}/default` 🔒
**Response `200`:** Updated address object.

---

## 🏪 Vendor Discovery

### Get All Active Vendors
`GET /vendors?page=0&size=20&city=Hyderabad&sortBy=createdAt&sortDir=desc`

| Query Param | Type | Required | Description |
|-------------|------|----------|-------------|
| `page` | int | ⬜ | Page number (default 0) |
| `size` | int | ⬜ | Results per page (default 20) |
| `city` | string | ⬜ | Filter by city |
| `cuisine` | string | ⬜ | Filter by cuisine type |
| `sortBy` | string | ⬜ | Field to sort by |
| `sortDir` | string | ⬜ | `asc` or `desc` |

**Response `200`:**
```json
{
  "success": true,
  "data": [
    {
      "vendorId": "uuid",
      "userId": "uuid",
      "registeredEmail": "ravi@royalcatering.com",
      "registeredPhone": "+919876543210",
      "businessName": "Royal Catering Co.",
      "businessEmail": "info@royalcatering.com",
      "businessPhone": "+919876543211",
      "businessEmailVerified": true,
      "businessPhoneVerified": true,
      "businessType": "CATERING",
      "businessRegistrationNumber": "REG123456",
      "taxId": "36AABCR1234F1ZV",
      "logoUrl": "http://localhost:8080/uploads/images/logo.jpg",
      "bannerUrl": "http://localhost:8080/uploads/images/banner.jpg",
      "description": "Premium catering for all occasions",
      "establishedYear": 2015,
      "cuisinesOffered": ["North Indian", "Mughlai"],
      "specialties": ["Biryani", "Dal Makhani"],
      "businessAddress": {
        "streetAddress": "45, Banjara Hills",
        "city": "Hyderabad",
        "state": "Telangana",
        "postalCode": "500034",
        "country": "India"
      },
      "ownerInfo": {
        "firstName": "Ravi",
        "lastName": "Kumar",
        "phone": "+919876543210",
        "email": "ravi@royalcatering.com",
        "idProofType": null,
        "idProofNumber": null
      },
      "serviceAreas": [
        { "city": "Hyderabad", "state": "Telangana", "radiusKm": 50 }
      ],
      "capacity": {
        "minGuests": 50,
        "maxGuests": 1000,
        "concurrentEvents": 3
      },
      "pricing": {
        "currency": "INR",
        "startingPricePerPlate": 250.00,
        "averagePricePerPlate": 450.00
      },
      "ratings": {
        "averageRating": 4.50,
        "totalReviews": 128
      },
      "stats": {
        "totalOrders": 245,
        "completedOrders": 230,
        "ordersCount": 245
      },
      "status": "ACTIVE",
      "approvalStatus": "APPROVED",
      "verified": true,
      "featured": false,
      "country": "INDIA",
      "createdAt": "2026-01-01T10:00:00Z",
      "documents": null
    }
  ],
  "pageInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 45,
    "totalPages": 3,
    "first": true,
    "last": false
  }
}
```

---

### Get Vendor by ID
`GET /vendors/{vendorId}`
**Response `200`:** Same vendor object as above (full detail, including `documents` array).

---

### Search Vendors
`GET /vendors/search?query=biryani&city=Hyderabad&page=0&size=20`

---

### Get Vendor Simplified Menu
`GET /vendors/{vendorId}/menu/simple`

**Response `200`:**
```json
{
  "data": [
    {
      "vendorItemId": "uuid",
      "vendorId": "uuid",
      "customName": "Special Biryani",
      "pricePerPlate": 250.00,
      "currency": "INR"
    }
  ]
}
```

---

## 🍽️ Menu & Categories

### Get All Categories
`GET /menu/categories`

**Response `200`:**
```json
{
  "data": [
    {
      "categoryId": "uuid",
      "categoryName": "Main Course",
      "categoryNameHindi": "मुख्य व्यंजन",
      "description": "Primary dishes",
      "iconUrl": "http://localhost:8080/uploads/images/maincourse.png",
      "displayOrder": 1,
      "status": "ACTIVE"
    }
  ]
}
```

---

### Get All Menu Items
`GET /menu/items?page=0&size=20`

**Response `200`:**
```json
{
  "data": [
    {
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
      "createdAt": "2026-01-01T10:00:00Z",
      "updatedAt": "2026-02-01T10:00:00Z"
    }
  ]
}
```

---

### Get Menu Items by Category
`GET /menu/items/category/{categoryId}?page=0&size=20`

---

### Search Menu Items
`GET /menu/items/search?query=biryani&page=0&size=20`

---

### Get Popular Menu Items
`GET /menu/items/popular`

---

### Get Vendor Menu Items
`GET /menu/vendor/{vendorId}/items?page=0&size=20`

**Response `200`:**
```json
{
  "data": [
    {
      "vendorItemId": "uuid",
      "vendorId": "uuid",
      "masterItemId": "uuid",
      "customName": "Special Hyderabadi Dum Biryani",
      "customDescription": "Slow-cooked aromatic rice with tender meat",
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
  ]
}
```

---

## 🛒 Cart

### Get Cart
`GET /cart` 🔒

**Response `200`:**
```json
{
  "data": [
    {
      "cartItemId": "uuid",
      "userId": "uuid",
      "vendorId": "uuid",
      "vendorItemId": "uuid",
      "itemName": "Chicken Biryani",
      "quantity": 50,
      "pricePerPlate": 280.00,
      "currency": "INR",
      "totalPrice": 14000.00,
      "addedAt": "2026-03-06T10:00:00Z"
    }
  ]
}
```

---

### Add to Cart
`POST /cart` 🔒

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `vendorId` | string | ✅ | |
| `vendorItemId` | string | ✅ | |
| `itemName` | string | ✅ | |
| `quantity` | integer | ✅ | Min 1 |
| `pricePerPlate` | decimal | ✅ | |
| `currency` | string | ⬜ | Default from vendor |

**Request:**
```json
{
  "vendorId": "uuid",
  "vendorItemId": "uuid",
  "itemName": "Chicken Biryani",
  "quantity": 50,
  "pricePerPlate": 280.00,
  "currency": "INR"
}
```
**Response `201`:** Cart item object.

---

### Update Cart Item Quantity
`PUT /cart/{cartItemId}` 🔒

**Request:** `{ "quantity": 75 }`
**Response `200`:** Updated cart item object.

---

### Remove from Cart
`DELETE /cart/{cartItemId}` 🔒
**Response `200`:** `{ "success": true, "message": "Item removed from cart" }`

---

### Clear Cart
`DELETE /cart` 🔒
**Response `200`:** `{ "success": true, "message": "Cart cleared" }`

---

### Batch Add to Cart
`POST /cart/batch` 🔒

**Request:**
```json
{
  "items": [
    { "vendorId": "uuid", "vendorItemId": "uuid", "itemName": "Chicken Biryani", "quantity": 50, "pricePerPlate": 280.00, "currency": "INR" },
    { "vendorId": "uuid", "vendorItemId": "uuid2", "itemName": "Paneer Butter Masala", "quantity": 30, "pricePerPlate": 180.00, "currency": "INR" }
  ]
}
```
**Response `201`:** Array of cart items added.

---

## ❤️ Wishlist

### Get Wishlist
`GET /wishlist` 🔒

**Response `200`:**
```json
{
  "data": [
    {
      "wishlistItemId": "uuid",
      "userId": "uuid",
      "vendorId": "uuid",
      "vendorName": "Royal Catering Co.",
      "notes": "Good for wedding",
      "addedAt": "2026-03-01T10:00:00Z"
    }
  ]
}
```

---

### Add to Wishlist
`POST /wishlist` 🔒

| Field | Type | Required |
|-------|------|----------|
| `vendorId` | string | ✅ |
| `notes` | string | ⬜ |

**Request:** `{ "vendorId": "uuid", "notes": "Good for wedding" }`
**Response `201`:** Wishlist item object.

---

### Remove from Wishlist
`DELETE /wishlist/{vendorId}` 🔒
**Response `200`:** `{ "success": true, "message": "Removed from wishlist" }`

---

## 📋 Bid Requests

### Create Bid Request
`POST /bids/requests` 🔒

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `eventDetails` | object | ✅ | |
| `eventDetails.eventType` | string | ✅ | e.g. `WEDDING`, `BIRTHDAY`, `CORPORATE` |
| `eventDetails.eventName` | string | ⬜ | |
| `eventDetails.eventDate` | datetime | ✅ | ISO-8601, must be future date |
| `eventDetails.eventStartTime` | string | ⬜ | e.g. `18:00` |
| `eventDetails.eventEndTime` | string | ⬜ | |
| `eventDetails.numberOfGuests` | integer | ✅ | Min 10, Max 10000 |
| `eventDetails.venueAddress` | object | ✅ | |
| `eventDetails.venueAddress.streetAddress` | string | ✅ | |
| `eventDetails.venueAddress.city` | string | ✅ | |
| `eventDetails.venueAddress.state` | string | ✅ | |
| `eventDetails.venueAddress.postalCode` | string | ✅ | |
| `eventDetails.venueAddress.country` | string | ⬜ | |
| `eventDetails.venueAddress.latitude` | double | ⬜ | For geospatial matching |
| `eventDetails.venueAddress.longitude` | double | ⬜ | For geospatial matching |
| `menuItems` | array | ⬜ | List of requested items |
| `menuItems[].vendorItemId` | string | ⬜ | Specific vendor item |
| `menuItems[].masterItemId` | string | ⬜ | Platform master item |
| `menuItems[].itemName` | string | ⬜ | |
| `menuItems[].quantity` | integer | ⬜ | Min 1 |
| `menuItems[].customizations` | array | ⬜ | |
| `additionalRequirements` | object | ⬜ | |
| `additionalRequirements.serviceStaffNeeded` | boolean | ⬜ | |
| `additionalRequirements.numberOfStaff` | integer | ⬜ | |
| `additionalRequirements.decorationNeeded` | boolean | ⬜ | |
| `additionalRequirements.liveCounters` | array | ⬜ | List of live counter types |
| `additionalRequirements.specialInstructions` | string | ⬜ | |
| `budget` | object | ⬜ | |
| `budget.currency` | string | ⬜ | |
| `budget.estimatedBudget` | decimal | ⬜ | Must be positive |
| `budget.budgetRange` | string | ⬜ | e.g. `100000-200000` |
| `targetedVendors` | array | ⬜ | List of specific vendor IDs to invite |

**Request:**
```json
{
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
      "country": "India",
      "latitude": 17.4234,
      "longitude": 78.4481
    }
  },
  "menuItems": [
    {
      "vendorItemId": "uuid",
      "masterItemId": "uuid",
      "itemName": "Chicken Biryani",
      "quantity": 300
    }
  ],
  "additionalRequirements": {
    "serviceStaffNeeded": true,
    "numberOfStaff": 10,
    "decorationNeeded": false,
    "liveCounters": ["Biryani Station", "Dessert Counter"],
    "specialInstructions": "Halal food preferred"
  },
  "budget": {
    "currency": "INR",
    "estimatedBudget": 150000.00,
    "budgetRange": "100000-200000"
  },
  "targetedVendors": ["vendor-uuid-1", "vendor-uuid-2"]
}
```
**Response `201`:**
```json
{
  "success": true,
  "data": {
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
      {
        "vendorItemId": "uuid",
        "masterItemId": "uuid",
        "itemName": "Chicken Biryani",
        "quantity": 300
      }
    ],
    "additionalRequirements": {
      "serviceStaffNeeded": true,
      "numberOfStaff": 10,
      "decorationNeeded": false,
      "liveCounters": ["Biryani Station", "Dessert Counter"],
      "specialInstructions": "Halal food preferred"
    },
    "budget": {
      "currency": "INR",
      "estimatedBudget": 150000.00,
      "budgetRange": "100000-200000"
    },
    "targetedVendors": ["vendor-uuid-1", "vendor-uuid-2"],
    "competitivePeriod": {
      "startTime": "2026-03-06T10:00:00Z",
      "endTime": "2026-03-09T10:00:00Z",
      "status": "ACTIVE"
    },
    "acceptedBid": null,
    "status": "ACTIVE",
    "totalBidsReceived": 0,
    "lowestBidAmount": null,
    "createdAt": "2026-03-06T10:00:00Z",
    "expiresAt": "2026-03-09T10:00:00Z"
  }
}
```

---

### Create Bid Request from Cart
`POST /bids/requests/from-cart` 🔒

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `eventDetails` | object | ✅ | Same as above |
| `budget` | object | ⬜ | |

> Creates one bid request per vendor in cart. Cart is cleared after.

---

### Get My Bid Requests
`GET /bids/requests/my?page=0&size=20` 🔒

**Response `200`:** Paginated list of `BidRequestResponse` objects.

---

### Get Bid Request by ID
`GET /bids/requests/{bidRequestId}` 🔒
**Response `200`:** Single `BidRequestResponse` object.

---

### Update Bid Request
`PUT /bids/requests/{bidRequestId}` 🔒

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `eventDetails` | object | ⬜ | Partial update supported |
| `budget` | object | ⬜ | |
| `additionalRequirements` | object | ⬜ | |

> Only allowed while status is `ACTIVE`.

---

### Cancel Bid Request
`DELETE /bids/requests/{bidRequestId}` 🔒
**Response `200`:** `{ "success": true, "message": "Bid request cancelled" }`

---

### Get Bids on My Request
`GET /bids/requests/{bidRequestId}/bids` 🔒

**Response `200`:**
```json
{
  "data": [
    {
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
      "staffProvided": {
        "chefs": 4,
        "servers": 10,
        "cleaners": 2
      },
      "termsAndConditions": "25% advance required at booking.",
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
  ]
}
```

---

### Accept a Bid
`POST /bids/{bidId}/accept` 🔒

**Response `200`:**
```json
{
  "data": {
    "bidRequestId": "uuid",
    "status": "PENDING_TOKEN_PAYMENT",
    "acceptedBid": {
      "bidId": "uuid",
      "vendorId": "uuid",
      "acceptedAt": "2026-03-06T14:00:00Z",
      "coolingPeriodEnd": "2026-03-07T14:00:00Z"
    }
  }
}
```
> After accepting you have **24 hours** to complete token payment.

---

## 📦 Orders

### Get My Orders
`GET /orders/my?page=0&size=20` 🔒

---

### Get Order by ID
`GET /orders/{orderId}` 🔒

**Response `200`:**
```json
{
  "success": true,
  "data": {
    "orderId": "uuid",
    "userId": "uuid",
    "bidRequestId": "uuid",
    "eventDetails": {
      "eventType": "WEDDING",
      "eventName": "Sharma Wedding Reception",
      "eventDate": "2026-04-15",
      "eventTime": "2026-04-15T18:00:00",
      "numberOfGuests": 300,
      "venueAddress": {
        "streetAddress": "Taj Banjara Hotel",
        "city": "Hyderabad",
        "state": "Telangana",
        "postalCode": "500034",
        "country": "India"
      }
    },
    "vendorOrders": [
      {
        "vendorOrderId": "uuid",
        "vendorId": "uuid",
        "vendorUserId": "uuid",
        "vendorName": "Royal Catering Co.",
        "items": [
          {
            "vendorItemId": "uuid",
            "itemName": "Chicken Biryani",
            "quantity": 300,
            "pricePerPlate": 400.00,
            "totalPrice": 120000.00
          }
        ],
        "subtotal": 120000.00,
        "serviceCharge": 6000.00,
        "taxAmount": 22680.00,
        "totalAmount": 148680.00,
        "vendorStatus": "ACCEPTED",
        "deliveryStatus": "PENDING"
      }
    ],
    "pricing": {
      "currency": "INR",
      "subtotal": 120000.00,
      "serviceCharges": 6000.00,
      "taxAmount": 22680.00,
      "platformFee": 2973.60,
      "discountAmount": 0.00,
      "totalAmount": 151653.60
    },
    "paymentDetails": {
      "tokenAmount": 37913.40,
      "tokenPaid": true,
      "tokenPaidAt": "2026-03-06T15:00:00Z",
      "totalPaid": 37913.40,
      "balanceDue": 113740.20,
      "paymentStatus": "TOKEN_PAID"
    },
    "contactInfo": {
      "primaryContactName": "Rahul Sharma",
      "primaryContactPhone": "+919876543210",
      "primaryContactEmail": "rahul@example.com"
    },
    "specialInstructions": "Please ensure halal food preparation",
    "status": "CONFIRMED",
    "cancellation": null,
    "createdAt": "2026-03-06T15:00:00Z",
    "confirmedAt": "2026-03-06T15:00:00Z",
    "deliveredAt": null,
    "completedAt": null
  }
}
```

---

### Get Upcoming Orders
`GET /orders/upcoming?page=0&size=20` 🔒

---

### Cancel Order
`DELETE /orders/{orderId}` 🔒

**Request:**
```json
{ "reason": "Event postponed indefinitely" }
```

**Response `200`:** Updated order with cancellation details:
```json
{
  "data": {
    "orderId": "uuid",
    "status": "CANCELLED",
    "cancellation": {
      "isCancelled": true,
      "cancellationReason": "Event postponed indefinitely",
      "cancelledAt": "2026-03-06T16:00:00Z",
      "refundAmount": 37913.40,
      "refundStatus": "PROCESSING"
    }
  }
}
```

**Cancellation Refund Policy:**
| Days Before Event | Token Refund % |
|-------------------|----------------|
| 30+ days | 100% |
| 15–29 days | 75% |
| 7–14 days | 50% |
| 3–6 days | 25% |
| 0–2 days | 0% |

---

## 💳 Payments

### Initiate Payment
`POST /payments/initiate` 🔒

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `bidId` | string | ⬜ | Required for token payment (before order creation) |
| `orderId` | string | ⬜ | Required for balance payment (after order created) |
| `paymentType` | string | ✅ | `TOKEN`, `BALANCE`, `FULL` |
| `amount` | decimal | ✅ | Payment amount |
| `currency` | string | ⬜ | `INR` or `USD` |
| `country` | string | ⬜ | `INDIA` or `USA` |

**Request (Token payment for bid):**
```json
{
  "bidId": "uuid",
  "paymentType": "TOKEN",
  "amount": 37913.40,
  "currency": "INR",
  "country": "INDIA"
}
```

**Response `200` (Razorpay — India):**
```json
{
  "data": {
    "transactionId": "uuid",
    "gatewayOrderId": "order_ABC123DEF456",
    "gatewayName": "RAZORPAY",
    "amount": 37913.40,
    "currency": "INR",
    "keyId": "rzp_live_xxxxxxxxxx",
    "clientSecret": null,
    "publishableKey": null
  }
}
```

**Response `200` (Stripe — USA):**
```json
{
  "data": {
    "transactionId": "uuid",
    "gatewayOrderId": "pi_3xxxxxxxxxxxxx",
    "gatewayName": "STRIPE",
    "amount": 500.00,
    "currency": "USD",
    "keyId": null,
    "clientSecret": "pi_3xxxxxxxxxxxxx_secret_xxxxxxxxxx",
    "publishableKey": "pk_live_xxxxxxxxxx"
  }
}
```

---

### Verify Payment
`POST /payments/verify` 🔒

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `gatewayOrderId` | string | ✅ | From initiate response |
| `gatewayPaymentId` | string | ✅ | From Razorpay/Stripe callback |
| `signature` | string | ⬜ | HMAC signature (Razorpay only) |

**Request:**
```json
{
  "gatewayOrderId": "order_ABC123DEF456",
  "gatewayPaymentId": "pay_XYZ789GHI012",
  "signature": "hmac_sha256_signature"
}
```

**Response `200`:** Full transaction object:
```json
{
  "data": {
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
}
```

---

### Get My Transactions
`GET /payments/my?page=0&size=20` 🔒
**Response `200`:** Paginated list of transaction objects.

---

### Get Transaction by ID
`GET /payments/{transactionId}` 🔒
**Response `200`:** Single transaction object.

---

### Get Order Transactions
`GET /payments/order/{orderId}` 🔒
**Response `200`:** List of transactions for the order.

---

## ⭐ Reviews

### Create Review
`POST /reviews` 🔒

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `orderId` | string | ✅ | Must be DELIVERED or COMPLETED order |
| `rating` | integer | ✅ | 1–5 |
| `foodQualityRating` | integer | ⬜ | 1–5 |
| `serviceQualityRating` | integer | ⬜ | 1–5 |
| `hygieneRating` | integer | ⬜ | 1–5 |
| `valueForMoneyRating` | integer | ⬜ | 1–5 |
| `punctualityRating` | integer | ⬜ | 1–5 |
| `reviewText` | string | ⬜ | Max 2000 chars |
| `images` | array | ⬜ | List of image URLs |

**Request:**
```json
{
  "orderId": "uuid",
  "rating": 5,
  "foodQualityRating": 5,
  "serviceQualityRating": 4,
  "hygieneRating": 5,
  "valueForMoneyRating": 4,
  "punctualityRating": 5,
  "reviewText": "Excellent food and service! The biryani was outstanding.",
  "images": ["http://localhost:8080/uploads/images/review1.jpg"]
}
```

**Response `201`:**
```json
{
  "data": {
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
    "helpfulCount": 0,
    "reportedCount": 0,
    "status": "PENDING",
    "moderationNotes": null,
    "createdAt": "2026-03-06T10:00:00Z",
    "updatedAt": "2026-03-06T10:00:00Z"
  }
}
```
> One review per order. Can only review `DELIVERED` or `COMPLETED` orders.

---

### Get Review by ID
`GET /reviews/{reviewId}`

---

### Get My Reviews
`GET /reviews/my?page=0&size=10` 🔒

---

### Get Vendor Reviews
`GET /reviews/vendor/{vendorId}?page=0&size=10`

---

### Mark Review as Helpful
`POST /reviews/{reviewId}/helpful`
**Response `200`:** `{ "data": { "helpfulCount": 13 } }`

---

### Report Review
`POST /reviews/{reviewId}/report` 🔒

**Request:** `{ "reason": "Inappropriate content" }`
**Response `200`:** `{ "success": true, "message": "Review reported successfully" }`

---

## 🎁 Loyalty Points

### Get My Balance
`GET /loyalty/balance` 🔒

**Response `200`:**
```json
{
  "data": {
    "userId": "uuid",
    "pointsBalance": 750,
    "lifetimePoints": 1200,
    "tier": "SILVER",
    "earnMultiplier": 1.5,
    "rupeesPerPoint": 0.25,
    "cashValue": 187.50,
    "nextTier": "GOLD",
    "pointsToNextTier": 4250
  }
}
```

---

### Get Loyalty Transaction History
`GET /loyalty/transactions?page=0&size=20` 🔒

**Response `200`:**
```json
{
  "data": [
    {
      "transactionId": "uuid",
      "userId": "uuid",
      "type": "EARN",
      "points": 150,
      "balanceAfter": 750,
      "description": "Points earned from order",
      "orderId": "uuid",
      "createdAt": "2026-03-01T10:00:00Z"
    }
  ]
}
```
**LoyaltyTransaction type enum:** `EARN`, `REDEEM`, `BONUS`, `REFUND`, `EXPIRE`

---

### Redeem Loyalty Points
`POST /loyalty/redeem` 🔒

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `orderId` | string | ✅ | |
| `pointsToRedeem` | integer | ✅ | |
| `orderAmount` | decimal | ✅ | |

**Request:**
```json
{
  "orderId": "uuid",
  "pointsToRedeem": 200,
  "orderAmount": 10000.00
}
```
> Max 50% of order value. 1 point = ₹0.25 (India) / $0.01 (USA).

---

## 🏷️ Promo Codes

### Apply Promo Code
`POST /promos/apply` 🔒

| Field | Type | Required |
|-------|------|----------|
| `code` | string | ✅ |
| `orderTotal` | decimal | ✅ |
| `vendorId` | string | ⬜ |

**Request:**
```json
{
  "code": "WELCOME50",
  "orderTotal": 10000.00,
  "vendorId": "uuid"
}
```

**Response `200`:**
```json
{
  "data": {
    "valid": true,
    "promoCodeId": "uuid",
    "code": "WELCOME50",
    "orderTotal": 10000.00,
    "discountAmount": 500.00,
    "finalAmount": 9500.00,
    "message": "Promo code applied successfully"
  }
}
```

---

## 🔗 Referrals

### Get My Referral Code
`GET /referrals/my-code` 🔒

**Response `200`:**
```json
{
  "data": {
    "referralCode": "REF1A2B3C",
    "userId": "uuid",
    "referralLink": "http://localhost:5173/register?ref=REF1A2B3C",
    "totalReferrals": 5,
    "successfulReferrals": 3,
    "totalRewardsEarned": 1500
  }
}
```

---

### Get Referral Stats
`GET /referrals/stats` 🔒

**Response `200`:**
```json
{
  "data": {
    "referralCode": "REF1A2B3C",
    "totalInvites": 5,
    "signups": 5,
    "completedOrders": 3,
    "pendingRewards": 2,
    "grantedRewards": 3,
    "totalPointsEarned": 1500,
    "recentReferrals": [
      {
        "referredUserId": "uuid",
        "referredUserName": "Priya S.",
        "eventType": "SIGNUP",
        "rewardStatus": "GRANTED",
        "rewardPoints": 500,
        "createdAt": "2026-02-20T10:00:00Z"
      }
    ]
  }
}
```

---

### Validate Referral Code
`GET /referrals/validate/{code}`
**Response `200`:** `{ "data": { "valid": true } }`

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
      "title": "Order Confirmed!",
      "message": "Your order #uuid has been confirmed.",
      "type": "ORDER_CONFIRMED",
      "channel": "IN_APP",
      "isRead": false,
      "data": { "orderId": "uuid" },
      "createdAt": "2026-03-06T15:00:00Z"
    }
  ]
}
```

---

### Get Unread Count
`GET /notifications/unread/count` 🔒
**Response `200`:** `{ "data": { "count": 3 } }`

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

**Response `200`:**
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
        "message": "What is your per-plate rate?",
        "senderId": "user-uuid",
        "timestamp": "2026-03-06T11:00:00Z"
      },
      "unreadCount": { "user-uuid": 2, "vendor-user-uuid": 0 },
      "status": "ACTIVE",
      "createdAt": "2026-03-06T10:00:00Z",
      "updatedAt": "2026-03-06T11:00:00Z"
    }
  ]
}
```

---

### Create / Get Conversation
`POST /chat/conversations` 🔒

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `vendorId` | string | ⬜ | Use for USER_VENDOR type |
| `otherUserId` | string | ⬜ | Use other user's userId |
| `type` | string | ⬜ | `USER_VENDOR`, `USER_SUPPORT` |

**Request:**
```json
{
  "vendorId": "vendor-uuid",
  "type": "USER_VENDOR"
}
```

**Response `200`:** Conversation object (see above).

---

### Get Messages
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
      "message": "Hello, can you cater for 300 guests?",
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

### Send Message (REST)
`POST /chat/conversations/{conversationId}/messages` 🔒

| Field | Type | Required |
|-------|------|----------|
| `message` | string | ✅ |
| `messageType` | string | ⬜ | Default `TEXT` |
| `fileUrl` | string | ⬜ | For IMAGE/FILE type |

**Request:**
```json
{
  "message": "What is your per-plate rate for 300 guests?",
  "messageType": "TEXT"
}
```
**Response `201`:** Message object.

---

### Mark Messages as Read
`PUT /chat/conversations/{conversationId}/read` 🔒
**Response `200`:** `{ "success": true }`

---

### Delete Message
`DELETE /chat/messages/{messageId}` 🔒
**Response `200`:** `{ "success": true, "message": "Message deleted" }`

---

### WebSocket Chat (Real-time)
**Connect:** `ws://localhost:8080/api/v1/ws?token=<accessToken>`
**Subscribe:** `SUBSCRIBE /topic/conversations.{conversationId}`
**Send:** `SEND /app/chat/{conversationId}` — body: `{ "message": "Hello!", "messageType": "TEXT" }`
**Read receipts:** `SUBSCRIBE /topic/conversations.{conversationId}.read`

---

## 📤 File Upload

### Upload Image
`POST /upload/image` 🔒
`Content-Type: multipart/form-data`

| Form Field | Type | Required | Notes |
|-----------|------|----------|-------|
| `file` | file | ✅ | Max 5MB, JPEG/PNG/GIF/WebP |
| `entityType` | string | ⬜ | `PROFILE`, `REVIEW`, `CHAT`, `VENDOR_DOCUMENT` |
| `entityId` | string | ⬜ | Related entity ID |

**Response `200`:**
```json
{
  "data": {
    "fileId": "uuid",
    "fileName": "uuid.jpg",
    "originalName": "my-photo.jpg",
    "fileUrl": "http://localhost:8080/uploads/images/uuid.jpg",
    "fileType": "IMAGE",
    "contentType": "image/jpeg",
    "fileSize": 204800,
    "entityType": "REVIEW",
    "entityId": "uuid",
    "uploadedBy": "uuid",
    "createdAt": "2026-03-06T10:00:00Z"
  }
}
```

---

### Upload Document
`POST /upload/document` 🔒
Max 10MB. Types: PDF, DOC, DOCX, JPEG, PNG.
Same response structure as image upload.

---

## 🎫 Support Tickets

### Create Ticket
`POST /support/tickets` 🔒

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `category` | string | ✅ | e.g. `ORDER`, `PAYMENT`, `VENDOR`, `ACCOUNT`, `OTHER` |
| `subcategory` | string | ⬜ | e.g. `DELIVERY_ISSUE`, `PAYOUT_DELAY` |
| `priority` | string | ⬜ | `LOW`, `MEDIUM`, `HIGH`, `URGENT` |
| `subject` | string | ✅ | 5–200 chars |
| `description` | string | ✅ | 10–2000 chars |
| `orderId` | string | ⬜ | Related order ID |
| `vendorId` | string | ⬜ | Related vendor ID |
| `paymentId` | string | ⬜ | Related transaction ID |
| `attachmentUrls` | array | ⬜ | List of uploaded file URLs |

**Request:**
```json
{
  "category": "ORDER",
  "subcategory": "DELIVERY_ISSUE",
  "subject": "Vendor not responding after payment",
  "description": "I paid the token amount but the vendor hasn't confirmed the order.",
  "priority": "HIGH",
  "orderId": "uuid",
  "vendorId": "uuid",
  "paymentId": "uuid",
  "attachmentUrls": ["http://localhost:8080/uploads/documents/screenshot.jpg"]
}
```

**Response `201`:**
```json
{
  "data": {
    "ticketId": "uuid",
    "ticketNumber": "TKT-000001",
    "createdBy": "user-uuid",
    "createdByName": "Rahul Sharma",
    "category": "ORDER",
    "subcategory": "DELIVERY_ISSUE",
    "priority": "HIGH",
    "subject": "Vendor not responding after payment",
    "description": "I paid the token amount but the vendor hasn't confirmed the order.",
    "relatedEntities": {
      "orderId": "uuid",
      "vendorId": "uuid",
      "paymentId": "uuid"
    },
    "assignedTo": "agent-uuid",
    "assignedAt": "2026-03-06T10:05:00Z",
    "conversationId": "chat-conv-uuid",
    "status": "ASSIGNED",
    "sla": {
      "firstResponseDue": "2026-03-07T06:00:00Z",
      "resolutionDue": "2026-03-08T06:00:00Z",
      "firstResponseAt": null,
      "resolvedAt": null,
      "slaBreached": false
    },
    "resolution": null,
    "customerSatisfaction": null,
    "createdAt": "2026-03-06T10:00:00Z",
    "closedAt": null
  }
}
```

---

### Get My Tickets
`GET /support/tickets/my?page=0&size=20` 🔒

---

### Get Ticket by ID
`GET /support/tickets/{ticketId}` 🔒
**Response `200`:** Full `TicketResponse` object.

---

### Rate Resolved Ticket (CSAT)
`POST /support/tickets/{ticketId}/rate` 🔒

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `rating` | integer | ✅ | 1–5 |
| `feedback` | string | ⬜ | |

**Request:**
```json
{
  "rating": 5,
  "feedback": "Agent was very helpful and resolved the issue quickly."
}
```
**Response `200`:** Updated ticket with `customerSatisfaction` populated.

---

## ⚠️ Standard Error Response

```json
{
  "success": false,
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "Order not found",
    "path": "/api/v1/orders/invalid-id",
    "timestamp": "2026-03-06T10:00:00Z",
    "fieldErrors": {
      "email": "Invalid email format",
      "password": "Password is required"
    }
  }
}
```

**Common Error Codes:**
| Code | HTTP | Description |
|------|------|-------------|
| `UNAUTHORIZED` | 401 | Missing or invalid JWT |
| `FORBIDDEN` | 403 | Insufficient permissions |
| `RESOURCE_NOT_FOUND` | 404 | Entity not found |
| `CONFLICT` | 409 | Duplicate resource |
| `VALIDATION_ERROR` | 400 | Request validation failed |
| `OTP_MISMATCH` | 400 | Wrong OTP entered |
| `OTP_NOT_FOUND` | 400 | No valid OTP found or expired |
| `ACCOUNT_LOCKED` | 401 | 3 failed login attempts |
| `INSUFFICIENT_POINTS` | 400 | Not enough loyalty points |
| `INVALID_SIGNATURE` | 400 | Payment verification failed |
| `BID_INACTIVE` | 400 | Bid is no longer active |
| `MAX_REVISIONS` | 400 | Maximum bid revisions reached |
