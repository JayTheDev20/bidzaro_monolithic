# 🍽️ Vendor Menu Management API
**Bidzaro Catering Platform** | Base URL: `http://localhost:8080/api/v1/menu`

> 🔒 All endpoints marked with 🔒 require `Authorization: Bearer <accessToken>` header with **VENDOR** role.
> ✅ = Required field | ⬜ = Optional field

---

## 📋 Table of Contents
1. [Enums Reference](#-enums-reference)
2. [Request DTO](#-request-dto--vendormenuitemrequest)
3. [Response DTO](#-response-dto--vendormenuitemresponse)
4. [Endpoint 1 — Browse Master Items](#1️⃣-browse-master-menu-items)
5. [Endpoint 2 — List Vendor Menu](#2️⃣-get-vendor-menu-items-list)
6. [Endpoint 3 — Get Single Item](#3️⃣-get-single-vendor-menu-item)
7. [Endpoint 4 — Add Item to Menu](#4️⃣-add-item-to-vendor-menu)
8. [Endpoint 5 — Update Item](#5️⃣-update-vendor-menu-item)
9. [Endpoint 6 — Update Availability](#6️⃣-update-item-availability)
10. [Endpoint 7 — Delete Item](#7️⃣-delete-vendor-menu-item)
11. [Error Reference](#-error-reference)
12. [Discount Calculation](#-discount-calculation-logic)
13. [Complete Workflow Example](#-complete-workflow-example)

---

## 🔢 Enums Reference

### VendorItemStatus
| Value | Description |
|-------|-------------|
| `ACTIVE` | Item is live on the vendor menu |
| `INACTIVE` | Item hidden / soft-deleted |
| `OUT_OF_STOCK` | Item temporarily out of stock |

---

## 📥 Request DTO — `VendorMenuItemRequest`

Used for **Add Item** (POST) and **Update Item** (PUT).

```json
{
  "masterItemId":          "string",   // ✅ Required on POST; ⬜ ignored on PUT
  "customName":            "string",   // ⬜ Optional  – overrides master item name
  "customDescription":     "string",   // ⬜ Optional  – custom description text
  "pricePerPlate":         0.00,       // ✅ Required on POST; ⬜ Optional on PUT
  "minimumOrderQuantity":  1,          // ⬜ Optional  – minimum plates (default: 1, min: 1)
  "discountPercentage":    0.0,        // ⬜ Optional  – 0.0 – 100.0
  "isAvailable":           true,       // ⬜ Optional  – default: true
  "unavailableReason":     "string",   // ⬜ Optional  – reason shown when unavailable
  "advanceNoticeHours":    0,          // ⬜ Optional  – hours notice needed (min: 0)
  "maxDailyCapacity":      0,          // ⬜ Optional  – max plates per day (min: 1)
  "preparationTimeMinutes": 0,         // ⬜ Optional  – prep time in minutes (min: 1)
  "customizationOptions": [            // ⬜ Optional  – list of add-on options
    {
      "optionName":    "string",       // ✅ Required (if options provided)
      "choices":       ["string"],     // ✅ Required – must not be empty
      "additionalCost": 0.00,          // ⬜ Optional  – extra cost per choice (min: 0.0)
      "isRequired":    false           // ⬜ Optional  – default: false
    }
  ]
}
```

### Field Validation Rules

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `masterItemId` | `String` | ✅ POST only | Must be a valid existing master item ID |
| `customName` | `String` | ⬜ | No length constraint, defaults to master item name |
| `customDescription` | `String` | ⬜ | Free text |
| `pricePerPlate` | `BigDecimal` | ✅ POST | Must be ≥ 0.01 |
| `minimumOrderQuantity` | `Integer` | ⬜ | Must be ≥ 1; defaults to 1 |
| `discountPercentage` | `BigDecimal` | ⬜ | 0.0 ≤ value ≤ 100.0 |
| `isAvailable` | `Boolean` | ⬜ | true / false; defaults to true |
| `unavailableReason` | `String` | ⬜ | Free text |
| `advanceNoticeHours` | `Integer` | ⬜ | Must be ≥ 0 |
| `maxDailyCapacity` | `Integer` | ⬜ | Must be ≥ 1 |
| `preparationTimeMinutes` | `Integer` | ⬜ | Must be ≥ 1 |
| `customizationOptions[].optionName` | `String` | ✅ if list provided | Must not be blank |
| `customizationOptions[].choices` | `List<String>` | ✅ if list provided | Must not be empty |
| `customizationOptions[].additionalCost` | `BigDecimal` | ⬜ | Must be ≥ 0.0 |
| `customizationOptions[].isRequired` | `Boolean` | ⬜ | defaults to false |

---

## 📤 Response DTO — `VendorMenuItemResponse`

Returned by all GET, POST, PUT, PATCH endpoints.

```json
{
  "vendorItemId": "f3b1c2d4-...",
  "vendorId":     "v9a8b7c6-...",
  "masterItemId": "m1a2b3c4-...",
  "customName":   "Special House Biryani",
  "customDescription": "Our signature slow-cooked biryani",
  "pricing": {
    "currency":             "USD",
    "pricePerPlate":        450.00,
    "minimumOrderQuantity": 10,
    "discountPercentage":   10.50,
    "discountedPrice":      402.75
  },
  "availability": {
    "isAvailable":       true,
    "unavailableReason": null,
    "unavailableUntil":  null,
    "advanceNoticeHours": 24,
    "maxDailyCapacity":  200
  },
  "preparationTimeMinutes": 45,
  "customizationOptions": [
    {
      "optionName":     "Meat Type",
      "choices":        ["Chicken", "Mutton", "Veg"],
      "additionalCost": 50.00,
      "isRequired":     true
    }
  ],
  "stats": {
    "totalOrders":   12,
    "averageRating": 4.5,
    "totalReviews":  8
  },
  "status":    "ACTIVE",
  "createdAt": "2026-03-06T10:00:00Z"
}
```

### Response Field Reference

| Field | Type | Description |
|-------|------|-------------|
| `vendorItemId` | `String` | Unique ID of this vendor menu item |
| `vendorId` | `String` | ID of the vendor who owns this item |
| `masterItemId` | `String` | ID of the platform master menu item |
| `customName` | `String` | Vendor's custom name (null = uses master name) |
| `customDescription` | `String` | Vendor's custom description |
| `pricing.currency` | `String` | Always `"USD"` |
| `pricing.pricePerPlate` | `BigDecimal` | Base price set by vendor |
| `pricing.minimumOrderQuantity` | `Integer` | Minimum plates per order |
| `pricing.discountPercentage` | `BigDecimal` | Applied discount % |
| `pricing.discountedPrice` | `BigDecimal` | Auto-calculated final price |
| `availability.isAvailable` | `Boolean` | true = accepting orders now |
| `availability.unavailableReason` | `String` | Reason shown to customers |
| `availability.unavailableUntil` | `Instant` | Auto re-enable timestamp |
| `availability.advanceNoticeHours` | `Integer` | Hours needed before event |
| `availability.maxDailyCapacity` | `Integer` | Max plates vendor can supply daily |
| `preparationTimeMinutes` | `Integer` | Time to prepare the dish |
| `customizationOptions` | `Array` | Add-on choices for this item |
| `stats.totalOrders` | `Integer` | Total times ordered |
| `stats.averageRating` | `BigDecimal` | Average customer rating |
| `stats.totalReviews` | `Integer` | Total number of reviews |
| `status` | `String` | `ACTIVE` / `INACTIVE` / `OUT_OF_STOCK` |
| `createdAt` | `Instant` | ISO-8601 timestamp |

> **Note:** Fields with `null` values are omitted from the response (`@JsonInclude(NON_NULL)`).

---

## 1️⃣ Browse Master Menu Items

Browse the platform's master catalogue — items a vendor can add to their menu.

**`GET /api/v1/menu/items`**

**Authentication:** Not required (public endpoint)

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | `int` | `0` | Page number (0-indexed) |
| `size` | `int` | `20` | Items per page |

**Request:**
```
GET /api/v1/menu/items?page=0&size=12
```

**Response — 200 OK:**
```json
{
  "success": true,
  "status": 200,
  "message": "Menu items retrieved",
  "data": [
    {
      "masterItemId": "m1a2b3c4-0001",
      "itemName": "Chicken Biryani",
      "description": "Fragrant basmati rice with spiced chicken",
      "categoryId": "cat-001",
      "categoryName": "Rice Dishes",
      "imageUrls": ["https://cdn.example.com/biryani.jpg"],
      "foodType": "NON_VEG",
      "spiceLevel": "MEDIUM",
      "dietaryTags": ["GLUTEN_FREE"],
      "allergens": ["NUTS"],
      "isPopular": true,
      "status": "ACTIVE"
    }
  ],
  "pageInfo": {
    "page": 0,
    "size": 12,
    "totalElements": 48,
    "totalPages": 4,
    "hasMore": true
  }
}
```

---

## 2️⃣ Get Vendor Menu Items List

Retrieve all items currently in a vendor's menu.

**`GET /api/v1/menu/vendor-items`** *(alias: `/menu/vendor`)*

**Authentication:** Not required (public — filters by vendorId query param)

> ⚠️ The vendor's own dashboard should use this with their `vendorId`. The authenticated add/update/delete endpoints use the token to derive vendorId automatically.

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `vendorId` | `String` | ✅ Yes | — | The vendor's ID |
| `page` | `int` | ⬜ No | `0` | Page number |
| `size` | `int` | ⬜ No | `20` | Items per page |

**Request:**
```
GET /api/v1/menu/vendor-items?vendorId=v9a8b7c6&page=0&size=20
```

**Response — 200 OK:**
```json
{
  "success": true,
  "status": 200,
  "message": "Vendor menu items retrieved",
  "data": [
    {
      "vendorItemId": "f3b1c2d4-0001",
      "vendorId": "v9a8b7c6",
      "masterItemId": "m1a2b3c4-0001",
      "customName": "Special House Biryani",
      "customDescription": "Slow-cooked in clay pot with aged basmati",
      "pricing": {
        "currency": "USD",
        "pricePerPlate": 450.00,
        "minimumOrderQuantity": 10,
        "discountPercentage": 10.50,
        "discountedPrice": 402.75
      },
      "availability": {
        "isAvailable": true,
        "advanceNoticeHours": 24,
        "maxDailyCapacity": 200
      },
      "preparationTimeMinutes": 45,
      "stats": {
        "totalOrders": 12,
        "averageRating": 4.5,
        "totalReviews": 8
      },
      "status": "ACTIVE",
      "createdAt": "2026-03-01T10:00:00Z"
    }
  ],
  "pageInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 15,
    "totalPages": 1,
    "hasMore": false
  }
}
```

---

## 3️⃣ Get Single Vendor Menu Item

Get full details of one item in a vendor's menu.

**`GET /api/v1/menu/vendor-items/{vendorItemId}`** *(alias: `/menu/vendor/{vendorItemId}`)*

**Authentication:** Not required

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `vendorItemId` | `String` | The `vendorItemId` from the list response |

**Request:**
```
GET /api/v1/menu/vendor-items/f3b1c2d4-0001
```

**Response — 200 OK:**
```json
{
  "success": true,
  "status": 200,
  "message": null,
  "data": {
    "vendorItemId": "f3b1c2d4-0001",
    "vendorId": "v9a8b7c6",
    "masterItemId": "m1a2b3c4-0001",
    "customName": "Special House Biryani",
    "customDescription": "Slow-cooked in clay pot with aged basmati",
    "pricing": {
      "currency": "USD",
      "pricePerPlate": 450.00,
      "minimumOrderQuantity": 10,
      "discountPercentage": 10.50,
      "discountedPrice": 402.75
    },
    "availability": {
      "isAvailable": true,
      "unavailableReason": null,
      "unavailableUntil": null,
      "advanceNoticeHours": 24,
      "maxDailyCapacity": 200
    },
    "preparationTimeMinutes": 45,
    "customizationOptions": [
      {
        "optionName": "Meat Type",
        "choices": ["Chicken", "Mutton", "Veg"],
        "additionalCost": 50.00,
        "isRequired": true
      },
      {
        "optionName": "Spice Level",
        "choices": ["Mild", "Medium", "Hot"],
        "additionalCost": 0.00,
        "isRequired": false
      }
    ],
    "stats": {
      "totalOrders": 12,
      "averageRating": 4.50,
      "totalReviews": 8
    },
    "status": "ACTIVE",
    "createdAt": "2026-03-01T10:00:00Z"
  }
}
```

**Error Responses:**
| Status | Code | Message |
|--------|------|---------|
| 404 | `NOT_FOUND` | `"Vendor menu item not found"` |

---

## 4️⃣ Add Item to Vendor Menu

🔒 Add a master menu item to the vendor's menu with custom pricing and specifications.

**`POST /api/v1/menu/vendor-items`** *(alias: `/menu/vendor`)*

**Authentication:** 🔒 Required — `VENDOR` role

**Headers:**
```
Authorization: Bearer <accessToken>
Content-Type: application/json
```

### Minimal Request (required fields only)
```json
{
  "masterItemId": "m1a2b3c4-0001",
  "pricePerPlate": 450.00
}
```

### Full Request (all fields)
```json
{
  "masterItemId":           "m1a2b3c4-0001",
  "customName":             "Special House Biryani",
  "customDescription":      "Slow-cooked in clay pot with aged basmati rice",
  "pricePerPlate":          450.00,
  "minimumOrderQuantity":   10,
  "discountPercentage":     10.50,
  "isAvailable":            true,
  "unavailableReason":      null,
  "advanceNoticeHours":     24,
  "maxDailyCapacity":       200,
  "preparationTimeMinutes": 45,
  "customizationOptions": [
    {
      "optionName":     "Meat Type",
      "choices":        ["Chicken", "Mutton", "Veg"],
      "additionalCost": 50.00,
      "isRequired":     true
    },
    {
      "optionName":     "Spice Level",
      "choices":        ["Mild", "Medium", "Hot"],
      "additionalCost": 0.00,
      "isRequired":     false
    }
  ]
}
```

**Response — 201 Created:**
```json
{
  "success": true,
  "status": 201,
  "message": "Item added to menu",
  "data": {
    "vendorItemId": "f3b1c2d4-9999",
    "vendorId": "v9a8b7c6",
    "masterItemId": "m1a2b3c4-0001",
    "customName": "Special House Biryani",
    "customDescription": "Slow-cooked in clay pot with aged basmati rice",
    "pricing": {
      "currency": "USD",
      "pricePerPlate": 450.00,
      "minimumOrderQuantity": 10,
      "discountPercentage": 10.50,
      "discountedPrice": 402.75
    },
    "availability": {
      "isAvailable": true,
      "advanceNoticeHours": 24,
      "maxDailyCapacity": 200
    },
    "preparationTimeMinutes": 45,
    "customizationOptions": [
      {
        "optionName": "Meat Type",
        "choices": ["Chicken", "Mutton", "Veg"],
        "additionalCost": 50.00,
        "isRequired": true
      },
      {
        "optionName": "Spice Level",
        "choices": ["Mild", "Medium", "Hot"],
        "additionalCost": 0.00,
        "isRequired": false
      }
    ],
    "stats": {
      "totalOrders": 0,
      "averageRating": 0,
      "totalReviews": 0
    },
    "status": "ACTIVE",
    "createdAt": "2026-03-06T16:42:03Z"
  }
}
```

**Error Responses:**
| Status | Code | Message | Cause |
|--------|------|---------|-------|
| 400 | `BAD_REQUEST` | `"Master item ID is required"` | Missing `masterItemId` |
| 400 | `BAD_REQUEST` | `"Price must be positive"` | `pricePerPlate` < 0.01 |
| 400 | `BAD_REQUEST` | `"Discount cannot exceed 100%"` | `discountPercentage` > 100 |
| 400 | `BAD_REQUEST` | `"Minimum order quantity must be at least 1"` | `minimumOrderQuantity` < 1 |
| 400 | `BAD_REQUEST` | `"Option name is required"` | Blank `customizationOptions[].optionName` |
| 400 | `BAD_REQUEST` | `"Choices list cannot be empty"` | Empty `customizationOptions[].choices` |
| 401 | `UNAUTHORIZED` | `"Unauthorized"` | Missing or invalid token |
| 403 | `FORBIDDEN` | `"Access Denied"` | User is not a VENDOR |
| 404 | `NOT_FOUND` | `"Master menu item not found"` | `masterItemId` does not exist |
| 409 | `CONFLICT` | `"This item is already in your menu"` | Duplicate — item already added |

---

## 5️⃣ Update Vendor Menu Item

🔒 Update specifications, pricing, availability, and customization options for an existing vendor menu item.

**`PUT /api/v1/menu/vendor-items/{vendorItemId}`** *(alias: `/menu/vendor/{vendorItemId}`)*

**Authentication:** 🔒 Required — `VENDOR` role (only the item's owner can update)

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `vendorItemId` | `String` | ID of the vendor menu item to update |

> **Partial update behavior:** All fields are optional. Only fields included in the request body are updated. Fields not sent remain unchanged.

### Update Only Price
```json
{
  "pricePerPlate": 500.00
}
```

### Update Price + Discount (discountedPrice auto-recalculated)
```json
{
  "pricePerPlate": 500.00,
  "discountPercentage": 12.50
}
```

### Update Custom Name & Description
```json
{
  "customName": "Premium Dum Biryani",
  "customDescription": "Cooked in sealed handi with saffron"
}
```

### Update Availability Settings
```json
{
  "isAvailable": false,
  "unavailableReason": "Closed for private event this week",
  "advanceNoticeHours": 48,
  "maxDailyCapacity": 300
}
```

### Update Preparation Time
```json
{
  "preparationTimeMinutes": 60
}
```

### Update Customization Options (replaces all existing options)
```json
{
  "customizationOptions": [
    {
      "optionName": "Protein Choice",
      "choices": ["Chicken", "Mutton", "Paneer", "Veg"],
      "additionalCost": 75.00,
      "isRequired": true
    }
  ]
}
```

### Full Update Request
```json
{
  "customName":             "Premium Dum Biryani",
  "customDescription":      "Cooked in sealed handi with saffron",
  "pricePerPlate":          500.00,
  "minimumOrderQuantity":   15,
  "discountPercentage":     12.50,
  "isAvailable":            true,
  "unavailableReason":      null,
  "advanceNoticeHours":     48,
  "maxDailyCapacity":       300,
  "preparationTimeMinutes": 60,
  "customizationOptions": [
    {
      "optionName":     "Protein Choice",
      "choices":        ["Chicken", "Mutton", "Paneer", "Veg"],
      "additionalCost": 75.00,
      "isRequired":     true
    },
    {
      "optionName":     "Extras",
      "choices":        ["Extra Raita", "Extra Salan"],
      "additionalCost": 30.00,
      "isRequired":     false
    }
  ]
}
```

**Response — 200 OK:**
```json
{
  "success": true,
  "status": 200,
  "message": "Item updated",
  "data": {
    "vendorItemId": "f3b1c2d4-0001",
    "vendorId": "v9a8b7c6",
    "masterItemId": "m1a2b3c4-0001",
    "customName": "Premium Dum Biryani",
    "customDescription": "Cooked in sealed handi with saffron",
    "pricing": {
      "currency": "USD",
      "pricePerPlate": 500.00,
      "minimumOrderQuantity": 15,
      "discountPercentage": 12.50,
      "discountedPrice": 437.50
    },
    "availability": {
      "isAvailable": true,
      "advanceNoticeHours": 48,
      "maxDailyCapacity": 300
    },
    "preparationTimeMinutes": 60,
    "customizationOptions": [
      {
        "optionName": "Protein Choice",
        "choices": ["Chicken", "Mutton", "Paneer", "Veg"],
        "additionalCost": 75.00,
        "isRequired": true
      },
      {
        "optionName": "Extras",
        "choices": ["Extra Raita", "Extra Salan"],
        "additionalCost": 30.00,
        "isRequired": false
      }
    ],
    "stats": {
      "totalOrders": 12,
      "averageRating": 4.50,
      "totalReviews": 8
    },
    "status": "ACTIVE",
    "createdAt": "2026-03-01T10:00:00Z"
  }
}
```

**Error Responses:**
| Status | Code | Message | Cause |
|--------|------|---------|-------|
| 400 | `FORBIDDEN` | `"You cannot update this item"` | Vendor trying to update another vendor's item |
| 401 | `UNAUTHORIZED` | `"Unauthorized"` | Missing or invalid token |
| 403 | `FORBIDDEN` | `"Access Denied"` | User is not a VENDOR |
| 404 | `NOT_FOUND` | `"Vendor menu item not found"` | `vendorItemId` does not exist |

---

## 6️⃣ Update Item Availability

🔒 Quickly toggle whether an item is currently available for customer orders.

**`PATCH /api/v1/menu/vendor-items/{vendorItemId}/availability`** *(alias: `/menu/vendor/{vendorItemId}/availability`)*

**Authentication:** 🔒 Required — `VENDOR` role

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `vendorItemId` | `String` | ID of the vendor menu item |

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `isAvailable` | `boolean` | ✅ Yes | `true` = available, `false` = unavailable |
| `reason` | `String` | ⬜ No | Reason text shown when `isAvailable=false` |

> When `isAvailable=true` → item status → `ACTIVE`
> When `isAvailable=false` → item status → `INACTIVE`

**Make Item Unavailable:**
```
PATCH /api/v1/menu/vendor-items/f3b1c2d4-0001/availability?isAvailable=false&reason=Out%20of%20stock%20till%20Monday
```

**Response — 200 OK:**
```json
{
  "success": true,
  "status": 200,
  "message": "Availability updated",
  "data": {
    "vendorItemId": "f3b1c2d4-0001",
    "vendorId": "v9a8b7c6",
    "masterItemId": "m1a2b3c4-0001",
    "customName": "Special House Biryani",
    "availability": {
      "isAvailable": false,
      "unavailableReason": "Out of stock till Monday"
    },
    "status": "INACTIVE",
    "createdAt": "2026-03-01T10:00:00Z"
  }
}
```

**Make Item Available Again:**
```
PATCH /api/v1/menu/vendor-items/f3b1c2d4-0001/availability?isAvailable=true
```

**Response — 200 OK:**
```json
{
  "success": true,
  "status": 200,
  "message": "Availability updated",
  "data": {
    "vendorItemId": "f3b1c2d4-0001",
    "availability": {
      "isAvailable": true,
      "unavailableReason": null
    },
    "status": "ACTIVE",
    "createdAt": "2026-03-01T10:00:00Z"
  }
}
```

**Error Responses:**
| Status | Code | Message | Cause |
|--------|------|---------|-------|
| 400 | `FORBIDDEN` | `"You cannot update this item"` | Item belongs to a different vendor |
| 401 | `UNAUTHORIZED` | `"Unauthorized"` | Missing or invalid token |
| 403 | `FORBIDDEN` | `"Access Denied"` | User is not a VENDOR |
| 404 | `NOT_FOUND` | `"Vendor menu item not found"` | `vendorItemId` does not exist |

---

## 7️⃣ Delete Vendor Menu Item

🔒 Remove an item from the vendor's menu. This is a **soft delete** — the record is not permanently deleted, the status is set to `INACTIVE`.

**`DELETE /api/v1/menu/vendor-items/{vendorItemId}`** *(alias: `/menu/vendor/{vendorItemId}`)*

**Authentication:** 🔒 Required — `VENDOR` role

**Path Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| `vendorItemId` | `String` | ID of the vendor menu item to remove |

**Request:**
```
DELETE /api/v1/menu/vendor-items/f3b1c2d4-0001
Authorization: Bearer <accessToken>
```

**Response — 200 OK:**
```json
{
  "success": true,
  "status": 200,
  "message": "Item removed from menu",
  "data": null
}
```

**Error Responses:**
| Status | Code | Message | Cause |
|--------|------|---------|-------|
| 400 | `FORBIDDEN` | `"You cannot delete this item"` | Item belongs to a different vendor |
| 401 | `UNAUTHORIZED` | `"Unauthorized"` | Missing or invalid token |
| 403 | `FORBIDDEN` | `"Access Denied"` | User is not a VENDOR |
| 404 | `NOT_FOUND` | `"Vendor menu item not found"` | `vendorItemId` does not exist |

---

## ❌ Error Reference

### Standard Error Response Format
```json
{
  "success": false,
  "status": 404,
  "message": "Vendor menu item not found",
  "data": null,
  "error": {
    "status": 404,
    "message": "Vendor menu item not found",
    "path": "/api/v1/menu/vendor-items/bad-id",
    "timestamp": "2026-03-06T16:42:00Z"
  }
}
```

### All Possible Error Codes

| HTTP | Scenario |
|------|----------|
| `400` | Validation failed (missing required field, out-of-range value) |
| `400` | Trying to update/delete another vendor's item |
| `401` | No `Authorization` header or expired/invalid JWT token |
| `403` | Authenticated but not a VENDOR (e.g. USER or ADMIN role) |
| `404` | `masterItemId` or `vendorItemId` not found |
| `409` | Item with same `masterItemId` already exists in vendor's menu |
| `500` | Unhandled server error |

---

## 🧮 Discount Calculation Logic

The `discountedPrice` is **auto-calculated by the server** — never send it in requests.

```
discountAmount  = pricePerPlate × (discountPercentage ÷ 100)
discountedPrice = pricePerPlate − discountAmount
                = pricePerPlate × (1 − discountPercentage ÷ 100)
```

**Examples:**

| pricePerPlate | discountPercentage | discountedPrice |
|---------------|--------------------|-----------------|
| 450.00 | 10.50 | 402.75 |
| 500.00 | 12.50 | 437.50 |
| 300.00 | 0.00 | 300.00 |
| 200.00 | 25.00 | 150.00 |
| 1000.00 | 5.00 | 950.00 |

> Rounding: `HALF_UP`, 2 decimal places.

**Effective Price Rule (server-side logic):**
1. If `discountedPrice` is set and > 0 → use `discountedPrice`
2. Else if `discountPercentage` > 0 → calculate and use
3. Else → use `pricePerPlate` as-is

---

## 🔄 Complete Workflow Example

### Scenario: Vendor adds Biryani, sets discount, later makes it unavailable

**Step 1 — Vendor logs in**
```
POST /api/v1/auth/login
→ accessToken: "eyJhbGciOi..."
```

**Step 2 — Browse master items to find `masterItemId`**
```
GET /api/v1/menu/items?page=0&size=20
→ masterItemId: "m1a2b3c4-0001" (Chicken Biryani)
```

**Step 3 — Add item to vendor menu**
```
POST /api/v1/menu/vendor-items
Authorization: Bearer eyJhbGciOi...

{
  "masterItemId":           "m1a2b3c4-0001",
  "customName":             "Special House Biryani",
  "pricePerPlate":          450.00,
  "minimumOrderQuantity":   10,
  "discountPercentage":     10.50,
  "advanceNoticeHours":     24,
  "maxDailyCapacity":       200,
  "preparationTimeMinutes": 45,
  "customizationOptions": [
    {
      "optionName": "Meat Type",
      "choices": ["Chicken", "Mutton", "Veg"],
      "additionalCost": 50.00,
      "isRequired": true
    }
  ]
}

→ 201 Created
→ vendorItemId: "f3b1c2d4-9999"
→ discountedPrice: 402.75  (auto-calculated)
```

**Step 4 — View vendor menu**
```
GET /api/v1/menu/vendor-items?vendorId=v9a8b7c6&page=0&size=20
→ 200 OK, list of items
```

**Step 5 — Update price during festival season**
```
PUT /api/v1/menu/vendor-items/f3b1c2d4-9999
Authorization: Bearer eyJhbGciOi...

{
  "pricePerPlate": 550.00,
  "discountPercentage": 15.00
}

→ 200 OK
→ discountedPrice: 467.50  (auto-recalculated)
```

**Step 6 — Temporarily unavailable (stock issue)**
```
PATCH /api/v1/menu/vendor-items/f3b1c2d4-9999/availability?isAvailable=false&reason=Mutton%20supplier%20delayed
Authorization: Bearer eyJhbGciOi...

→ 200 OK
→ status: "INACTIVE"
```

**Step 7 — Make available again**
```
PATCH /api/v1/menu/vendor-items/f3b1c2d4-9999/availability?isAvailable=true
Authorization: Bearer eyJhbGciOi...

→ 200 OK
→ status: "ACTIVE"
```

**Step 8 — Remove item from menu**
```
DELETE /api/v1/menu/vendor-items/f3b1c2d4-9999
Authorization: Bearer eyJhbGciOi...

→ 200 OK
→ data: null  (soft deleted — status = INACTIVE)
```

---

## 🗂️ Endpoint Summary

| # | Method | Path | Auth | Description |
|---|--------|------|------|-------------|
| 1 | `GET` | `/api/v1/menu/items` | ❌ Public | Browse all master items |
| 2 | `GET` | `/api/v1/menu/vendor-items` | ❌ Public | List vendor's menu |
| 2a | `GET` | `/api/v1/menu/vendor` | ❌ Public | Alias for above |
| 3 | `GET` | `/api/v1/menu/vendor-items/{vendorItemId}` | ❌ Public | Get one vendor item |
| 3a | `GET` | `/api/v1/menu/vendor/{vendorItemId}` | ❌ Public | Alias for above |
| 4 | `POST` | `/api/v1/menu/vendor-items` | 🔒 VENDOR | Add item to menu |
| 4a | `POST` | `/api/v1/menu/vendor` | 🔒 VENDOR | Alias for above |
| 5 | `PUT` | `/api/v1/menu/vendor-items/{vendorItemId}` | 🔒 VENDOR | Update item |
| 5a | `PUT` | `/api/v1/menu/vendor/{vendorItemId}` | 🔒 VENDOR | Alias for above |
| 6 | `PATCH` | `/api/v1/menu/vendor-items/{vendorItemId}/availability` | 🔒 VENDOR | Toggle availability |
| 6a | `PATCH` | `/api/v1/menu/vendor/{vendorItemId}/availability` | 🔒 VENDOR | Alias for above |
| 7 | `DELETE` | `/api/v1/menu/vendor-items/{vendorItemId}` | 🔒 VENDOR | Remove item (soft delete) |
| 7a | `DELETE` | `/api/v1/menu/vendor/{vendorItemId}` | 🔒 VENDOR | Alias for above |

---

**Version:** 1.0 | **Last Updated:** March 6, 2026 | **Status:** ✅ Production Ready

