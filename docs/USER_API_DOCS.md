# 👤 USER API DOCUMENTATION
## Bidzaro Catering Platform — Complete User Reference (Real DTO-Based)

**Version:** 1.0.0 | **Base URL:** `http://localhost:8080/api/v1`
**Auth:** `Authorization: Bearer {accessToken}` | **Content-Type:** `application/json`

> All field names, types, and response shapes are taken directly from the actual Java DTO classes.

---

## 🔐 Auth Flow
```
POST /auth/register  →  accessToken + refreshToken
POST /auth/login     →  accessToken + refreshToken
All protected APIs   →  Authorization: Bearer {accessToken}
Token expired?       →  POST /auth/refresh-token  →  new accessToken
```

---

# 1. REGISTER

```http
POST /auth/register
Content-Type: application/json
```

### Request — All Fields
```json
{
  "email": "john.doe@gmail.com",
  "phone": "+917890123456",
  "password": "MyPass@123",
  "firstName": "John",
  "lastName": "Doe",
  "country": "INDIA",
  "userType": "USER",
  "fcmToken": "fcm_device_token_abc123xyz",
  "deviceInfo": "Samsung Galaxy S24 - Android 14"
}
```

| Field | Required | Type | Validation |
|-------|----------|------|-----------|
| `email` | ✅ | String | `@Email` — valid email format |
| `phone` | ✅ | String | Pattern: `^\+?[1-9]\d{9,14}$` |
| `password` | ✅ | String | 8–100 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special `@$!%*?&` |
| `firstName` | ✅ | String | 1–50 chars |
| `lastName` | ❌ | String | max 50 chars |
| `country` | ❌ | String | `INDIA` or `USA` |
| `userType` | ❌ | String | `USER` (default for customers) |
| `fcmToken` | ❌ | String | Firebase device push token |
| `deviceInfo` | ❌ | String | Free text device description |

### Success Response `201 Created`
```json
{
  "success": true,
  "statusCode": 201,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1NTBlODQwMC1lMjliLTQxZDQtYTcxNi00NDY2NTU0NDAwMDAiLCJpYXQiOjE3NDA0MDAwMDAsImV4cCI6MTc0MTAwNDgwMH0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1NTBlODQwMCIsInR5cGUiOiJSRUZSRVNIIiwiaWF0IjoxNzQwNDAwMDAwfQ.refresh_signature_here",
    "tokenType": "Bearer",
    "expiresIn": 604800,
    "user": {
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
      "emailVerified": false,
      "phoneVerified": false,
      "twoFactorEnabled": false,
      "preferredLanguage": null,
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
          "paymentReminders": true,
          "securityAlerts": true
        },
        "pushNotifications": {
          "orderUpdates": true,
          "bidUpdates": true,
          "promotional": false,
          "paymentReminders": true
        },
        "whatsappNotifications": {
          "orderUpdates": false,
          "bidUpdates": false
        }
      },
      "lastLoginAt": null,
      "createdAt": "2026-02-24T10:30:45.123456Z"
    }
  },
  "timestamp": "2026-02-24T10:30:45.123456Z"
}
```

### Error Responses
```json
// 409 — Email already registered
{ "success": false, "statusCode": 409, "error": { "code": "EMAIL_ALREADY_EXISTS", "message": "Email is already registered" } }

// 409 — Phone already registered
{ "success": false, "statusCode": 409, "error": { "code": "PHONE_ALREADY_EXISTS", "message": "Phone is already registered" } }

// 422 — Validation failed
{
  "success": false, "statusCode": 422,
  "error": {
    "code": "VALIDATION_FAILED",
    "fieldErrors": {
      "password": "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character",
      "phone": "Invalid phone number format"
    }
  }
}
```

---

# 2. LOGIN

```http
POST /auth/login
Content-Type: application/json
```

### Request — All Fields
```json
{
  "identifier": "john.doe@gmail.com",
  "password": "MyPass@123",
  "fcmToken": "fcm_device_token_abc123xyz",
  "deviceInfo": "Samsung Galaxy S24 - Android 14"
}
```

| Field | Required | Notes |
|-------|----------|-------|
| `identifier` | ✅ | Email OR phone number (`+917890123456`) |
| `password` | ✅ | Account password |
| `fcmToken` | ❌ | Updates push notification device token |
| `deviceInfo` | ❌ | Device description for session tracking |

### Success Response `200 OK`
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1NTBlODQwMC1lMjliLTQxZDQtYTcxNi00NDY2NTU0NDAwMDAiLCJpYXQiOjE3NDA0MDAwMDAsImV4cCI6MTc0MTAwNDgwMH0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1NTBlODQwMCIsInR5cGUiOiJSRUZSRVNIIn0.refresh_sig",
    "tokenType": "Bearer",
    "expiresIn": 604800,
    "user": {
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
}
```

### Error Responses
```json
{ "success": false, "statusCode": 401, "error": { "code": "INVALID_CREDENTIALS", "message": "Invalid credentials" } }
{ "success": false, "statusCode": 401, "error": { "code": "ACCOUNT_LOCKED", "message": "Account locked due to multiple failed login attempts" } }
{ "success": false, "statusCode": 401, "error": { "code": "ACCOUNT_SUSPENDED", "message": "Your account has been suspended" } }
```

---

# 3. REFRESH TOKEN

```http
POST /auth/refresh-token
Content-Type: application/json
```

### Request
```json
{ "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1NTBlODQwMCIsInR5cGUiOiJSRUZSRVNIIn0.refresh_sig" }
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.(new_access_payload).new_sig",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.(new_refresh_payload).new_refresh_sig",
    "tokenType": "Bearer",
    "expiresIn": 604800
  }
}
```

---

# 4. SEND OTP

```http
POST /auth/send-otp
Content-Type: application/json
```

### Request
```json
{ "identifier": "john.doe@gmail.com", "type": "EMAIL" }
```

| Field | Required | Values |
|-------|----------|--------|
| `identifier` | ✅ | Email or phone |
| `type` | ✅ | `EMAIL` or `PHONE` |

### Success Response `200 OK`
```json
{ "success": true, "data": { "sent": true, "expiresIn": 600, "message": "OTP sent successfully" } }
```

---

# 5. VERIFY OTP

```http
POST /auth/verify-otp
Content-Type: application/json
```

### Request
```json
{ "identifier": "john.doe@gmail.com", "otp": "482910", "type": "EMAIL" }
```

### Success Response `200 OK`
```json
{ "success": true, "data": { "verified": true, "message": "Email verified successfully" } }
```

---

# 6. FORGOT PASSWORD

```http
POST /auth/forgot-password
Content-Type: application/json
```

### Request
```json
{ "email": "john.doe@gmail.com" }
```

### Success Response `200 OK`
```json
{ "success": true, "data": { "message": "Password reset link has been sent to your email" } }
```

---

# 7. RESET PASSWORD

```http
POST /auth/reset-password
Content-Type: application/json
```

### Request
```json
{ "token": "a1b2c3d4e5f6-reset-token-from-email-link", "newPassword": "NewPass@456" }
```

### Success Response `200 OK`
```json
{ "success": true, "data": { "message": "Password reset successfully" } }
```

---

# 8. CHANGE PASSWORD

```http
POST /auth/change-password
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{ "currentPassword": "MyPass@123", "newPassword": "NewPass@456" }
```

### Success Response `200 OK`
```json
{ "success": true, "data": { "message": "Password changed successfully" } }
```

---

# 9. GET MY PROFILE

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

# 10. UPDATE PROFILE

```http
PUT /users/profile
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request — All Fields
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "gender": "MALE",
  "dateOfBirth": "1992-06-15",
  "preferredLanguage": "en",
  "preferredCurrency": "INR",
  "profilePictureUrl": "http://localhost:8080/uploads/images/profile-550e8400.jpg"
}
```

| Field | Required | Values |
|-------|----------|--------|
| `firstName` | ❌ | max 50 chars |
| `lastName` | ❌ | max 50 chars |
| `gender` | ❌ | `MALE`, `FEMALE`, `OTHER`, `PREFER_NOT_TO_SAY` |
| `dateOfBirth` | ❌ | `YYYY-MM-DD` |
| `preferredLanguage` | ❌ | `en`, `hi` |
| `preferredCurrency` | ❌ | `INR`, `USD` |
| `profilePictureUrl` | ❌ | URL returned from file upload API |

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Profile updated successfully",
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

# 11. ADD ADDRESS

```http
POST /users/addresses
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request — All Fields
```json
{
  "addressType": "HOME",
  "label": "Home",
  "fullName": "John Doe",
  "phone": "+917890123456",
  "streetAddress": "42, MG Road, Indiranagar",
  "apartment": "Flat 5B",
  "city": "Bangalore",
  "state": "Karnataka",
  "postalCode": "560038",
  "country": "India",
  "landmark": "Near Indiranagar Metro Station",
  "latitude": 12.9716,
  "longitude": 77.5946,
  "isDefault": true
}
```

| Field | Required | Type | Notes |
|-------|----------|------|-------|
| `streetAddress` | ✅ | String | Full street address |
| `city` | ✅ | String | City name |
| `state` | ✅ | String | State name |
| `postalCode` | ✅ | String | PIN / ZIP code |
| `country` | ✅ | String | Country name |
| `isDefault` | ✅ | Boolean | `true` / `false` |
| `addressType` | ❌ | String | `HOME`, `WORK`, `OTHER` |
| `label` | ❌ | String | e.g. "Home", "Office", "Marriage Hall" |
| `fullName` | ❌ | String | Recipient full name |
| `phone` | ❌ | String | Contact number |
| `apartment` | ❌ | String | Flat/suite/building number |
| `landmark` | ❌ | String | Nearby landmark |
| `latitude` | ❌ | Double | GPS latitude (from maps) |
| `longitude` | ❌ | Double | GPS longitude (from maps) |

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "addressId": "addr-abc123def456ghi789",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "addressType": "HOME",
    "label": "Home",
    "fullName": "John Doe",
    "phone": "+917890123456",
    "streetAddress": "42, MG Road, Indiranagar",
    "apartment": "Flat 5B",
    "city": "Bangalore",
    "state": "Karnataka",
    "postalCode": "560038",
    "country": "India",
    "latitude": 12.9716,
    "longitude": 77.5946,
    "landmark": "Near Indiranagar Metro Station",
    "isDefault": true,
    "createdAt": "2026-02-24T10:30:45.123456Z"
  }
}
```

---

# 12. GET ALL ADDRESSES

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
      "addressId": "addr-abc123def456ghi789",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "addressType": "HOME",
      "label": "Home",
      "fullName": "John Doe",
      "phone": "+917890123456",
      "streetAddress": "42, MG Road, Indiranagar",
      "apartment": "Flat 5B",
      "city": "Bangalore",
      "state": "Karnataka",
      "postalCode": "560038",
      "country": "India",
      "latitude": 12.9716,
      "longitude": 77.5946,
      "landmark": "Near Indiranagar Metro Station",
      "isDefault": true,
      "createdAt": "2026-02-24T10:30:45.123456Z"
    },
    {
      "addressId": "addr-xyz789uvw012",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "addressType": "WORK",
      "label": "Office",
      "fullName": "John Doe",
      "phone": "+917890123456",
      "streetAddress": "10, Whitefield Main Road",
      "apartment": null,
      "city": "Bangalore",
      "state": "Karnataka",
      "postalCode": "560066",
      "country": "India",
      "latitude": null,
      "longitude": null,
      "landmark": null,
      "isDefault": false,
      "createdAt": "2026-02-20T08:00:00.000000Z"
    }
  ]
}
```

---

# 13. DELETE ADDRESS

```http
DELETE /users/addresses/{addressId}
Authorization: Bearer {accessToken}
```

### Success Response `204 No Content`
```
HTTP/1.1 204 No Content
(empty body)
```

---

# 14. BROWSE ALL VENDORS

```http
GET /vendors?page=0&size=20&city=Bangalore&status=ACTIVE
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "vendorId": "vendor-12345-67890",
      "userId": "vendor-user-550e8400",
      "registeredEmail": null,
      "registeredPhone": null,
      "registeredEmailVerified": null,
      "registeredPhoneVerified": null,
      "businessName": "Spice Garden Catering",
      "businessEmail": "info@spicegarden.com",
      "businessPhone": "+917890123456",
      "businessEmailVerified": true,
      "businessPhoneVerified": true,
      "businessType": "CATERING",
      "businessRegistrationNumber": null,
      "taxId": null,
      "logoUrl": "http://localhost:8080/uploads/images/logo-spice.jpg",
      "bannerUrl": "http://localhost:8080/uploads/images/banner-spice.jpg",
      "description": "Authentic South Indian catering since 2010",
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
      "ownerInfo": null,
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
      "documents": null,
      "country": "INDIA"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 298, "totalPages": 15 }
}
```

---

# 15. SEARCH VENDORS

```http
GET /vendors/search?query=spice&city=Bangalore&page=0&size=20
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "vendorId": "vendor-12345-67890",
      "userId": "vendor-user-550e8400",
      "registeredEmail": null,
      "registeredPhone": null,
      "registeredEmailVerified": null,
      "registeredPhoneVerified": null,
      "businessName": "Spice Garden Catering",
      "businessEmail": "info@spicegarden.com",
      "businessPhone": "+917890123456",
      "businessEmailVerified": true,
      "businessPhoneVerified": true,
      "businessType": "CATERING",
      "businessRegistrationNumber": null,
      "taxId": null,
      "logoUrl": "http://localhost:8080/uploads/images/logo-spice.jpg",
      "bannerUrl": null,
      "description": "Authentic South Indian catering since 2010",
      "establishedYear": 2010,
      "cuisinesOffered": ["South Indian", "North Indian"],
      "specialties": ["Weddings"],
      "businessAddress": { "streetAddress": null, "city": "Bangalore", "state": "Karnataka", "postalCode": null, "country": null },
      "ownerInfo": null,
      "serviceAreas": [ { "city": "Bangalore", "state": "Karnataka", "radiusKm": 30 } ],
      "capacity": { "minGuests": 50, "maxGuests": 3000, "concurrentEvents": 4 },
      "pricing": { "currency": "INR", "startingPricePerPlate": 350.00, "averagePricePerPlate": 500.00 },
      "ratings": { "averageRating": 4.7, "totalReviews": 312 },
      "stats": { "totalOrders": 600, "completedOrders": 596 },
      "status": "ACTIVE",
      "approvalStatus": "APPROVED",
      "verified": true,
      "featured": false,
      "createdAt": "2026-01-15T08:00:00.000000Z",
      "documents": null,
      "country": "INDIA"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 3, "totalPages": 1 }
}
```

---

# 16. GET VENDOR DETAILS

```http
GET /vendors/{vendorId}
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
    "description": "Premium authentic South Indian catering since 2010.",
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

# 17. GET MENU CATEGORIES

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
      "description": "Appetizers and starters",
      "displayOrder": 1,
      "iconUrl": "http://localhost:8080/uploads/icons/starters.svg",
      "status": "ACTIVE"
    },
    {
      "categoryId": "cat-002",
      "categoryName": "Main Course",
      "categoryNameHindi": "मुख्य व्यंजन",
      "description": "Main course dishes",
      "displayOrder": 2,
      "iconUrl": "http://localhost:8080/uploads/icons/maincourse.svg",
      "status": "ACTIVE"
    },
    {
      "categoryId": "cat-003",
      "categoryName": "Desserts",
      "categoryNameHindi": "मिठाई",
      "description": "Sweet dishes and desserts",
      "displayOrder": 3,
      "iconUrl": "http://localhost:8080/uploads/icons/desserts.svg",
      "status": "ACTIVE"
    },
    {
      "categoryId": "cat-004",
      "categoryName": "Beverages",
      "categoryNameHindi": "पेय",
      "description": "Drinks and beverages",
      "displayOrder": 4,
      "iconUrl": "http://localhost:8080/uploads/icons/beverages.svg",
      "status": "ACTIVE"
    }
  ]
}
```

---

# 18. GET MENU ITEMS (Master Catalog)

```http
GET /menu/items?page=0&size=20
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
      "description": "Grilled cottage cheese with aromatic spices",
      "categoryId": "cat-001",
      "categoryName": null,
      "cuisineType": "North Indian",
      "foodType": "VEGETARIAN",
      "spiceLevel": "MEDIUM",
      "dietaryTags": ["VEGETARIAN", "GLUTEN_FREE"],
      "allergens": ["DAIRY"],
      "nutritionalInfo": {
        "calories": 320,
        "proteinGrams": 18,
        "carbsGrams": 12,
        "fatGrams": 22,
        "servingSizeGrams": 200
      },
      "imageUrls": [
        "http://localhost:8080/uploads/images/paneer-tikka-1.jpg",
        "http://localhost:8080/uploads/images/paneer-tikka-2.jpg"
      ],
      "isPopular": true,
      "status": "ACTIVE",
      "createdAt": "2026-01-01T00:00:00.000000Z",
      "updatedAt": "2026-01-01T00:00:00.000000Z"
    },
    {
      "masterItemId": "item-002",
      "itemName": "Butter Chicken",
      "itemNameHindi": "बटर चिकन",
      "description": "Tender chicken in rich tomato-butter gravy",
      "categoryId": "cat-002",
      "categoryName": null,
      "cuisineType": "North Indian",
      "foodType": "NON_VEGETARIAN",
      "spiceLevel": "MILD",
      "dietaryTags": [],
      "allergens": ["DAIRY"],
      "nutritionalInfo": {
        "calories": 380,
        "proteinGrams": 28,
        "carbsGrams": 15,
        "fatGrams": 24,
        "servingSizeGrams": 250
      },
      "imageUrls": ["http://localhost:8080/uploads/images/butter-chicken-1.jpg"],
      "isPopular": true,
      "status": "ACTIVE",
      "createdAt": "2026-01-01T00:00:00.000000Z",
      "updatedAt": "2026-01-01T00:00:00.000000Z"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 250, "totalPages": 13 }
}
```

---

# 19. GET VENDOR MENU ITEMS

```http
GET /menu/vendor-items?vendorId=vendor-12345-67890&page=0&size=20
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
        {
          "optionName": "Spice Level",
          "choices": ["Mild", "Medium", "Spicy", "Extra Spicy"],
          "additionalCost": 0.00,
          "isRequired": false
        },
        {
          "optionName": "Serving Size",
          "choices": ["Regular (150g)", "Large (250g)"],
          "additionalCost": 30.00,
          "isRequired": false
        }
      ],
      "stats": { "totalOrders": 1200, "averageRating": 4.80, "totalReviews": 85 },
      "status": "ACTIVE",
      "createdAt": "2026-01-20T10:00:00.000000Z"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 45, "totalPages": 3 }
}
```

---

# 20. GET CART

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
      "cartItemId": "citem-001-aabb",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "vendorId": "vendor-12345-67890",
      "vendorItemId": "vitem-001",
      "masterItemId": "item-001",
      "itemName": "Spice Garden Special Paneer Tikka",
      "quantity": 100,
      "pricePerPlate": 180.00,
      "totalPrice": 18000.00,
      "addedAt": "2026-02-24T09:00:00.000000Z",
      "expiresAt": "2026-03-25T09:00:00.000000Z"
    },
    {
      "cartItemId": "citem-002-ccdd",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "vendorId": "vendor-12345-67890",
      "vendorItemId": "vitem-002",
      "masterItemId": "item-002",
      "itemName": "Butter Chicken",
      "quantity": 100,
      "pricePerPlate": 220.00,
      "totalPrice": 22000.00,
      "addedAt": "2026-02-24T09:00:00.000000Z",
      "expiresAt": "2026-03-25T09:00:00.000000Z"
    }
  ]
}
```

---

# 21. ADD ITEM TO CART

```http
POST /cart/items
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{ "vendorItemId": "vitem-001", "quantity": 100 }
```

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "cartItemId": "citem-001-aabb",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "vendorId": "vendor-12345-67890",
    "vendorItemId": "vitem-001",
    "masterItemId": "item-001",
    "itemName": "Spice Garden Special Paneer Tikka",
    "quantity": 100,
    "pricePerPlate": 180.00,
    "totalPrice": 18000.00,
    "addedAt": "2026-02-24T09:00:00.000000Z",
    "expiresAt": "2026-03-25T09:00:00.000000Z"
  }
}
```

---

# 22. BATCH ADD TO CART

```http
POST /cart/items/batch
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{
  "vendorId": "vendor-12345-67890",
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
      "cartItemId": "citem-001-aabb",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "vendorId": "vendor-12345-67890",
      "vendorItemId": "vitem-001",
      "masterItemId": "item-001",
      "itemName": "Paneer Tikka",
      "quantity": 100,
      "pricePerPlate": 180.00,
      "totalPrice": 18000.00,
      "addedAt": "2026-02-24T09:00:00.000000Z",
      "expiresAt": "2026-03-25T09:00:00.000000Z"
    },
    {
      "cartItemId": "citem-002-ccdd",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "vendorId": "vendor-12345-67890",
      "vendorItemId": "vitem-002",
      "masterItemId": "item-002",
      "itemName": "Butter Chicken",
      "quantity": 100,
      "pricePerPlate": 220.00,
      "totalPrice": 22000.00,
      "addedAt": "2026-02-24T09:00:00.000000Z",
      "expiresAt": "2026-03-25T09:00:00.000000Z"
    }
  ]
}
```

---

# 23. UPDATE CART ITEM

```http
PUT /cart/items/{cartItemId}
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{ "quantity": 150 }
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": { "cartItemId": "citem-001-aabb", "quantity": 150, "pricePerPlate": 180.00, "totalPrice": 27000.00 }
}
```

---

# 24. REMOVE CART ITEM

```http
DELETE /cart/items/{cartItemId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{ "success": true, "message": "Item removed from cart" }
```

---

# 25. CLEAR CART

```http
DELETE /cart
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{ "success": true, "message": "Cart cleared successfully" }
```

---

# 26. CREATE BID REQUEST

```http
POST /bids/requests
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request — All Fields
```json
{
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
    { "masterItemId": "item-001", "itemName": "Paneer Tikka", "quantity": 500 },
    { "masterItemId": "item-002", "itemName": "Butter Chicken", "quantity": 400 },
    { "masterItemId": "item-010", "itemName": "Gulab Jamun", "quantity": 500 }
  ],
  "additionalRequirements": {
    "serviceStaffNeeded": true,
    "numberOfStaff": 25,
    "decorationNeeded": false,
    "liveCounters": ["Dosa Counter", "Chaat Counter"],
    "specialInstructions": "Separate veg and non-veg sections. Food ready by 6:30 PM sharp."
  },
  "budget": {
    "currency": "INR",
    "estimatedBudget": 200000,
    "budgetRange": "200000-250000"
  },
  "targetedVendors": []
}
```

| Field | Required | Notes |
|-------|----------|-------|
| `eventDetails.eventType` | ✅ | `WEDDING`, `CORPORATE`, `BIRTHDAY`, `ANNIVERSARY`, `PRIVATE_PARTY` |
| `eventDetails.eventDate` | ✅ | ISO datetime, must be future |
| `eventDetails.numberOfGuests` | ✅ | Integer > 0 |
| `eventDetails.venueAddress` | ✅ | Full venue address object |
| `menuItems` | ✅ | Min 1 item with masterItemId and quantity |
| `budget.estimatedBudget` | ✅ | Total budget number |
| `budget.currency` | ✅ | `INR` or `USD` |
| `additionalRequirements` | ❌ | Optional event requirements |
| `targetedVendors` | ❌ | List of vendorIds, or `[]` for all |

### Success Response `201 Created`
```json
{
  "success": true,
  "message": "Bid request created successfully",
  "data": {
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
      "specialInstructions": "Separate veg and non-veg sections. Food ready by 6:30 PM sharp."
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
    "totalBidsReceived": 0,
    "lowestBidAmount": null,
    "createdAt": "2026-02-24T10:30:45.123456Z",
    "expiresAt": "2026-03-03T10:30:45.123456Z"
  }
}
```

---

# 27. GET MY BID REQUESTS

```http
GET /bids/requests?page=0&size=20&status=ACTIVE
Authorization: Bearer {accessToken}
```

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
        "venueAddress": { "streetAddress": "Palace Grounds", "city": "Bangalore", "state": "Karnataka", "postalCode": "560080", "country": "India" }
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
        "specialInstructions": "Separate veg and non-veg sections."
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
      "totalBidsReceived": 3,
      "lowestBidAmount": 195000.00,
      "createdAt": "2026-02-24T10:30:45.123456Z",
      "expiresAt": "2026-03-03T10:30:45.123456Z"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 4, "totalPages": 1 }
}
```

---

# 28. VIEW BIDS RECEIVED

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
    },
    {
      "bidId": "bid-royal-002-bb22",
      "bidRequestId": "breq-88990-77665-aabb",
      "vendorId": "vendor-67890-12345",
      "vendorName": "Royal Feast Catering",
      "eventDetails": null,
      "quotedPrice": {
        "currency": "INR",
        "subtotal": 190000.00,
        "serviceCharge": 19000.00,
        "taxPercentage": 5.00,
        "taxAmount": 9500.00,
        "totalAmount": 218500.00
      },
      "itemizedPricing": null,
      "deliveryDetails": null,
      "staffProvided": null,
      "termsAndConditions": null,
      "validityPeriodHours": 72,
      "advancePercentage": 25.00,
      "requiredAdvanceAmount": 54625.00,
      "revisionCount": 1,
      "status": "PENDING",
      "isLowest": false,
      "rank": 2,
      "submittedAt": "2026-02-24T13:30:00.000000Z",
      "expiresAt": "2026-02-27T13:30:00.000000Z"
    }
  ]
}
```

---

# 29. ACCEPT A BID

```http
POST /bids/{bidId}/accept
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Bid accepted successfully",
  "data": {
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
      { "vendorItemId": null, "masterItemId": "item-001", "itemName": "Paneer Tikka", "quantity": 500 }
    ],
    "additionalRequirements": { "serviceStaffNeeded": true, "numberOfStaff": 25, "decorationNeeded": false, "liveCounters": ["Dosa Counter"], "specialInstructions": "Separate sections." },
    "budget": { "currency": "INR", "estimatedBudget": 200000, "budgetRange": "200000-250000" },
    "targetedVendors": [],
    "competitivePeriod": {
      "startTime": "2026-02-24T10:30:45.123456Z",
      "endTime": "2026-02-27T10:30:45.123456Z",
      "status": "CLOSED"
    },
    "acceptedBid": {
      "bidId": "bid-spice-001-aa11",
      "vendorId": "vendor-12345-67890",
      "acceptedAt": "2026-02-24T14:00:00.000000Z",
      "coolingPeriodEnd": "2026-02-25T14:00:00.000000Z"
    },
    "status": "ACCEPTED",
    "totalBidsReceived": 3,
    "lowestBidAmount": 201250.00,
    "createdAt": "2026-02-24T10:30:45.123456Z",
    "expiresAt": "2026-03-03T10:30:45.123456Z"
  }
}
```

---

# 30. CANCEL BID REQUEST

```http
DELETE /bids/requests/{bidRequestId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{ "success": true, "message": "Bid request cancelled successfully" }
```

---

# 31. CREATE ORDER

```http
POST /orders?bidRequestId=breq-88990-77665-aabb
Authorization: Bearer {accessToken}
```

### Success Response `201 Created`
```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
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
          { "vendorItemId": "vitem-002", "itemName": "Butter Chicken", "quantity": 400, "pricePerPlate": 218.75, "totalPrice": 87500.00 },
          { "vendorItemId": "vitem-010", "itemName": "Gulab Jamun", "quantity": 500, "pricePerPlate": 0.00, "totalPrice": 0.00 }
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
}
```

---

# 32. GET ALL MY ORDERS

```http
GET /orders?page=0&size=20&status=CONFIRMED
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
        "venueAddress": { "streetAddress": "Palace Grounds", "city": "Bangalore", "state": "Karnataka", "postalCode": "560080", "country": "India" }
      },
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
      "contactInfo": null,
      "specialInstructions": null,
      "status": "CONFIRMED",
      "cancellation": null,
      "createdAt": "2026-02-25T11:00:00.000000Z",
      "confirmedAt": "2026-02-25T11:00:00.000000Z",
      "deliveredAt": null,
      "completedAt": null
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 5, "totalPages": 1 }
}
```

---

# 33. GET ORDER DETAILS

```http
GET /orders/{orderId}
Authorization: Bearer {accessToken}
```

### Success Response `200 OK` — Full `OrderResponse` Object
```json
{
  "success": true,
  "data": {
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
          { "vendorItemId": "vitem-002", "itemName": "Butter Chicken", "quantity": 400, "pricePerPlate": 218.75, "totalPrice": 87500.00 },
          { "vendorItemId": "vitem-010", "itemName": "Gulab Jamun", "quantity": 500, "pricePerPlate": 0.00, "totalPrice": 0.00 }
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
}
```

---

# 34. CANCEL ORDER

```http
POST /orders/{orderId}/cancel?reason=Event+postponed
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Order cancelled successfully",
  "data": {
    "orderId": "order-54321-12345-ccdd",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "bidRequestId": "breq-88990-77665-aabb",
    "eventDetails": { "eventType": "WEDDING", "eventName": "Priya & Rahul Wedding", "eventDate": "2026-05-20", "eventTime": "18:00", "numberOfGuests": 500, "venueAddress": { "city": "Bangalore", "state": "Karnataka" } },
    "vendorOrders": [
      { "vendorOrderId": "vorder-001-aabb", "vendorId": "vendor-12345-67890", "vendorUserId": null, "vendorName": "Spice Garden Catering", "items": null, "subtotal": 175000.00, "serviceCharge": 17500.00, "taxAmount": 8750.00, "totalAmount": 201250.00, "vendorStatus": "CANCELLED", "deliveryStatus": "PENDING" }
    ],
    "pricing": { "currency": "INR", "subtotal": 175000.00, "serviceCharges": 17500.00, "taxAmount": 8750.00, "platformFee": 4025.00, "discountAmount": 0.00, "totalAmount": 205275.00 },
    "paymentDetails": {
      "tokenAmount": 51318.75,
      "tokenPaid": true,
      "tokenPaidAt": "2026-02-25T11:05:00.000000Z",
      "totalPaid": 51318.75,
      "balanceDue": 0.00,
      "paymentStatus": "REFUND_INITIATED"
    },
    "contactInfo": null,
    "specialInstructions": null,
    "status": "CANCELLED",
    "cancellation": {
      "isCancelled": true,
      "cancellationReason": "Event postponed",
      "cancelledAt": "2026-02-26T10:00:00.000000Z",
      "refundAmount": 51318.75,
      "refundStatus": "INITIATED"
    },
    "createdAt": "2026-02-25T11:00:00.000000Z",
    "confirmedAt": "2026-02-25T11:00:00.000000Z",
    "deliveredAt": null,
    "completedAt": null
  }
}
```

---

# 35. INITIATE PAYMENT

```http
POST /payments/initiate
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request — All Fields
```json
{
  "orderId": "order-54321-12345-ccdd",
  "bidId": "bid-spice-001-aa11",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "transactionId": null,
  "amount": 51318.75,
  "currency": "INR",
  "paymentType": "TOKEN",
  "customerEmail": "john.doe@gmail.com",
  "customerName": "John Doe",
  "country": "INDIA"
}
```

| Field | Required | Notes |
|-------|----------|-------|
| `amount` | ✅ | Payment amount (numeric) |
| `currency` | ✅ | `INR` (Razorpay) or `USD` (Stripe) |
| `paymentType` | ✅ | `TOKEN`, `FULL`, `PARTIAL` |
| `country` | ✅ | Determines which gateway to use |
| `orderId` | ❌ | For full/balance payment |
| `bidId` | ❌ | For token payment before order creation |
| `customerEmail` | ❌ | Used in gateway billing |
| `customerName` | ❌ | Used in gateway billing |

### Success Response `200 OK` — Razorpay (India/INR)
```json
{
  "success": true,
  "data": {
    "transactionId": "txn-99887-66554-eeff",
    "gatewayOrderId": "order_RazpXYZ1234567890AB",
    "gatewayName": "RAZORPAY",
    "amount": 51318.75,
    "currency": "INR",
    "keyId": "rzp_live_xxxxxxxxxxxxxxxx",
    "clientSecret": null,
    "publishableKey": null
  }
}
```

### Success Response `200 OK` — Stripe (USA/USD)
```json
{
  "success": true,
  "data": {
    "transactionId": "txn-99887-66554-eeff",
    "gatewayOrderId": "pi_3OxyzStripeIntentId1A",
    "gatewayName": "STRIPE",
    "amount": 615.83,
    "currency": "USD",
    "keyId": null,
    "clientSecret": "pi_3OxyzStripeIntentId1A_secret_abcdefghijklmnop",
    "publishableKey": "pk_live_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
  }
}
```

---

# 36. VERIFY PAYMENT

```http
POST /payments/verify
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request — All Fields
```json
{
  "gatewayOrderId": "order_RazpXYZ1234567890AB",
  "gatewayPaymentId": "pay_RazpABC9876543210XY",
  "signature": "a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6"
}
```

| Field | Required | Notes |
|-------|----------|-------|
| `gatewayOrderId` | ✅ | From initiate payment response |
| `gatewayPaymentId` | ✅ | From Razorpay/Stripe SDK callback |
| `signature` | ❌ | Razorpay HMAC signature (Stripe not needed) |

### Success Response `200 OK`
```json
{
  "success": true,
  "message": "Payment verified successfully",
  "data": {
    "transactionId": "txn-99887-66554-eeff",
    "orderId": "order-54321-12345-ccdd",
    "status": "SUCCESS",
    "amount": 51318.75,
    "currency": "INR"
  }
}
```

---

# 37. PAYMENT HISTORY

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
      "transactionId": "txn-99887-66554-eeff",
      "orderId": "order-54321-12345-ccdd",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "paymentType": "TOKEN",
      "amount": 51318.75,
      "currency": "INR",
      "status": "SUCCESS",
      "paymentGateway": "RAZORPAY",
      "gatewayOrderId": "order_RazpXYZ1234567890AB",
      "gatewayTransactionId": "pay_RazpABC9876543210XY",
      "processedAt": "2026-02-25T11:05:00.000000Z"
    },
    {
      "transactionId": "txn-88776-55443-gghh",
      "orderId": "order-54321-12345-ccdd",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "paymentType": "FULL",
      "amount": 153956.25,
      "currency": "INR",
      "status": "SUCCESS",
      "paymentGateway": "RAZORPAY",
      "gatewayOrderId": "order_RazpDEF9876543210CD",
      "gatewayTransactionId": "pay_RazpGHI1234567890EF",
      "processedAt": "2026-05-18T09:00:00.000000Z"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 2, "totalPages": 1 }
}
```

---

# 38. CREATE CHAT CONVERSATION

```http
POST /chat/conversations
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{ "otherUserId": "vendor-user-550e8400", "type": "USER_VENDOR" }
```

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "conversationId": "conv-77665-88990-aabb",
    "participants": [
      { "userId": "550e8400-e29b-41d4-a716-446655440000", "userType": "USER", "name": "John Doe" },
      { "userId": "vendor-user-550e8400", "userType": "VENDOR", "name": "Spice Garden Catering" }
    ],
    "otherParticipant": { "userId": "vendor-user-550e8400", "userType": "VENDOR", "name": "Spice Garden Catering" },
    "lastMessage": null,
    "unreadCount": 0,
    "status": "ACTIVE",
    "createdAt": "2026-02-24T10:30:45.123456Z",
    "updatedAt": "2026-02-24T10:30:45.123456Z"
  }
}
```

---

# 39. GET CONVERSATIONS

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
        { "userId": "550e8400-e29b-41d4-a716-446655440000", "userType": "USER", "name": "John Doe" },
        { "userId": "vendor-user-550e8400", "userType": "VENDOR", "name": "Spice Garden Catering" }
      ],
      "otherParticipant": { "userId": "vendor-user-550e8400", "userType": "VENDOR", "name": "Spice Garden Catering" },
      "lastMessage": {
        "message": "Yes, we can handle 500 guests for your wedding!",
        "senderId": "vendor-user-550e8400",
        "senderType": "VENDOR",
        "timestamp": "2026-02-24T11:00:00.000000Z"
      },
      "unreadCount": 2,
      "status": "ACTIVE",
      "createdAt": "2026-02-24T10:30:45.123456Z",
      "updatedAt": "2026-02-24T11:00:00.000000Z"
    }
  ]
}
```

---

# 40. GET CHAT MESSAGES

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
      "message": "Yes! We can handle 500 guests for your wedding. Our starting price is ₹500/plate.",
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

### WebSocket — Send Message
```
URL:       ws://localhost:8080/api/v1/ws
Header:    Authorization: Bearer {accessToken}

SEND to:   /app/chat.sendMessage
{
  "conversationId": "conv-77665-88990-aabb",
  "message": "What dishes do you recommend for 500 guests?",
  "messageType": "TEXT"
}

SUBSCRIBE: /topic/conversations.conv-77665-88990-aabb
// Receive real-time ChatMessageResponse objects
```

---

# 41. SUBMIT REVIEW

```http
POST /reviews
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request — All Fields
```json
{
  "orderId": "order-54321-12345-ccdd",
  "rating": 5,
  "foodQualityRating": 5,
  "serviceQualityRating": 4,
  "hygieneRating": 5,
  "valueForMoneyRating": 4,
  "punctualityRating": 4,
  "reviewText": "Outstanding food quality! Paneer Tikka was excellent. Staff was professional. Slightly late setup but overall amazing.",
  "images": [
    "http://localhost:8080/uploads/images/wedding-food-1.jpg",
    "http://localhost:8080/uploads/images/wedding-food-2.jpg"
  ]
}
```

| Field | Required | Rules |
|-------|----------|-------|
| `orderId` | ✅ | Order must be COMPLETED or DELIVERED |
| `rating` | ✅ | 1 – 5 (overall) |
| `foodQualityRating` | ❌ | 1 – 5 |
| `serviceQualityRating` | ❌ | 1 – 5 |
| `hygieneRating` | ❌ | 1 – 5 |
| `valueForMoneyRating` | ❌ | 1 – 5 |
| `punctualityRating` | ❌ | 1 – 5 |
| `reviewText` | ❌ | max 2000 chars |
| `images` | ❌ | max 5 image URLs from upload API |

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "reviewId": "rev-11223-44556-ccdd",
    "orderId": "order-54321-12345-ccdd",
    "vendorId": "vendor-12345-67890",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "userName": null,
    "rating": 5,
    "foodQualityRating": 5,
    "serviceQualityRating": 4,
    "hygieneRating": 5,
    "valueForMoneyRating": 4,
    "punctualityRating": 4,
    "reviewText": "Outstanding food quality! Paneer Tikka was excellent...",
    "images": [
      "http://localhost:8080/uploads/images/wedding-food-1.jpg",
      "http://localhost:8080/uploads/images/wedding-food-2.jpg"
    ],
    "vendorResponse": null,
    "helpfulCount": 0,
    "status": "APPROVED",
    "createdAt": "2026-05-21T10:00:00.000000Z"
  }
}
```

---

# 42. GET VENDOR REVIEWS

```http
GET /reviews/vendor/{vendorId}?page=0&size=20
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
      "reviewText": "Outstanding food quality! Paneer Tikka was excellent...",
      "images": ["http://localhost:8080/uploads/images/wedding-food-1.jpg"],
      "vendorResponse": {
        "responseText": "Thank you so much John! We are thrilled you enjoyed the food. Hope to serve you again!",
        "respondedAt": "2026-05-22T09:00:00.000000Z"
      },
      "helpfulCount": 8,
      "status": "APPROVED",
      "createdAt": "2026-05-21T10:00:00.000000Z"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 312, "totalPages": 16 }
}
```

---

# 43. CREATE SUPPORT TICKET

```http
POST /support/tickets
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request — All Fields
```json
{
  "category": "ORDER",
  "subcategory": "DELIVERY_ISSUE",
  "priority": "HIGH",
  "subject": "Food arrived 2 hours late for my wedding",
  "description": "The catering team was supposed to arrive by 5 PM but arrived at 7 PM, causing significant inconvenience to 500 guests.",
  "orderId": "order-54321-12345-ccdd",
  "vendorId": "vendor-12345-67890",
  "paymentId": null,
  "attachmentUrls": [
    "http://localhost:8080/uploads/images/complaint-photo-1.jpg"
  ]
}
```

| Field | Required | Validation |
|-------|----------|-----------|
| `category` | ✅ | `ORDER`, `PAYMENT`, `VENDOR`, `ACCOUNT`, `OTHER` |
| `subject` | ✅ | 5 – 200 chars |
| `description` | ✅ | 10 – 2000 chars |
| `priority` | ❌ | `LOW`, `MEDIUM`, `HIGH`, `URGENT` (default: MEDIUM) |
| `subcategory` | ❌ | Free text subcategory |
| `orderId` | ❌ | Related order ID |
| `vendorId` | ❌ | Related vendor ID |
| `paymentId` | ❌ | Related payment/transaction ID |
| `attachmentUrls` | ❌ | List of URLs from file upload |

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
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
    "status": "OPEN",
    "sla": {
      "firstResponseDue": "2026-02-24T11:30:45.123456Z",
      "resolutionDue": "2026-02-25T10:30:45.123456Z",
      "firstResponseAt": null,
      "resolvedAt": null,
      "slaBreached": false
    },
    "resolution": null,
    "customerSatisfaction": null,
    "createdAt": "2026-02-24T10:30:45.123456Z",
    "closedAt": null
  }
}
```

---

# 44. GET MY TICKETS

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
      "ticketId": "tkt-99001-22334-eeff",
      "ticketNumber": "TKT-20260224-001",
      "createdBy": "550e8400-e29b-41d4-a716-446655440000",
      "createdByName": "John Doe",
      "category": "ORDER",
      "subcategory": "DELIVERY_ISSUE",
      "priority": "HIGH",
      "subject": "Food arrived 2 hours late for my wedding",
      "description": "The catering team was supposed to arrive by 5 PM...",
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
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 3, "totalPages": 1 }
}
```

---

# 45. APPLY PROMO CODE

```http
POST /promos/apply
Authorization: Bearer {accessToken}
Content-Type: application/json
```

### Request
```json
{ "code": "BIDZARO2026", "orderTotal": 205275.00 }
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "valid": true,
    "promoCodeId": "promo-bidzaro-2026-xxyy",
    "code": "BIDZARO2026",
    "orderTotal": 205275.00,
    "discountAmount": 10000.00,
    "finalAmount": 195275.00,
    "message": "Promo code applied! You save ₹10,000",
    "errorCode": null
  }
}
```

### Invalid Code `200 OK`
```json
{
  "success": true,
  "data": {
    "valid": false,
    "promoCodeId": null,
    "code": "BADCODE",
    "orderTotal": 205275.00,
    "discountAmount": null,
    "finalAmount": null,
    "message": "Invalid or expired promo code",
    "errorCode": "INVALID_CODE"
  }
}
```

---

# 46. GET ACTIVE PROMOS

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
  ]
}
```

---

# 47. GET LOYALTY BALANCE

```http
GET /loyalty/balance
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "pointsBalance": 1250,
    "lifetimePoints": 3500,
    "tier": "GOLD",
    "pointsToNextTier": 6500,
    "nextTier": "PLATINUM",
    "pointsValue": 312.50,
    "earnMultiplier": 1.5
  }
}
```

> **Tier Thresholds:** BRONZE (0) → SILVER (1000) → GOLD (5000) → PLATINUM (10000 lifetime points)
> **Value:** 4 points = ₹1 discount (pointsValue = pointsBalance × 0.25)

---

# 48. LOYALTY TRANSACTIONS

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
      "transactionId": "ltxn-001-aabb",
      "type": "EARN",
      "points": 500,
      "balanceAfter": 1250,
      "description": "Points earned from order completion",
      "orderId": "order-54321-12345-ccdd",
      "createdAt": "2026-05-21T10:00:00.000000Z"
    },
    {
      "transactionId": "ltxn-002-ccdd",
      "type": "BONUS",
      "points": 200,
      "balanceAfter": 750,
      "description": "Welcome bonus for joining via referral",
      "orderId": null,
      "createdAt": "2026-01-10T08:00:00.000000Z"
    },
    {
      "transactionId": "ltxn-003-eeff",
      "type": "REDEEM",
      "points": -400,
      "balanceAfter": 350,
      "description": "Points redeemed for ₹100 discount on order",
      "orderId": "order-11111-22222-aabb",
      "createdAt": "2026-02-10T14:00:00.000000Z"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 12, "totalPages": 1 }
}
```

---

# 49. GET REFERRAL CODE

```http
GET /referrals/my-code
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "code": "REF550E8400",
    "shareLink": "http://localhost:8080/register?ref=REF550E8400",
    "status": "ACTIVE",
    "totalReferrals": 5,
    "successfulReferrals": 3,
    "pendingReferrals": 2,
    "totalRewardsEarned": 1500
  }
}
```

---

# 50. REFERRAL STATS

```http
GET /referrals/stats
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{
  "success": true,
  "data": {
    "referralCode": "REF550E8400",
    "totalInvites": 5,
    "signups": 5,
    "completedOrders": 3,
    "pendingRewards": 200,
    "grantedRewards": 1500,
    "totalPointsEarned": 1500,
    "recentReferrals": [
      { "referredUserName": "Rahul Sharma", "eventType": "FIRST_ORDER", "rewardStatus": "GRANTED", "rewardPoints": 500, "createdAt": "2026-01-20T10:00:00.000000Z" },
      { "referredUserName": "Meera Iyer",   "eventType": "FIRST_ORDER", "rewardStatus": "GRANTED", "rewardPoints": 500, "createdAt": "2026-02-01T10:00:00.000000Z" },
      { "referredUserName": "Suresh Patel", "eventType": "SIGNUP",      "rewardStatus": "PENDING",  "rewardPoints": 0,   "createdAt": "2026-02-20T10:00:00.000000Z" }
    ]
  }
}
```

---

# 51. GET NOTIFICATIONS

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
      "notificationId": "notif-001-aabb",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "userType": "USER",
      "notificationType": "BID_RECEIVED",
      "channel": "IN_APP",
      "title": "New Bid Received",
      "message": "Spice Garden Catering submitted a quote of ₹2,01,250 for your wedding request.",
      "data": { "referenceId": "bid-spice-001-aa11", "referenceType": "BID" },
      "priority": "NORMAL",
      "status": "DELIVERED",
      "sentAt": "2026-02-24T12:00:00.000000Z",
      "deliveredAt": "2026-02-24T12:00:05.000000Z",
      "readAt": null,
      "failedReason": null,
      "retryCount": 0,
      "maxRetries": 3,
      "createdAt": "2026-02-24T12:00:00.000000Z"
    },
    {
      "notificationId": "notif-002-ccdd",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "userType": "USER",
      "notificationType": "ORDER_CONFIRMED",
      "channel": "IN_APP",
      "title": "Order Confirmed",
      "message": "Your order for Priya & Rahul Wedding has been confirmed. Token payment received.",
      "data": { "referenceId": "order-54321-12345-ccdd", "referenceType": "ORDER" },
      "priority": "HIGH",
      "status": "DELIVERED",
      "sentAt": "2026-02-25T11:05:00.000000Z",
      "deliveredAt": "2026-02-25T11:05:02.000000Z",
      "readAt": "2026-02-25T11:10:00.000000Z",
      "failedReason": null,
      "retryCount": 0,
      "maxRetries": 3,
      "createdAt": "2026-02-25T11:05:00.000000Z"
    }
  ],
  "pageInfo": { "pageNumber": 0, "pageSize": 20, "totalElements": 15, "totalPages": 1 }
}
```

---

# 52. MARK NOTIFICATION READ

```http
PATCH /notifications/{notificationId}/read
Authorization: Bearer {accessToken}
```

### Success Response `200 OK`
```json
{ "success": true, "message": "Notification marked as read" }
```

---

# 53. FILE UPLOAD (Images & Documents)

```http
POST /uploads/image      — for images (jpg, png, webp) max 5MB
POST /uploads/document   — for documents (pdf, doc) max 10MB
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data
```

### Form Fields
```
file:        <binary file data>
entityType:  PROFILE_PICTURE | MENU_ITEM | VENDOR_LOGO | VENDOR_DOCUMENT | CHAT_ATTACHMENT | REVIEW_IMAGE
entityId:    550e8400-e29b-41d4-a716-446655440000  (your userId or relevant ID)
```

### Success Response `201 Created`
```json
{
  "success": true,
  "data": {
    "fileId": "file-img-001-aabb",
    "fileName": "profile-1708773045123.jpg",
    "originalName": "my-photo.jpg",
    "fileType": "IMAGE",
    "contentType": "image/jpeg",
    "fileSize": 145678,
    "fileUrl": "http://localhost:8080/uploads/images/profile-1708773045123.jpg",
    "createdAt": "2026-02-24T10:30:45.123456Z"
  }
}
```

> Use the returned `fileUrl` in update profile, review images, chat attachments etc.

---

# 📌 Error Code Reference

| HTTP | Error Code | Description |
|------|-----------|-------------|
| 400 | `BAD_REQUEST` | Invalid request data |
| 400 | `CART_EMPTY` | Cart is empty |
| 400 | `INVALID_QUANTITY` | Quantity must be ≥ 1 |
| 400 | `ORDER_NOT_COMPLETED` | Cannot review — order not complete |
| 400 | `REVIEW_EXISTS` | Already reviewed this order |
| 400 | `PAYMENT_FAILED` | Payment gateway returned failure |
| 400 | `INVALID_SIGNATURE` | Razorpay signature mismatch |
| 401 | `UNAUTHORIZED` | Token missing or expired |
| 401 | `INVALID_CREDENTIALS` | Wrong login credentials |
| 401 | `ACCOUNT_LOCKED` | 5 failed login attempts |
| 401 | `ACCOUNT_SUSPENDED` | Account suspended by admin |
| 403 | `FORBIDDEN` | Not authorized for this resource |
| 404 | `USER_NOT_FOUND` | User does not exist |
| 404 | `ORDER_NOT_FOUND` | Order does not exist |
| 404 | `BID_NOT_FOUND` | Bid does not exist |
| 404 | `VENDOR_NOT_FOUND` | Vendor does not exist |
| 409 | `EMAIL_ALREADY_EXISTS` | Email already registered |
| 409 | `PHONE_ALREADY_EXISTS` | Phone already registered |
| 422 | `VALIDATION_FAILED` | Field-level validation errors with `fieldErrors` map |
| 429 | `RATE_LIMIT_EXCEEDED` | Too many requests |

---

*USER_API_DOCS.md — Based on actual Java DTOs (RegisterRequest, AuthResponse, UserResponse, AddressResponse, VendorResponse, MenuItemResponse, VendorMenuItemResponse, CategoryResponse, BidRequestResponse, VendorBidResponse, OrderResponse, PaymentInitiationRequest/Response, PaymentVerificationRequest, ConversationResponse, ChatMessageResponse, ReviewResponse, TicketResponse, PromoCodeResponse, ApplyPromoResponse, LoyaltyBalanceResponse, LoyaltyTransactionResponse, ReferralCodeResponse, ReferralStatsResponse, FileUploadResponse, Notification model)*
*Bidzaro Catering Platform v1.0.0 | Generated: February 24, 2026*

