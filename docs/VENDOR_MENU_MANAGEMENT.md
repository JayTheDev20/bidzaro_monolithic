# 🍽️ VENDOR MENU MANAGEMENT API Documentation

**Bidzaro Catering Platform** | Base URL: `http://localhost:8080/api/v1/menu`

> 🔒 = Requires `Authorization: Bearer <accessToken>` with `userType: VENDOR`
> ✅ = Required field | ⬜ = Optional field

---

## 📋 Table of Contents

1. [Overview](#-overview)
2. [Enums Reference](#-enums-reference)
3. [DTOs & Request/Response Models](#-dtos--requestresponse-models)
4. [API Endpoints](#-api-endpoints)
5. [Error Handling](#-error-handling)

---

## 🎯 Overview

Vendors can manage their own custom menu items by:
- Browsing available master menu items from the platform
- Adding items to their custom menu with vendor-specific pricing
- Customizing item names and descriptions
- Managing pricing with discounts
- Controlling item availability
- Updating specifications and details

All items added to vendor menu are based on master menu items created by admins. Vendors can customize names, descriptions, and pricing while inheriting the base item specifications.

---

## 🔢 Enums Reference

### VendorItemStatus
Represents the status of a vendor's menu item

| Value | Description |
|-------|-------------|
| `ACTIVE` | Item is active and available for bids |
| `INACTIVE` | Item is inactive (soft deleted) |

### ItemAvailability Status
Represents whether the item is currently available for orders

| Value | Description |
|-------|-------------|
| `AVAILABLE` | Item is available for customers |
| `TEMPORARILY_UNAVAILABLE` | Item is temporarily out of stock |

---

## 📦 DTOs & Request/Response Models

### VendorMenuItemRequest
**Used for**: Adding new items to vendor menu or updating existing items

```json
{
  "masterItemId": "string",        // ✅ REQUIRED - ID of the master menu item
  "customName": "string",          // ⬜ OPTIONAL - Custom name for the item (defaults to master item name)
  "customDescription": "string",   // ⬜ OPTIONAL - Custom description for the item
  "pricePerPlate": 450.00,        // ✅ REQUIRED - Price per plate in vendor's currency
  "minimumOrderQuantity": 10,      // ⬜ OPTIONAL - Minimum plates to order (default: 1)
  "discountPercentage": 10.5       // ⬜ OPTIONAL - Discount percentage (0-100)
}
```

### VendorMenuItemResponse
**Returned by**: GET, POST, PUT endpoints

```json
{
  "vendorItemId": "abc-123-uuid",
  "vendorId": "vendor-uuid",
  "masterItemId": "master-item-uuid",
  "masterItemName": "Biryani",
  "masterItemDescription": "Fragrant rice with spices",
  "masterItemCategory": "Rice Dishes",
  "masterItemImage": "https://cdn.example.com/biryani.jpg",

  "customName": "Special House Biryani",           // Vendor's custom name
  "customDescription": "Our signature biryani",   // Vendor's custom description

  "pricing": {
    "currency": "USD",
    "pricePerPlate": 450.00,                       // Vendor's set price
    "minimumOrderQuantity": 10,
    "discountPercentage": 10.5,
    "discountedPrice": 402.25                      // Auto-calculated: pricePerPlate - discount
  },

  "availability": {
    "isAvailable": true,
    "lastUpdated": "2026-03-06T15:00:00Z",
    "unavailableReason": null                      // Only set when unavailable
  },

  "specifications": {
    "preparationTime": "45 minutes",
    "servingSize": "1 plate",
    "ingredients": ["rice", "mutton", "spices"],
    "allergens": ["nuts"]
  },

  "status": "ACTIVE",                              // ACTIVE or INACTIVE
  "createdAt": "2026-03-06T10:00:00Z",
  "updatedAt": "2026-03-06T15:00:00Z"
}
```

### PaginatedVendorMenuItemResponse
**Returned by**: GET vendor-items list endpoint

```json
{
  "success": true,
  "status": 200,
  "message": "Vendor menu items retrieved",
  "data": [
    // ... array of VendorMenuItemResponse objects
  ],
  "pageInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 50,
    "totalPages": 3,
    "hasMore": true
  }
}
```

---

## 🔌 API Endpoints

### 1️⃣ Get Available Master Menu Items (Browse)

**Endpoint**: `GET /menu/items?page=0&size=20`

**Description**: Browse all available master menu items from the platform that can be added to vendor menu

**Authentication**: 🔒 Required (VENDOR role)

**Headers**:
```
Authorization: Bearer <accessToken>
Content-Type: application/json
```

**Query Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | 0 | Page number (0-indexed) |
| `size` | int | 20 | Items per page |

**Response**: `200 OK`
```json
{
  "success": true,
  "status": 200,
  "message": "Menu items retrieved",
  "data": [
    {
      "itemId": "item-123",
      "itemName": "Biryani",
      "description": "Fragrant rice with meat",
      "category": "Rice Dishes",
      "categoryId": "cat-456",
      "image": "https://cdn.example.com/biryani.jpg",
      "basePrice": 400.00,
      "preparationTime": "45 minutes",
      "servingSize": "1 plate",
      "status": "ACTIVE"
    }
    // ... more items
  ],
  "pageInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8,
    "hasMore": true
  }
}
```

**Possible Errors**:
- `401 UNAUTHORIZED`: Missing or invalid token
- `403 FORBIDDEN`: User is not a VENDOR

---

### 2️⃣ Get Vendor's Current Menu Items

**Endpoint**: `GET /vendor-items?page=0&size=20`

**Description**: Get all menu items currently in the vendor's custom menu

**Authentication**: 🔒 Required (VENDOR role)

**Headers**:
```
Authorization: Bearer <accessToken>
Content-Type: application/json
```

**Query Parameters**:
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | 0 | Page number (0-indexed) |
| `size` | int | 20 | Items per page |

**Response**: `200 OK`
```json
{
  "success": true,
  "status": 200,
  "message": "Vendor menu items retrieved",
  "data": [
    {
      "vendorItemId": "vendor-item-123",
      "vendorId": "vendor-uuid",
      "masterItemId": "item-123",
      "masterItemName": "Biryani",
      "customName": "Special House Biryani",
      "customDescription": "Our signature biryani with aged meat",
      "pricing": {
        "currency": "USD",
        "pricePerPlate": 450.00,
        "minimumOrderQuantity": 10,
        "discountPercentage": 10.5,
        "discountedPrice": 402.25
      },
      "availability": {
        "isAvailable": true,
        "lastUpdated": "2026-03-06T15:00:00Z"
      },
      "status": "ACTIVE",
      "createdAt": "2026-03-05T10:00:00Z",
      "updatedAt": "2026-03-06T15:00:00Z"
    }
    // ... more items
  ],
  "pageInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 25,
    "totalPages": 2,
    "hasMore": true
  }
}
```

**Possible Errors**:
- `401 UNAUTHORIZED`: Missing or invalid token
- `403 FORBIDDEN`: User is not a VENDOR

---

### 3️⃣ Get Single Vendor Menu Item Details

**Endpoint**: `GET /vendor-items/{vendorItemId}`

**Description**: Get detailed information about a specific item in the vendor's menu

**Authentication**: 🔒 Required (VENDOR role)

**Headers**:
```
Authorization: Bearer <accessToken>
Content-Type: application/json
```

**Path Parameters**:
| Parameter | Type | Description |
|-----------|------|-------------|
| `vendorItemId` | string | Unique ID of the vendor menu item |

**Response**: `200 OK`
```json
{
  "success": true,
  "status": 200,
  "message": "Item retrieved",
  "data": {
    "vendorItemId": "vendor-item-123",
    "vendorId": "vendor-uuid",
    "masterItemId": "item-123",
    "masterItemName": "Biryani",
    "masterItemDescription": "Fragrant rice with spices",
    "masterItemCategory": "Rice Dishes",
    "masterItemImage": "https://cdn.example.com/biryani.jpg",
    "customName": "Special House Biryani",
    "customDescription": "Our signature biryani with aged meat",
    "pricing": {
      "currency": "USD",
      "pricePerPlate": 450.00,
      "minimumOrderQuantity": 10,
      "discountPercentage": 10.5,
      "discountedPrice": 402.25
    },
    "availability": {
      "isAvailable": true,
      "lastUpdated": "2026-03-06T15:00:00Z",
      "unavailableReason": null
    },
    "specifications": {
      "preparationTime": "45 minutes",
      "servingSize": "1 plate",
      "ingredients": ["basmati rice", "mutton", "spices", "ghee"],
      "allergens": ["nuts", "sesame"]
    },
    "status": "ACTIVE",
    "createdAt": "2026-03-05T10:00:00Z",
    "updatedAt": "2026-03-06T15:00:00Z"
  }
}
```

**Possible Errors**:
- `401 UNAUTHORIZED`: Missing or invalid token
- `403 FORBIDDEN`: User is not a VENDOR
- `404 NOT_FOUND`: Item not found or doesn't belong to vendor
- `404 NOT_FOUND`: Message - "Vendor menu item not found"

---

### 4️⃣ Add New Item to Vendor Menu

**Endpoint**: `POST /vendor-items`

**Description**: Add a master menu item to the vendor's custom menu with vendor-specific pricing and details

**Authentication**: 🔒 Required (VENDOR role)

**Headers**:
```
Authorization: Bearer <accessToken>
Content-Type: application/json
```

**Request Body**:
```json
{
  "masterItemId": "item-123",                    // ✅ REQUIRED
  "customName": "Special House Biryani",        // ⬜ OPTIONAL (defaults to master item name)
  "customDescription": "Our signature biryani", // ⬜ OPTIONAL
  "pricePerPlate": 450.00,                      // ✅ REQUIRED (must be > 0)
  "minimumOrderQuantity": 10,                   // ⬜ OPTIONAL (defaults to 1)
  "discountPercentage": 10.5                    // ⬜ OPTIONAL (0-100)
}
```

**Response**: `201 CREATED`
```json
{
  "success": true,
  "status": 201,
  "message": "Item added to menu",
  "data": {
    "vendorItemId": "vendor-item-789",
    "vendorId": "vendor-uuid",
    "masterItemId": "item-123",
    "masterItemName": "Biryani",
    "customName": "Special House Biryani",
    "customDescription": "Our signature biryani",
    "pricing": {
      "currency": "USD",
      "pricePerPlate": 450.00,
      "minimumOrderQuantity": 10,
      "discountPercentage": 10.5,
      "discountedPrice": 402.25
    },
    "availability": {
      "isAvailable": true,
      "lastUpdated": "2026-03-06T15:30:00Z"
    },
    "status": "ACTIVE",
    "createdAt": "2026-03-06T15:30:00Z",
    "updatedAt": "2026-03-06T15:30:00Z"
  }
}
```

**Possible Errors**:
- `400 BAD_REQUEST`: Invalid request data
  - Message: "masterItemId is required"
  - Message: "pricePerPlate must be greater than 0"
  - Message: "discountPercentage must be between 0 and 100"

- `401 UNAUTHORIZED`: Missing or invalid token
- `403 FORBIDDEN`: User is not a VENDOR
- `404 NOT_FOUND`: Master item not found
  - Message: "Master menu item not found"
- `409 CONFLICT`: Item already exists in vendor menu
  - Message: "This item is already in your menu"

**Calculation Example**:
```
Base Price: 450.00
Discount:   10.5%
Calculation: 450 × 10.5 ÷ 100 = 47.25
Final Price: 450 - 47.25 = 402.75 (USD per plate)
Minimum Order: 10 plates = 4,025.00 USD minimum
```

---

### 5️⃣ Update Vendor Menu Item

**Endpoint**: `PUT /vendor-items/{vendorItemId}`

**Description**: Update specifications, pricing, and custom details of a vendor menu item

**Authentication**: 🔒 Required (VENDOR role)

**Headers**:
```
Authorization: Bearer <accessToken>
Content-Type: application/json
```

**Path Parameters**:
| Parameter | Type | Description |
|-----------|------|-------------|
| `vendorItemId` | string | Unique ID of the vendor menu item |

**Request Body** (All fields optional - send only what you want to update):
```json
{
  "customName": "Updated House Biryani",        // ⬜ OPTIONAL
  "customDescription": "Premium aged meat",     // ⬜ OPTIONAL
  "pricePerPlate": 500.00,                      // ⬜ OPTIONAL
  "minimumOrderQuantity": 15,                   // ⬜ OPTIONAL
  "discountPercentage": 12.5                    // ⬜ OPTIONAL
}
```

**Response**: `200 OK`
```json
{
  "success": true,
  "status": 200,
  "message": "Item updated",
  "data": {
    "vendorItemId": "vendor-item-789",
    "vendorId": "vendor-uuid",
    "masterItemId": "item-123",
    "masterItemName": "Biryani",
    "customName": "Updated House Biryani",
    "customDescription": "Premium aged meat",
    "pricing": {
      "currency": "USD",
      "pricePerPlate": 500.00,
      "minimumOrderQuantity": 15,
      "discountPercentage": 12.5,
      "discountedPrice": 437.50
    },
    "availability": {
      "isAvailable": true,
      "lastUpdated": "2026-03-06T15:45:00Z"
    },
    "status": "ACTIVE",
    "createdAt": "2026-03-05T10:00:00Z",
    "updatedAt": "2026-03-06T15:45:00Z"
  }
}
```

**Possible Errors**:
- `400 BAD_REQUEST`: Invalid request data
- `401 UNAUTHORIZED`: Missing or invalid token
- `403 FORBIDDEN`: User is not a VENDOR or item doesn't belong to vendor
  - Message: "You cannot update this item"
- `404 NOT_FOUND`: Item not found
  - Message: "Vendor menu item not found"

**Update Notes**:
- Send only the fields you want to update
- Discount is automatically recalculated when price or discount percentage changes
- Other fields remain unchanged if not provided

---

### 6️⃣ Update Item Availability

**Endpoint**: `PATCH /vendor-items/{vendorItemId}/availability`

**Description**: Toggle item availability and provide reason if making unavailable

**Authentication**: 🔒 Required (VENDOR role)

**Headers**:
```
Authorization: Bearer <accessToken>
Content-Type: application/json
```

**Path Parameters**:
| Parameter | Type | Description |
|-----------|------|-------------|
| `vendorItemId` | string | Unique ID of the vendor menu item |

**Query Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `isAvailable` | boolean | ✅ Yes | true = available, false = unavailable |
| `reason` | string | ⬜ No | Reason for unavailability (required if isAvailable=false) |

**Example Request**:
```
PATCH /vendor-items/vendor-item-789/availability?isAvailable=false&reason=Out%20of%20stock%20for%20event
```

**Response**: `200 OK`
```json
{
  "success": true,
  "status": 200,
  "message": "Availability updated",
  "data": {
    "vendorItemId": "vendor-item-789",
    "vendorId": "vendor-uuid",
    "masterItemId": "item-123",
    "customName": "Special House Biryani",
    "availability": {
      "isAvailable": false,
      "lastUpdated": "2026-03-06T16:00:00Z",
      "unavailableReason": "Out of stock for event"
    },
    "status": "INACTIVE",
    "updatedAt": "2026-03-06T16:00:00Z"
  }
}
```

**Another Example - Making Available**:
```
PATCH /vendor-items/vendor-item-789/availability?isAvailable=true
```

**Response**:
```json
{
  "success": true,
  "status": 200,
  "message": "Availability updated",
  "data": {
    "vendorItemId": "vendor-item-789",
    "availability": {
      "isAvailable": true,
      "lastUpdated": "2026-03-06T16:15:00Z",
      "unavailableReason": null
    },
    "status": "ACTIVE"
  }
}
```

**Possible Errors**:
- `400 BAD_REQUEST`: isAvailable parameter is missing
- `401 UNAUTHORIZED`: Missing or invalid token
- `403 FORBIDDEN`: User is not a VENDOR or item doesn't belong to vendor
- `404 NOT_FOUND`: Item not found
  - Message: "Vendor menu item not found"

---

### 7️⃣ Delete Vendor Menu Item

**Endpoint**: `DELETE /vendor-items/{vendorItemId}`

**Description**: Remove an item from the vendor's menu (soft delete - status becomes INACTIVE)

**Authentication**: 🔒 Required (VENDOR role)

**Headers**:
```
Authorization: Bearer <accessToken>
Content-Type: application/json
```

**Path Parameters**:
| Parameter | Type | Description |
|-----------|------|-------------|
| `vendorItemId` | string | Unique ID of the vendor menu item |

**Response**: `200 OK`
```json
{
  "success": true,
  "status": 200,
  "message": "Item deleted",
  "data": null
}
```

**Possible Errors**:
- `401 UNAUTHORIZED`: Missing or invalid token
- `403 FORBIDDEN`: User is not a VENDOR or item doesn't belong to vendor
  - Message: "You cannot delete this item"
- `404 NOT_FOUND`: Item not found
  - Message: "Vendor menu item not found"

**Note**: This is a soft delete - the item status changes to INACTIVE but the record remains in the database for historical tracking.

---

## ⚠️ Error Handling

### Standard Error Response Format

All errors follow this format:

```json
{
  "success": false,
  "status": 400,
  "message": "Error message",
  "data": null,
  "error": {
    "code": "ERROR_CODE",
    "message": "Detailed error message",
    "path": "/api/v1/menu/vendor-items",
    "timestamp": "2026-03-06T16:00:00Z"
  }
}
```

### Common Error Codes

| HTTP | Code | Message | Cause |
|------|------|---------|-------|
| 400 | BAD_REQUEST | Invalid request data | Malformed JSON or missing required fields |
| 401 | UNAUTHORIZED | Missing or invalid token | No Authorization header or expired token |
| 403 | FORBIDDEN | Access denied | User role mismatch or insufficient permissions |
| 404 | NOT_FOUND | Resource not found | Item/vendor doesn't exist |
| 409 | CONFLICT | Item already exists | Duplicate entry attempt |
| 500 | INTERNAL_ERROR | Server error | Unexpected server error |

### Validation Rules

#### Price Fields
- `pricePerPlate`: Must be > 0, max 2 decimal places
- `discountPercentage`: Must be between 0 and 100, max 2 decimal places
- `minimumOrderQuantity`: Must be >= 1, integer only

#### Text Fields
- `customName`: Max 255 characters
- `customDescription`: Max 1000 characters
- `reason` (availability): Max 500 characters

#### masterItemId
- Must be a valid ID of an existing master menu item in the platform
- Item must be in ACTIVE status to be added

---

## 🔑 Key Concepts

### 1. Master Items vs Vendor Items
- **Master Items**: Created by admins, available globally, define base specifications
- **Vendor Items**: Created by vendors from master items, vendor-specific customization

### 2. Price Calculation
Discounted price is automatically calculated:
```
Discounted Price = Price Per Plate - (Price Per Plate × Discount % ÷ 100)
```

### 3. Availability vs Status
- **Availability**: Whether item is temporarily available (can be toggled frequently)
- **Status**: Item's overall state in the system (ACTIVE/INACTIVE)

### 4. Soft Delete
Items are never permanently deleted - status changes to INACTIVE, preserving historical data for past orders.

### 5. Pagination
All list endpoints support pagination:
- Use `page` (0-indexed) and `size` parameters
- Response includes `pageInfo` with `totalElements`, `totalPages`, `hasMore`

---

## 📱 Example Workflow

### Complete Vendor Menu Setup Flow

```
1. Vendor browses available items
   GET /menu/items?page=0&size=20

2. Vendor finds item "Biryani" (masterItemId: item-123)

3. Vendor adds to their menu with custom pricing
   POST /vendor-items
   {
     "masterItemId": "item-123",
     "customName": "Special House Biryani",
     "pricePerPlate": 450.00,
     "minimumOrderQuantity": 10,
     "discountPercentage": 10.5
   }
   Response: vendorItemId = "vendor-item-789"

4. Vendor gets their menu
   GET /vendor-items?page=0&size=20

5. Vendor views specific item details
   GET /vendor-items/vendor-item-789

6. Vendor updates pricing later
   PUT /vendor-items/vendor-item-789
   {
     "pricePerPlate": 500.00,
     "discountPercentage": 12.5
   }

7. Vendor temporarily makes item unavailable
   PATCH /vendor-items/vendor-item-789/availability?isAvailable=false&reason=Out%20of%20stock

8. Vendor removes item from menu
   DELETE /vendor-items/vendor-item-789
```

---

## 🎯 Best Practices

### For Pricing
- Set competitive prices based on quality and market rates
- Use discounts strategically during promotions
- Consider minimum order quantities for profitability

### For Availability
- Keep items available when you can fulfill orders
- Provide clear reasons when items are unavailable
- Update availability status in real-time

### For Customization
- Use custom names to highlight your unique offering
- Provide detailed descriptions of your preparation style
- Keep specifications updated with accurate information

### For Menu Management
- Regularly review and update your menu
- Remove items you no longer serve
- Adjust pricing based on ingredient costs
- Monitor customer interest in different items

---

**Last Updated**: March 6, 2026
**Version**: 1.0
**Status**: Production Ready


