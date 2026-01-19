# Vendor Module - Complete API Documentation

**Version:** 1.0.0  
**Base URL:** `http://localhost:8080/api/v1`  
**Date:** January 19, 2026

---

## Table of Contents

1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Vendor Registration APIs](#vendor-registration-apis)
4. [Vendor Management APIs](#vendor-management-apis)
5. [Admin Vendor Approval APIs](#admin-vendor-approval-apis)
6. [Related APIs](#related-apis)
7. [Error Codes](#error-codes)

---

## Overview

The Vendor module manages all vendor-related operations including:
- Vendor registration and profile management
- Vendor verification and approval workflow
- Vendor search and discovery
- Vendor analytics and dashboard

### Vendor Entity Structure

```json
{
  "vendorId": "unique_vendor_identifier",
  "userId": "user_id_of_vendor_owner",
  "businessName": "Business Name",
  "businessEmail": "business@example.com",
  "businessPhone": "+919876543210",
  "businessType": "CATERING|RESTAURANT|CLOUD_KITCHEN|HOME_CHEF|BAKERY",
  "businessRegistrationNumber": "registration_number",
  "taxId": "tax_id",
  "logoUrl": "https://storage.example.com/logo.jpg",
  "bannerUrl": "https://storage.example.com/banner.jpg",
  "description": "Business description",
  "establishedYear": 2020,
  "cuisinesOffered": ["North Indian", "South Indian", "Chinese"],
  "specialties": ["weddings", "corporate_events"],
  "status": "PENDING_APPROVAL|ACTIVE|SUSPENDED|REJECTED|DELETED",
  "approvalStatus": "PENDING|APPROVED|REJECTED|UNDER_REVIEW",
  "verified": false,
  "featured": false,
  "createdAt": "2026-01-08T12:00:00Z",
  "updatedAt": "2026-01-09T10:30:00Z"
}
```

---

## Authentication

All vendor operations that modify data require authentication. Include the bearer token in the Authorization header:

```
Authorization: Bearer <access_token>
```

To obtain tokens, use the Authentication endpoints.

---

## Vendor Registration APIs

### 1. Register as Vendor

**Endpoint:** `POST /vendors`

**Authentication:** Required (Bearer Token)

**Description:** Allows an authenticated user to register as a vendor. The vendor will be in PENDING_APPROVAL status until admin approval.

**Headers:**
```
Authorization: Bearer {accessToken}
Content-Type: application/json
```

#### Request Body

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

#### Response (201 Created)

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
    "description": "Premium catering services for all occasions",
    "establishedYear": 2018,
    "cuisinesOffered": ["North Indian", "South Indian", "Chinese"],
    "specialties": ["weddings", "corporate_events", "birthday_parties"],
    "status": "PENDING_APPROVAL",
    "approvalStatus": "PENDING",
    "verified": false,
    "featured": false,
    "createdAt": "2026-01-08T12:00:00Z"
  }
}
```

#### Postman Collection

```json
{
  "name": "Register as Vendor",
  "request": {
    "method": "POST",
    "header": [
      {
        "key": "Authorization",
        "value": "Bearer {{accessToken}}",
        "type": "text"
      },
      {
        "key": "Content-Type",
        "value": "application/json",
        "type": "text"
      }
    ],
    "body": {
      "mode": "raw",
      "raw": "{\n  \"businessName\": \"Delicious Catering Co.\",\n  \"businessEmail\": \"contact@deliciouscatering.com\",\n  \"businessPhone\": \"+919876543210\",\n  \"businessType\": \"CATERING\",\n  \"businessRegistrationNumber\": \"REG123456789\",\n  \"taxId\": \"27XXXXX1234Z1Z5\",\n  \"description\": \"Premium catering services for all occasions\",\n  \"establishedYear\": 2018,\n  \"cuisinesOffered\": [\"North Indian\", \"South Indian\", \"Chinese\"],\n  \"specialties\": [\"weddings\", \"corporate_events\", \"birthday_parties\"],\n  \"country\": \"India\",\n  \"businessAddress\": {\n    \"streetAddress\": \"123 Main Street, Block A\",\n    \"city\": \"Mumbai\",\n    \"state\": \"Maharashtra\",\n    \"postalCode\": \"400001\",\n    \"country\": \"India\",\n    \"latitude\": 19.0760,\n    \"longitude\": 72.8777\n  },\n  \"ownerInfo\": {\n    \"firstName\": \"Ramesh\",\n    \"lastName\": \"Kumar\",\n    \"phone\": \"+919876543210\",\n    \"email\": \"ramesh@deliciouscatering.com\",\n    \"idProofType\": \"AADHAR\",\n    \"idProofNumber\": \"123456789012\"\n  },\n  \"serviceAreas\": [\n    {\n      \"city\": \"Mumbai\",\n      \"state\": \"Maharashtra\",\n      \"radiusKm\": 50\n    }\n  ],\n  \"capacity\": {\n    \"minGuests\": 50,\n    \"maxGuests\": 5000,\n    \"concurrentEvents\": 5\n  },\n  \"pricing\": {\n    \"currency\": \"INR\",\n    \"startingPricePerPlate\": 500.00,\n    \"averagePricePerPlate\": 750.00\n  }\n}"
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
```

#### Error Responses

**400 Bad Request - Validation Error:**
```json
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "errors": [
    {
      "field": "businessName",
      "message": "Business name is required"
    },
    {
      "field": "businessEmail",
      "message": "Invalid email format"
    }
  ]
}
```

**401 Unauthorized:**
```json
{
  "success": false,
  "message": "Unauthorized - Authentication required",
  "data": null
}
```

**409 Conflict - Duplicate Email:**
```json
{
  "success": false,
  "message": "Business email already registered",
  "data": null
}
```

---

## Vendor Management APIs

### 2. Get All Vendors

**Endpoint:** `GET /vendors`

**Authentication:** Not Required

**Description:** Retrieves a paginated list of active vendors with filtering and sorting options.

**Query Parameters:**
- `page` (optional, default: 0) - Page number (0-indexed)
- `size` (optional, default: 20) - Number of records per page
- `status` (optional) - Filter by status (PENDING_APPROVAL, ACTIVE, SUSPENDED, REJECTED, DELETED)
- `city` (optional) - Filter by city
- `cuisine` (optional) - Filter by cuisine type
- `sortBy` (optional, default: createdAt) - Sort field
- `sortDir` (optional, default: desc) - Sort direction (asc/desc)

**Full URL:**
```
http://localhost:8080/api/v1/vendors?page=0&size=20&city=Mumbai&cuisine=North%20Indian&sortBy=createdAt&sortDir=desc
```

#### Response (200 OK)

```json
{
  "success": true,
  "message": "Vendors retrieved successfully",
  "data": [
    {
      "vendorId": "vendor_60f5b3e4a1d2c9123a4b9f10",
      "userId": "user_60f5a3d2c9d4f8123a4b9e0f",
      "businessName": "Delicious Catering Co.",
      "businessEmail": "contact@deliciouscatering.com",
      "businessPhone": "+919876543210",
      "businessType": "CATERING",
      "logoUrl": "https://storage.example.com/logo1.jpg",
      "description": "Premium catering services for all occasions",
      "establishedYear": 2018,
      "cuisinesOffered": ["North Indian", "South Indian", "Chinese"],
      "specialties": ["weddings", "corporate_events"],
      "status": "ACTIVE",
      "approvalStatus": "APPROVED",
      "verified": true,
      "featured": true,
      "ratings": {
        "averageRating": 4.5,
        "totalReviews": 150
      },
      "stats": {
        "totalOrders": 500,
        "completedOrders": 495
      },
      "createdAt": "2026-01-08T12:00:00Z"
    },
    {
      "vendorId": "vendor_60f5c4f5b2e3f1234a5c0e11",
      "userId": "user_60f5b4e3d0e5g2345b6d1f20",
      "businessName": "Royal Cuisine Catering",
      "businessEmail": "royal@cuisine.com",
      "businessPhone": "+919876543211",
      "businessType": "CATERING",
      "logoUrl": "https://storage.example.com/logo2.jpg",
      "description": "Authentic royal catering experience",
      "establishedYear": 2015,
      "cuisinesOffered": ["Mughlai", "Continental"],
      "specialties": ["weddings", "business_events"],
      "status": "ACTIVE",
      "approvalStatus": "APPROVED",
      "verified": true,
      "featured": false,
      "ratings": {
        "averageRating": 4.2,
        "totalReviews": 89
      },
      "stats": {
        "totalOrders": 320,
        "completedOrders": 315
      },
      "createdAt": "2025-12-15T10:30:00Z"
    }
  ],
  "pageInfo": {
    "totalElements": 156,
    "totalPages": 8,
    "currentPage": 0,
    "pageSize": 20,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

#### Postman Collection

```json
{
  "name": "Get All Vendors",
  "request": {
    "method": "GET",
    "header": [],
    "url": {
      "raw": "http://localhost:8080/api/v1/vendors?page=0&size=20&city=Mumbai&cuisine=North Indian&sortBy=createdAt&sortDir=desc",
      "protocol": "http",
      "host": ["localhost"],
      "port": "8080",
      "path": ["api", "v1", "vendors"],
      "query": [
        {
          "key": "page",
          "value": "0"
        },
        {
          "key": "size",
          "value": "20"
        },
        {
          "key": "city",
          "value": "Mumbai"
        },
        {
          "key": "cuisine",
          "value": "North Indian"
        },
        {
          "key": "sortBy",
          "value": "createdAt"
        },
        {
          "key": "sortDir",
          "value": "desc"
        }
      ]
    }
  }
}
```

---

### 3. Get Vendor by ID

**Endpoint:** `GET /vendors/{vendorId}`

**Authentication:** Not Required

**Description:** Retrieves detailed information about a specific vendor.

**Path Parameters:**
- `vendorId` (required) - The unique vendor identifier

**Full URL:**
```
http://localhost:8080/api/v1/vendors/vendor_60f5b3e4a1d2c9123a4b9f10
```

#### Response (200 OK)

```json
{
  "success": true,
  "message": "Vendor retrieved successfully",
  "data": {
    "vendorId": "vendor_60f5b3e4a1d2c9123a4b9f10",
    "userId": "user_60f5a3d2c9d4f8123a4b9e0f",
    "businessName": "Delicious Catering Co.",
    "businessEmail": "contact@deliciouscatering.com",
    "businessPhone": "+919876543210",
    "businessType": "CATERING",
    "businessRegistrationNumber": "REG123456789",
    "taxId": "27XXXXX1234Z1Z5",
    "logoUrl": "https://storage.example.com/logo.jpg",
    "bannerUrl": "https://storage.example.com/banner.jpg",
    "description": "Premium catering services for all occasions",
    "establishedYear": 2018,
    "cuisinesOffered": ["North Indian", "South Indian", "Chinese"],
    "specialties": ["weddings", "corporate_events", "birthday_parties"],
    "status": "ACTIVE",
    "approvalStatus": "APPROVED",
    "verified": true,
    "featured": true,
    "businessAddress": {
      "streetAddress": "123 Main Street, Block A",
      "city": "Mumbai",
      "state": "Maharashtra",
      "postalCode": "400001",
      "country": "India"
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
    "createdAt": "2026-01-08T12:00:00Z",
    "updatedAt": "2026-01-15T14:30:00Z"
  }
}
```

#### Postman Collection

```json
{
  "name": "Get Vendor by ID",
  "request": {
    "method": "GET",
    "header": [],
    "url": {
      "raw": "http://localhost:8080/api/v1/vendors/vendor_60f5b3e4a1d2c9123a4b9f10",
      "protocol": "http",
      "host": ["localhost"],
      "port": "8080",
      "path": ["api", "v1", "vendors", "vendor_60f5b3e4a1d2c9123a4b9f10"]
    }
  }
}
```

#### Error Response (404 Not Found)

```json
{
  "success": false,
  "message": "Vendor not found",
  "data": null
}
```

---

### 4. Get My Vendor Profile

**Endpoint:** `GET /vendors/me`

**Authentication:** Required (Bearer Token)

**Description:** Retrieves the vendor profile of the authenticated user.

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Full URL:**
```
http://localhost:8080/api/v1/vendors/me
```

#### Response (200 OK)

```json
{
  "success": true,
  "message": "Vendor profile retrieved successfully",
  "data": {
    "vendorId": "vendor_60f5b3e4a1d2c9123a4b9f10",
    "userId": "user_60f5a3d2c9d4f8123a4b9e0f",
    "businessName": "Delicious Catering Co.",
    "businessEmail": "contact@deliciouscatering.com",
    "businessPhone": "+919876543210",
    "businessType": "CATERING",
    "logoUrl": "https://storage.example.com/logo.jpg",
    "bannerUrl": "https://storage.example.com/banner.jpg",
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
      "postalCode": "400001",
      "country": "India"
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
      "completedOrders": 495
    },
    "createdAt": "2026-01-08T12:00:00Z"
  }
}
```

#### Postman Collection

```json
{
  "name": "Get My Vendor Profile",
  "request": {
    "method": "GET",
    "header": [
      {
        "key": "Authorization",
        "value": "Bearer {{accessToken}}",
        "type": "text"
      }
    ],
    "url": {
      "raw": "http://localhost:8080/api/v1/vendors/me",
      "protocol": "http",
      "host": ["localhost"],
      "port": "8080",
      "path": ["api", "v1", "vendors", "me"]
    }
  }
}
```

---

### 5. Update Vendor Profile

**Endpoint:** `PUT /vendors/{vendorId}`

**Authentication:** Required (Bearer Token)

**Description:** Updates the vendor profile. Only the vendor owner or admin can update a vendor's profile.

**Headers:**
```
Authorization: Bearer {accessToken}
Content-Type: application/json
```

**Path Parameters:**
- `vendorId` (required) - The unique vendor identifier

**Full URL:**
```
http://localhost:8080/api/v1/vendors/vendor_60f5b3e4a1d2c9123a4b9f10
```

#### Request Body

```json
{
  "businessName": "Delicious Catering Co. - Updated",
  "description": "Premium catering services with new specialties",
  "establishedYear": 2018,
  "cuisinesOffered": ["North Indian", "South Indian", "Chinese", "Continental"],
  "specialties": ["weddings", "corporate_events", "birthday_parties", "conferences"],
  "businessAddress": {
    "streetAddress": "456 Market Street, Block B",
    "city": "Mumbai",
    "state": "Maharashtra",
    "postalCode": "400002",
    "country": "India"
  },
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

#### Response (200 OK)

```json
{
  "success": true,
  "message": "Vendor updated successfully",
  "data": {
    "vendorId": "vendor_60f5b3e4a1d2c9123a4b9f10",
    "userId": "user_60f5a3d2c9d4f8123a4b9e0f",
    "businessName": "Delicious Catering Co. - Updated",
    "businessEmail": "contact@deliciouscatering.com",
    "businessPhone": "+919876543210",
    "businessType": "CATERING",
    "description": "Premium catering services with new specialties",
    "establishedYear": 2018,
    "cuisinesOffered": ["North Indian", "South Indian", "Chinese", "Continental"],
    "specialties": ["weddings", "corporate_events", "birthday_parties", "conferences"],
    "status": "ACTIVE",
    "approvalStatus": "APPROVED",
    "verified": true,
    "featured": true,
    "businessAddress": {
      "streetAddress": "456 Market Street, Block B",
      "city": "Mumbai",
      "state": "Maharashtra",
      "postalCode": "400002",
      "country": "India"
    },
    "capacity": {
      "minGuests": 50,
      "maxGuests": 6000,
      "concurrentEvents": 6
    },
    "pricing": {
      "currency": "INR",
      "startingPricePerPlate": 600.00,
      "averagePricePerPlate": 850.00
    },
    "updatedAt": "2026-01-15T15:45:00Z"
  }
}
```

#### Postman Collection

```json
{
  "name": "Update Vendor Profile",
  "request": {
    "method": "PUT",
    "header": [
      {
        "key": "Authorization",
        "value": "Bearer {{accessToken}}",
        "type": "text"
      },
      {
        "key": "Content-Type",
        "value": "application/json",
        "type": "text"
      }
    ],
    "body": {
      "mode": "raw",
      "raw": "{\n  \"businessName\": \"Delicious Catering Co. - Updated\",\n  \"description\": \"Premium catering services with new specialties\",\n  \"establishedYear\": 2018,\n  \"cuisinesOffered\": [\"North Indian\", \"South Indian\", \"Chinese\", \"Continental\"],\n  \"specialties\": [\"weddings\", \"corporate_events\", \"birthday_parties\", \"conferences\"],\n  \"businessAddress\": {\n    \"streetAddress\": \"456 Market Street, Block B\",\n    \"city\": \"Mumbai\",\n    \"state\": \"Maharashtra\",\n    \"postalCode\": \"400002\",\n    \"country\": \"India\"\n  },\n  \"capacity\": {\n    \"minGuests\": 50,\n    \"maxGuests\": 6000,\n    \"concurrentEvents\": 6\n  },\n  \"pricing\": {\n    \"currency\": \"INR\",\n    \"startingPricePerPlate\": 600.00,\n    \"averagePricePerPlate\": 850.00\n  }\n}"
    },
    "url": {
      "raw": "http://localhost:8080/api/v1/vendors/vendor_60f5b3e4a1d2c9123a4b9f10",
      "protocol": "http",
      "host": ["localhost"],
      "port": "8080",
      "path": ["api", "v1", "vendors", "vendor_60f5b3e4a1d2c9123a4b9f10"]
    }
  }
}
```

#### Error Response (403 Forbidden)

```json
{
  "success": false,
  "message": "You don't have permission to update this vendor",
  "data": null
}
```

---

### 6. Search Vendors

**Endpoint:** `GET /vendors/search`

**Authentication:** Not Required

**Description:** Search for vendors based on query, city, cuisines, and rating filters.

**Query Parameters:**
- `query` (optional) - Search query for business name or cuisine
- `city` (optional) - Filter by city
- `cuisines` (optional, repeatable) - Filter by cuisine types (e.g., `&cuisines=North Indian&cuisines=Chinese`)
- `rating` (optional) - Minimum rating filter
- `page` (optional, default: 0) - Page number
- `size` (optional, default: 20) - Records per page

**Full URL:**
```
http://localhost:8080/api/v1/vendors/search?query=catering&city=Mumbai&cuisines=North%20Indian&rating=4.0&page=0&size=20
```

#### Response (200 OK)

```json
{
  "success": true,
  "message": "Search results",
  "data": [
    {
      "vendorId": "vendor_60f5b3e4a1d2c9123a4b9f10",
      "businessName": "Delicious Catering Co.",
      "businessEmail": "contact@deliciouscatering.com",
      "businessPhone": "+919876543210",
      "businessType": "CATERING",
      "logoUrl": "https://storage.example.com/logo1.jpg",
      "description": "Premium catering services for all occasions",
      "cuisinesOffered": ["North Indian", "South Indian", "Chinese"],
      "status": "ACTIVE",
      "verified": true,
      "ratings": {
        "averageRating": 4.5,
        "totalReviews": 150
      },
      "createdAt": "2026-01-08T12:00:00Z"
    },
    {
      "vendorId": "vendor_60f5c4f5b2e3f1234a5c0e11",
      "businessName": "North Indian Flavors Catering",
      "businessEmail": "flavors@catering.com",
      "businessPhone": "+919876543212",
      "businessType": "CATERING",
      "logoUrl": "https://storage.example.com/logo3.jpg",
      "description": "Authentic North Indian cuisine",
      "cuisinesOffered": ["North Indian", "Mughlai"],
      "status": "ACTIVE",
      "verified": true,
      "ratings": {
        "averageRating": 4.3,
        "totalReviews": 98
      },
      "createdAt": "2025-12-20T08:15:00Z"
    }
  ],
  "pageInfo": {
    "totalElements": 45,
    "totalPages": 3,
    "currentPage": 0,
    "pageSize": 20,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

#### Postman Collection

```json
{
  "name": "Search Vendors",
  "request": {
    "method": "GET",
    "header": [],
    "url": {
      "raw": "http://localhost:8080/api/v1/vendors/search?query=catering&city=Mumbai&cuisines=North Indian&rating=4.0&page=0&size=20",
      "protocol": "http",
      "host": ["localhost"],
      "port": "8080",
      "path": ["api", "v1", "vendors", "search"],
      "query": [
        {
          "key": "query",
          "value": "catering"
        },
        {
          "key": "city",
          "value": "Mumbai"
        },
        {
          "key": "cuisines",
          "value": "North Indian"
        },
        {
          "key": "rating",
          "value": "4.0"
        },
        {
          "key": "page",
          "value": "0"
        },
        {
          "key": "size",
          "value": "20"
        }
      ]
    }
  }
}
```

---

## Admin Vendor Approval APIs

### 7. Get Pending Vendors (Admin Only)

**Endpoint:** `GET /vendors/admin/pending`

**Authentication:** Required (Bearer Token - Admin Role)

**Description:** Retrieves a list of vendors pending approval. Admin only endpoint.

**Headers:**
```
Authorization: Bearer {adminAccessToken}
```

**Query Parameters:**
- `page` (optional, default: 0) - Page number
- `size` (optional, default: 20) - Records per page

**Full URL:**
```
http://localhost:8080/api/v1/vendors/admin/pending?page=0&size=20
```

#### Response (200 OK)

```json
{
  "success": true,
  "message": "Pending vendors retrieved",
  "data": [
    {
      "vendorId": "vendor_pending_001",
      "userId": "user_123",
      "businessName": "New Catering Services",
      "businessEmail": "new@catering.com",
      "businessPhone": "+919999999999",
      "businessType": "CATERING",
      "description": "Start-up catering company",
      "establishedYear": 2025,
      "cuisinesOffered": ["North Indian", "Chinese"],
      "status": "PENDING_APPROVAL",
      "approvalStatus": "PENDING",
      "verified": false,
      "featured": false,
      "businessAddress": {
        "streetAddress": "789 New Street",
        "city": "Pune",
        "state": "Maharashtra",
        "postalCode": "411001",
        "country": "India"
      },
      "createdAt": "2026-01-18T10:00:00Z"
    },
    {
      "vendorId": "vendor_pending_002",
      "userId": "user_456",
      "businessName": "Premium Events Catering",
      "businessEmail": "events@premium.com",
      "businessPhone": "+918888888888",
      "businessType": "CATERING",
      "description": "High-end event catering",
      "establishedYear": 2024,
      "cuisinesOffered": ["Continental", "Fusion"],
      "status": "PENDING_APPROVAL",
      "approvalStatus": "PENDING",
      "verified": false,
      "featured": false,
      "businessAddress": {
        "streetAddress": "321 Premium Lane",
        "city": "Bangalore",
        "state": "Karnataka",
        "postalCode": "560001",
        "country": "India"
      },
      "createdAt": "2026-01-19T09:30:00Z"
    }
  ],
  "pageInfo": {
    "totalElements": 12,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 20,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

#### Postman Collection

```json
{
  "name": "Get Pending Vendors (Admin)",
  "request": {
    "method": "GET",
    "header": [
      {
        "key": "Authorization",
        "value": "Bearer {{adminAccessToken}}",
        "type": "text"
      }
    ],
    "url": {
      "raw": "http://localhost:8080/api/v1/vendors/admin/pending?page=0&size=20",
      "protocol": "http",
      "host": ["localhost"],
      "port": "8080",
      "path": ["api", "v1", "vendors", "admin", "pending"],
      "query": [
        {
          "key": "page",
          "value": "0"
        },
        {
          "key": "size",
          "value": "20"
        }
      ]
    }
  }
}
```

#### Error Response (403 Forbidden)

```json
{
  "success": false,
  "message": "Access denied. Admin role required.",
  "data": null
}
```

---

### 8. Approve Vendor (Admin Only)

**Endpoint:** `POST /vendors/{vendorId}/approve`

**Authentication:** Required (Bearer Token - Admin Role)

**Description:** Approves a vendor registration. Changes the vendor status to ACTIVE and approval status to APPROVED.

**Headers:**
```
Authorization: Bearer {adminAccessToken}
Content-Type: application/json
```

**Path Parameters:**
- `vendorId` (required) - The unique vendor identifier

**Full URL:**
```
http://localhost:8080/api/v1/vendors/vendor_pending_001/approve
```

#### Request Body

No request body required.

#### Response (200 OK)

```json
{
  "success": true,
  "message": "Vendor approved successfully",
  "data": {
    "vendorId": "vendor_pending_001",
    "userId": "user_123",
    "businessName": "New Catering Services",
    "businessEmail": "new@catering.com",
    "businessPhone": "+919999999999",
    "businessType": "CATERING",
    "description": "Start-up catering company",
    "establishedYear": 2025,
    "cuisinesOffered": ["North Indian", "Chinese"],
    "status": "ACTIVE",
    "approvalStatus": "APPROVED",
    "verified": true,
    "featured": false,
    "businessAddress": {
      "streetAddress": "789 New Street",
      "city": "Pune",
      "state": "Maharashtra",
      "postalCode": "411001",
      "country": "India"
    },
    "ratings": {
      "averageRating": 0,
      "totalReviews": 0
    },
    "stats": {
      "totalOrders": 0,
      "completedOrders": 0
    },
    "approvalDate": "2026-01-19T14:00:00Z",
    "updatedAt": "2026-01-19T14:00:00Z"
  }
}
```

#### Postman Collection

```json
{
  "name": "Approve Vendor (Admin)",
  "request": {
    "method": "POST",
    "header": [
      {
        "key": "Authorization",
        "value": "Bearer {{adminAccessToken}}",
        "type": "text"
      }
    ],
    "body": {
      "mode": "raw",
      "raw": ""
    },
    "url": {
      "raw": "http://localhost:8080/api/v1/vendors/vendor_pending_001/approve",
      "protocol": "http",
      "host": ["localhost"],
      "port": "8080",
      "path": ["api", "v1", "vendors", "vendor_pending_001", "approve"]
    }
  }
}
```

#### Error Response (404 Not Found)

```json
{
  "success": false,
  "message": "Vendor not found",
  "data": null
}
```

---

### 9. Reject Vendor (Admin Only)

**Endpoint:** `POST /vendors/{vendorId}/reject`

**Authentication:** Required (Bearer Token - Admin Role)

**Description:** Rejects a vendor registration with a reason. Changes the vendor status to REJECTED.

**Headers:**
```
Authorization: Bearer {adminAccessToken}
Content-Type: application/x-www-form-urlencoded
```

**Path Parameters:**
- `vendorId` (required) - The unique vendor identifier

**Query Parameters:**
- `reason` (required) - Reason for rejection

**Full URL:**
```
http://localhost:8080/api/v1/vendors/vendor_pending_002/reject?reason=Incomplete%20documentation%20and%20invalid%20GST%20number
```

#### Response (200 OK)

```json
{
  "success": true,
  "message": "Vendor rejected",
  "data": {
    "vendorId": "vendor_pending_002",
    "userId": "user_456",
    "businessName": "Premium Events Catering",
    "businessEmail": "events@premium.com",
    "businessPhone": "+918888888888",
    "businessType": "CATERING",
    "description": "High-end event catering",
    "status": "REJECTED",
    "approvalStatus": "REJECTED",
    "verified": false,
    "rejectionReason": "Incomplete documentation and invalid GST number",
    "businessAddress": {
      "streetAddress": "321 Premium Lane",
      "city": "Bangalore",
      "state": "Karnataka",
      "postalCode": "560001",
      "country": "India"
    },
    "updatedAt": "2026-01-19T15:30:00Z"
  }
}
```

#### Postman Collection

```json
{
  "name": "Reject Vendor (Admin)",
  "request": {
    "method": "POST",
    "header": [
      {
        "key": "Authorization",
        "value": "Bearer {{adminAccessToken}}",
        "type": "text"
      }
    ],
    "url": {
      "raw": "http://localhost:8080/api/v1/vendors/vendor_pending_002/reject?reason=Incomplete documentation and invalid GST number",
      "protocol": "http",
      "host": ["localhost"],
      "port": "8080",
      "path": ["api", "v1", "vendors", "vendor_pending_002", "reject"],
      "query": [
        {
          "key": "reason",
          "value": "Incomplete documentation and invalid GST number"
        }
      ]
    }
  }
}
```

---

## Related APIs

### Related Vendor Endpoints

These endpoints are related to vendor operations but are documented in other modules:

#### Bid Management (Vendor-specific)
- **POST** `/bids/requests/{bidRequestId}/submit-bid` - Vendor submits a bid for a request
- **GET** `/bids/vendor/submitted?page=0&size=20` - Get bids submitted by vendor
- **GET** `/bids/vendor/received?page=0&size=20` - Get bids received by vendor

#### Menu Management (Vendor-specific)
- **POST** `/menu/vendor-items` - Add menu item for vendor
- **PUT** `/menu/vendor-items/{vendorItemId}` - Update vendor menu item
- **GET** `/menu/vendor-items?vendorId={vendorId}&page=0&size=20` - Get vendor menu items
- **PATCH** `/menu/vendor-items/{vendorItemId}/availability?isAvailable={true|false}` - Update item availability

#### Order Management (Vendor-specific)
- **GET** `/orders/vendor?page=0&size=20` - Get vendor orders

#### Reviews (Vendor-specific)
- **POST** `/reviews/{reviewId}/vendor-response?responseText={text}` - Vendor responds to review
- **GET** `/reviews/vendor/{vendorId}?page=0&size=20` - Get vendor reviews

#### Analytics (Vendor-specific)
- **GET** `/analytics/vendor/dashboard` - Vendor dashboard analytics

#### File Upload
- **POST** `/upload/document` - Upload vendor documents (GST, FSSAI, etc.)
- **POST** `/upload/image` - Upload vendor logo/banner

---

## Error Codes

### Common HTTP Status Codes

| Status Code | Description |
|-------------|-------------|
| 200 | OK - Request successful |
| 201 | Created - Resource created successfully |
| 400 | Bad Request - Invalid input or validation failed |
| 401 | Unauthorized - Authentication required or invalid token |
| 403 | Forbidden - Insufficient permissions or access denied |
| 404 | Not Found - Resource does not exist |
| 409 | Conflict - Resource already exists (duplicate) |
| 500 | Internal Server Error - Server encountered an error |

### Common Error Response Format

```json
{
  "success": false,
  "message": "Error message",
  "data": null,
  "errors": [
    {
      "field": "fieldName",
      "message": "Error details"
    }
  ]
}
```

### Validation Error Fields

Common validation errors for vendor endpoints:

```json
{
  "success": false,
  "message": "Validation failed",
  "data": null,
  "errors": [
    {
      "field": "businessName",
      "message": "Business name is required"
    },
    {
      "field": "businessEmail",
      "message": "Invalid email format"
    },
    {
      "field": "businessPhone",
      "message": "Phone number is required"
    },
    {
      "field": "businessAddress.city",
      "message": "City is required"
    },
    {
      "field": "businessAddress.postalCode",
      "message": "Postal code is required"
    },
    {
      "field": "capacity.minGuests",
      "message": "Minimum guests must be at least 10"
    },
    {
      "field": "capacity.maxGuests",
      "message": "Maximum guests cannot exceed 10000"
    },
    {
      "field": "pricing.startingPricePerPlate",
      "message": "Price must be positive"
    }
  ]
}
```

---

## Integration Examples

### Complete Vendor Onboarding Flow

1. **User Registration** (Authentication Module)
   - Register as customer via `/auth/register`
   
2. **Vendor Registration** (This Module)
   - Call `POST /vendors` with vendor details
   - Vendor gets `PENDING_APPROVAL` status

3. **Admin Approval** (This Module - Admin Only)
   - Admin calls `GET /vendors/admin/pending` to see pending vendors
   - Admin approves vendor via `POST /vendors/{vendorId}/approve`
   - Vendor status changes to `ACTIVE`

4. **Menu Setup** (Menu Module)
   - Vendor adds menu items via `POST /menu/vendor-items`
   - Vendor manages prices and availability

5. **Order Fulfillment** (Order Module)
   - Customers can now place orders with this vendor
   - Vendor manages orders and fulfillment

### Authentication Flow

All protected endpoints require a valid JWT token in the Authorization header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyXzEyMyIsInJvbGUiOiJWRU5ET1IiLCJpYXQiOjE2Mzk1OTI0MDAsImV4cCI6MTYzOTU5NjAwMH0.abc123...
```

Token can be obtained via `/auth/login` endpoint.

---

## Environment Variables

### Required Configuration

```
API_BASE_URL=http://localhost:8080/api/v1
JWT_SECRET=your_secret_key
JWT_EXPIRATION=900000 (15 minutes)
```

### Database Models

Vendor data is stored in MongoDB with the following structure:

```
Collection: vendors
{
  _id: ObjectId
  vendor_id: String (indexed, unique)
  user_id: String (indexed)
  business_name: String
  business_email: String (indexed, unique)
  business_phone: String
  business_type: Enum(CATERING, RESTAURANT, CLOUD_KITCHEN, HOME_CHEF, BAKERY)
  status: Enum(PENDING_APPROVAL, ACTIVE, SUSPENDED, REJECTED, DELETED)
  approval_status: Enum(PENDING, APPROVED, REJECTED, UNDER_REVIEW)
  ...
}
```

---

## Notes

- All timestamps are in ISO 8601 format (UTC)
- Currency codes follow ISO 4217 standard
- Phone numbers should be in E.164 format when possible
- Vendor profiles must be approved by admin before they can accept orders
- Vendor can only be deleted by admin, regular deletion is not allowed
- All API responses follow the standard ApiResponse wrapper format

---

**Last Updated:** January 19, 2026  
**API Version:** v1  
**Documentation Version:** 1.0

