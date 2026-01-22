# User Module API Documentation

This document outlines the API endpoints, request payloads, and response structures for the User module.

## Base URL
`/users`

## Authentication
All endpoints require a Bearer Token in the Authorization header unless specified otherwise.

---

## 1. Profile Management

### Get Profile
Retrieves the current user's profile information.

- **Endpoint:** `GET /users/profile`
- **Description:** Returns current user's profile.
- **Response:** `ApiResponse<UserResponse>`

### Update Profile
Updates the user's profile information.

- **Endpoint:** `PUT /users/profile`
- **Description:** Updates user profile.
- **Request Body:** `UpdateProfileRequest`
  ```json
  {
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+1234567890"
  }
  ```
- **Response:** `ApiResponse<UserResponse>`

### Update Profile Picture
Updates the user's profile picture URL.

- **Endpoint:** `PATCH /users/profile-picture`
- **Description:** Updates user's profile picture.
- **Query Parameters:**
  - `imageUrl` (String): The URL of the new profile picture.
- **Response:** `ApiResponse<UserResponse>`

### Get Notification Preferences
Retrieves the user's notification settings.

- **Endpoint:** `GET /users/notification-preferences`
- **Description:** Returns user's notification preferences.
- **Response:** `ApiResponse<NotificationPreferences>`

### Update Notification Preferences
Updates the user's notification settings.

- **Endpoint:** `PUT /users/notification-preferences`
- **Description:** Updates notification preferences.
- **Request Body:** `NotificationPreferences`
  ```json
  {
    "emailNotifications": true,
    "pushNotifications": false,
    "smsNotifications": true
  }
  ```
- **Response:** `ApiResponse<NotificationPreferences>`

---

## 2. Address Management

### Get All Addresses
Retrieves a list of all addresses associated with the user.

- **Endpoint:** `GET /users/addresses`
- **Description:** Returns all addresses for current user.
- **Response:** `ApiResponse<List<AddressResponse>>`

### Get Address by ID
Retrieves a specific address by its ID.

- **Endpoint:** `GET /users/addresses/{addressId}`
- **Description:** Returns address by ID.
- **Path Parameters:**
  - `addressId` (String): The ID of the address.
- **Response:** `ApiResponse<AddressResponse>`

### Add Address
Adds a new address to the user's profile.

- **Endpoint:** `POST /users/addresses`
- **Description:** Adds a new address.
- **Request Body:** `AddressRequest`
  ```json
  {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA",
    "type": "HOME",
    "isDefault": true
  }
  ```
- **Response:** `ApiResponse<AddressResponse>`

### Update Address
Updates an existing address.

- **Endpoint:** `PUT /users/addresses/{addressId}`
- **Description:** Updates an address.
- **Path Parameters:**
  - `addressId` (String): The ID of the address to update.
- **Request Body:** `AddressRequest`
- **Response:** `ApiResponse<AddressResponse>`

### Delete Address
Deletes an address from the user's profile.

- **Endpoint:** `DELETE /users/addresses/{addressId}`
- **Description:** Deletes an address.
- **Path Parameters:**
  - `addressId` (String): The ID of the address to delete.
- **Response:** `ApiResponse<Void>`

### Set Default Address
Sets a specific address as the default address.

- **Endpoint:** `PATCH /users/addresses/{addressId}/default`
- **Description:** Sets an address as default.
- **Path Parameters:**
  - `addressId` (String): The ID of the address to set as default.
- **Response:** `ApiResponse<AddressResponse>`
