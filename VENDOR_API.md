# Vendor Module API Documentation

This document outlines the API endpoints, request payloads, and response structures for the Vendor module.

## Base URL
`/vendors`

## Authentication
Most endpoints require a Bearer Token. Public search endpoints do not require authentication.

---

## 1. Vendor Registration & Profile

### Register as Vendor
Creates a new vendor profile for the authenticated user.

- **Endpoint:** `POST /vendors`
- **Description:** Creates a new vendor profile. Pending approval.
- **Request Body:** `VendorRegistrationRequest`
  ```json
  {
    "businessName": "Tasty Catering",
    "description": "Best catering in town",
    "cuisineTypes": ["Italian", "Mexican"],
    "address": {
       "street": "456 Market St",
       "city": "San Francisco",
       "state": "CA",
       "zipCode": "94103",
       "country": "USA"
    },
    "contactEmail": "contact@tasty.com",
    "contactPhone": "+1987654321"
  }
  ```
- **Response:** `ApiResponse<VendorResponse>`

### Get My Vendor Profile
Retrieves the vendor profile of the currently authenticated user.

- **Endpoint:** `GET /vendors/me`
- **Description:** Returns the vendor profile of the authenticated user.
- **Response:** `ApiResponse<VendorResponse>`

### Update Vendor Profile
Updates the vendor profile details.

- **Endpoint:** `PUT /vendors/{vendorId}`
- **Description:** Updates the vendor profile.
- **Path Parameters:**
  - `vendorId` (String): The ID of the vendor.
- **Request Body:** `VendorUpdateRequest`
  ```json
  {
    "businessName": "Tasty Catering & Events",
    "description": "Updated description...",
    "cuisineTypes": ["Italian", "Mexican", "French"]
  }
  ```
- **Response:** `ApiResponse<VendorResponse>`

---

## 2. Public Vendor Discovery

### Get All Vendors
Retrieves a paginated list of active vendors.

- **Endpoint:** `GET /vendors`
- **Description:** Returns a paginated list of active vendors.
- **Query Parameters:**
  - `page` (int): Page number (default: 0).
  - `size` (int): Page size (default: 20).
  - `status` (String): Filter by status.
  - `city` (String): Filter by city.
  - `cuisine` (String): Filter by cuisine type.
  - `sortBy` (String): Sort field (default: "createdAt").
  - `sortDir` (String): Sort direction ("asc" or "desc", default: "desc").
- **Response:** `ApiResponse<List<VendorResponse>>`

### Get Vendor by ID
Retrieves detailed information about a specific vendor.

- **Endpoint:** `GET /vendors/{vendorId}`
- **Description:** Returns vendor details by vendor ID.
- **Path Parameters:**
  - `vendorId` (String): The ID of the vendor.
- **Response:** `ApiResponse<VendorResponse>`

### Search Vendors
Searches for vendors based on various criteria.

- **Endpoint:** `GET /vendors/search`
- **Description:** Search vendors by name, city, cuisine, etc.
- **Query Parameters:**
  - `query` (String): Search keyword.
  - `city` (String): Filter by city.
  - `cuisines` (List<String>): Filter by cuisine types.
  - `rating` (Double): Minimum rating.
  - `page` (int): Page number.
  - `size` (int): Page size.
- **Response:** `ApiResponse<List<VendorResponse>>`

---

## 3. Admin Operations (Vendor Management)

### Get Pending Vendors
Retrieves a list of vendors waiting for approval.

- **Endpoint:** `GET /vendors/admin/pending`
- **Description:** Returns vendors pending approval (Admin only).
- **Permissions:** `ROLE_ADMIN`
- **Query Parameters:**
  - `page` (int): Page number.
  - `size` (int): Page size.
- **Response:** `ApiResponse<List<VendorResponse>>`

### Approve Vendor
Approves a pending vendor registration.

- **Endpoint:** `POST /vendors/{vendorId}/approve`
- **Description:** Approves a vendor registration (Admin only).
- **Permissions:** `ROLE_ADMIN`
- **Path Parameters:**
  - `vendorId` (String): The ID of the vendor to approve.
- **Response:** `ApiResponse<VendorResponse>`

### Reject Vendor
Rejects a pending vendor registration.

- **Endpoint:** `POST /vendors/{vendorId}/reject`
- **Description:** Rejects a vendor registration (Admin only).
- **Permissions:** `ROLE_ADMIN`
- **Path Parameters:**
  - `vendorId` (String): The ID of the vendor to reject.
- **Query Parameters:**
  - `reason` (String): Reason for rejection.
- **Response:** `ApiResponse<VendorResponse>`

---

## 4. Vendor Data & Status

### Get All Vendors (Data Only)
Retrieves all vendors with their data, useful for data analysis or listing.

- **Endpoint:** `GET /vendors`
- **Description:** Returns a paginated list of vendors. Use query parameters to filter.
- **Query Parameters:**
  - `page` (int): Page number.
  - `size` (int): Page size.
  - `status` (String): Filter by status (e.g., ACTIVE, PENDING, SUSPENDED).
- **Response:** `ApiResponse<List<VendorResponse>>`

### Get Registered Vendors (Completed Profile)
Retrieves vendors who have completed their profile creation and are active.

- **Endpoint:** `GET /vendors?status=ACTIVE`
- **Description:** Returns vendors with status 'ACTIVE', indicating a completed and approved profile.
- **Response:** `ApiResponse<List<VendorResponse>>`

### Get Vendors Pending Approval (Registered but not Active)
Retrieves vendors who have registered but are waiting for admin approval.

- **Endpoint:** `GET /vendors/admin/pending`
- **Description:** Returns vendors with status 'PENDING'.
- **Response:** `ApiResponse<List<VendorResponse>>`

### Vendor Response Payload Structure
The `VendorResponse` object contains detailed vendor information.

```json
{
  "vendorId": "string",
  "userId": "string",
  "businessName": "string",
  "businessEmail": "string",
  "businessPhone": "string",
  "businessType": "string",
  "description": "string",
  "cuisinesOffered": ["string"],
  "status": "string", // ACTIVE, PENDING, REJECTED, SUSPENDED
  "approvalStatus": "string", // PENDING, APPROVED, REJECTED
  "verified": boolean,
  "businessAddress": {
    "streetAddress": "string",
    "city": "string",
    "state": "string",
    "postalCode": "string",
    "country": "string"
  },
  "ownerInfo": {
    "firstName": "string",
    "lastName": "string",
    "email": "string",
    "phone": "string"
  },
  "pricing": {
    "currency": "string",
    "startingPricePerPlate": number,
    "averagePricePerPlate": number
  },
  "ratings": {
    "averageRating": number,
    "totalReviews": number
  }
}
```
