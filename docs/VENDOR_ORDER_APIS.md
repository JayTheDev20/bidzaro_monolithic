# Vendor Order APIs

This document contains all vendor-relevant order APIs in this backend.

- **Base URL:** `/api/v1`
- **Auth:** Bearer JWT required
- **Role:** `VENDOR` for vendor-specific endpoints

## What Vendors Can Do

1. Get orders assigned to them
2. Get details of a specific assigned order
3. Update operational order status for assigned orders

## Endpoint Summary

| Method | Endpoint | Purpose | Role |
|---|---|---|---|
| `GET` | `/orders/vendor?page=0&size=20` | Get paginated vendor orders | `VENDOR` |
| `GET` | `/orders/vendor/my?page=0&size=20` | Alias/shorthand for vendor orders | `VENDOR` |
| `GET` | `/orders/{orderId}` | Get order details (vendor must be assigned) | Authenticated (access-checked) |
| `PUT` | `/orders/vendor/{orderId}/status?status=IN_PREPARATION` | Update vendor order status | `VENDOR` |
| `PATCH` | `/orders/vendor/{orderId}/status?status=IN_PREPARATION` | Update vendor order status (alternate verb) | `VENDOR` |

---

## 1) Get Vendor Orders

### `GET /orders/vendor?page=0&size=20`

Returns orders assigned to the logged-in vendor.

**Query Params**

- `page` (default: `0`)
- `size` (default: `20`)

**Response (200)**

```json
{
  "success": true,
  "status": 200,
  "message": "Vendor orders retrieved",
  "data": [
    {
      "orderId": "0ce69fd8-8ae8-4e41-8df8-f75a3229e326",
      "status": "CONFIRMED",
      "userId": "53329557-e14b-4479-830b-fe123e56af2a",
      "vendorOrders": [
        {
          "vendorId": "vendor-uuid",
          "vendorUserId": "vendor-user-uuid",
          "vendorName": "Vendor Kitchen"
        }
      ]
    }
  ],
  "pageInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

---

## 2) Get Vendor Orders (Shorthand)

### `GET /orders/vendor/my?page=0&size=20`

Same behavior as `/orders/vendor`.

---

## 3) Get Order Details

### `GET /orders/{orderId}`

Returns full order details.

### Access rule

Allowed if:

- logged-in user is the order owner, or
- logged-in user is assigned vendor (`vendorUserId` matches)

Otherwise returns `403 FORBIDDEN`.

**Response (200)**

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {
    "orderId": "0ce69fd8-8ae8-4e41-8df8-f75a3229e326",
    "status": "IN_PREPARATION",
    "eventDetails": {
      "eventDate": "2026-03-30",
      "eventTime": "19:00"
    },
    "vendorOrders": [
      {
        "vendorId": "vendor-uuid",
        "vendorUserId": "vendor-user-uuid",
        "vendorName": "Vendor Kitchen"
      }
    ]
  }
}
```

---

## 4) Update Vendor Order Status

### `PUT /orders/vendor/{orderId}/status?status=IN_PREPARATION`
### `PATCH /orders/vendor/{orderId}/status?status=IN_PREPARATION`

Updates operational workflow status for assigned vendor orders.

### Required Query Param

- `status`: must be one of vendor-allowed statuses (below)

### Vendor-Allowed Status Values

- `IN_PREPARATION`
- `READY_FOR_DELIVERY`
- `DELIVERING`
- `DELIVERED`

### Full `OrderStatus` enum (system-wide)

- `PENDING_TOKEN_PAYMENT`
- `CONFIRMED`
- `IN_PREPARATION`
- `READY_FOR_DELIVERY`
- `DELIVERING`
- `DELIVERED`
- `COMPLETED`
- `CANCELLED`

### Valid Transition Chain

- `CONFIRMED -> IN_PREPARATION`
- `IN_PREPARATION -> READY_FOR_DELIVERY`
- `READY_FOR_DELIVERY -> DELIVERING`
- `DELIVERING -> DELIVERED`
- `DELIVERED -> COMPLETED` (typically non-vendor flow)

If transition is invalid, API returns `400 INVALID_TRANSITION`.

### Access checks

- Vendor must be assigned to the order (`vendorUserId` check)
- If not assigned: `403 FORBIDDEN`

**Response (200)**

```json
{
  "success": true,
  "status": 200,
  "message": "Vendor order status updated",
  "data": {
    "orderId": "0ce69fd8-8ae8-4e41-8df8-f75a3229e326",
    "status": "READY_FOR_DELIVERY"
  }
}
```

---

## Common Error Cases

### 400 Bad Request

- `INVALID_STATUS`: vendor sent status not allowed for vendor APIs
- `INVALID_TRANSITION`: invalid progression (example: `CONFIRMED -> DELIVERING` directly)

### 403 Forbidden

- vendor not assigned to the order

### 404 Not Found

- order not found

### 405 Method Not Allowed

- wrong HTTP method on endpoint
- Example: old generic path `/orders/{orderId}/status` was PATCH-first; vendor should use `/orders/vendor/{orderId}/status`

---

## Curl Examples

```bash
curl -X GET "http://localhost:8080/api/v1/orders/vendor?page=0&size=20" \
  -H "Authorization: Bearer <VENDOR_JWT>"

curl -X GET "http://localhost:8080/api/v1/orders/0ce69fd8-8ae8-4e41-8df8-f75a3229e326" \
  -H "Authorization: Bearer <VENDOR_JWT>"

curl -X PUT "http://localhost:8080/api/v1/orders/vendor/0ce69fd8-8ae8-4e41-8df8-f75a3229e326/status?status=IN_PREPARATION" \
  -H "Authorization: Bearer <VENDOR_JWT>"

curl -X PATCH "http://localhost:8080/api/v1/orders/vendor/0ce69fd8-8ae8-4e41-8df8-f75a3229e326/status?status=READY_FOR_DELIVERY" \
  -H "Authorization: Bearer <VENDOR_JWT>"
```

