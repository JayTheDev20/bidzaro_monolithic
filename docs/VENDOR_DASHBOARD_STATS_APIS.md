# Vendor Dashboard & Vendor Stats APIs

This document covers APIs used to fetch:

1. Vendor dashboard data (for vendor login)
2. Vendor stats data (vendor-safe and admin analytics views)

**Base URL:** `http://localhost:8080/api/v1`

---

## 1) Vendor Dashboard Data API

### Endpoint

`GET /analytics/vendor/dashboard`

### Access

- Authentication: Required (`Bearer` token)
- Role: `VENDOR`

### Purpose

Returns detailed dashboard metrics for the logged-in vendor.

### Success Response (200)

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {
    "vendorId": "f1f6c167-0e89-461d-8346-42ff1542f771",
    "vendorName": "ABC Catering",
    "metrics": {
      "totalOrders": 10,
      "completedOrders": 0,
      "pendingOrders": 0,
      "cancelledOrders": 0,
      "totalRevenue": 0,
      "pendingPayouts": 0,
      "thisMonthRevenue": 0,
      "currency": "INR"
    },
    "bidMetrics": {
      "totalBidsSubmitted": 24,
      "acceptedBids": 0,
      "pendingBids": 0,
      "acceptanceRate": 0,
      "averageBidAmount": 0
    },
    "recentOrders": [],
    "upcomingEvents": [],
    "performance": {
      "averageRating": 4.6,
      "totalReviews": 58,
      "responseRate": 0,
      "onTimeDeliveryRate": 0,
      "repeatCustomers": 0
    }
  },
  "timestamp": "2026-03-17T00:00:00Z"
}
```

### cURL

```bash
curl -X GET "http://localhost:8080/api/v1/analytics/vendor/dashboard" \
  -H "Authorization: Bearer <VENDOR_ACCESS_TOKEN>"
```

---

## 2) Vendor Stats Data API (Vendor-Safe)

### Endpoint

`GET /analytics/vendor/stats`

### Access

- Authentication: Required (`Bearer` token)
- Role: `VENDOR`

### Purpose

Returns compact vendor stats for vendor dashboards without admin access.

### Success Response (200)

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {
    "vendorId": "f1f6c167-0e89-461d-8346-42ff1542f771",
    "vendorName": "ABC Catering",
    "metrics": {
      "totalOrders": 10,
      "completedOrders": 0,
      "pendingOrders": 0,
      "cancelledOrders": 0,
      "totalRevenue": 0,
      "pendingPayouts": 0,
      "thisMonthRevenue": 0,
      "currency": "INR"
    },
    "bidMetrics": {
      "totalBidsSubmitted": 24,
      "acceptedBids": 0,
      "pendingBids": 0,
      "acceptanceRate": 0,
      "averageBidAmount": 0
    },
    "performance": {
      "averageRating": 4.6,
      "totalReviews": 58,
      "responseRate": 0,
      "onTimeDeliveryRate": 0,
      "repeatCustomers": 0
    }
  },
  "timestamp": "2026-03-17T00:00:00Z"
}
```

### cURL

```bash
curl -X GET "http://localhost:8080/api/v1/analytics/vendor/stats" \
  -H "Authorization: Bearer <VENDOR_ACCESS_TOKEN>"
```

---

## 3) Vendor Stats Data API (Admin Dashboard)

### Endpoint

`GET /admin/dashboard`

### Access

- Authentication: Required (`Bearer` token)
- Role: `ADMIN`

### Purpose

Returns complete admin dashboard stats; vendor stats are available under `data.vendorStats`.

### Vendor stats fields in response

```json
{
  "vendorStats": {
    "totalVendors": 120,
    "activeVendors": 89,
    "pendingApproval": 14,
    "verifiedVendors": 75,
    "newVendorsThisMonth": 9
  }
}
```

### Full response shape (trimmed)

```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": {
    "userStats": {},
    "vendorStats": {
      "totalVendors": 120,
      "activeVendors": 89,
      "pendingApproval": 14,
      "verifiedVendors": 75,
      "newVendorsThisMonth": 9
    },
    "orderStats": {},
    "revenueStats": {},
    "bidStats": {}
  },
  "timestamp": "2026-03-17T00:00:00Z"
}
```

### cURL

```bash
curl -X GET "http://localhost:8080/api/v1/admin/dashboard" \
  -H "Authorization: Bearer <ADMIN_ACCESS_TOKEN>"
```

---

## 4) Vendor Analytics Stats API (Admin Only)

### Endpoint

`GET /analytics/vendors`

### Access

- Authentication: Required (`Bearer` token)
- Role: `ADMIN`

### Purpose

Returns platform-level vendor analytics summary map.

> Vendor tokens will get `403 FORBIDDEN` on this endpoint by design.

---

## Notes

- Use `GET /analytics/vendor/dashboard` and `GET /analytics/vendor/stats` for vendor UI.
- `/analytics/vendors` is an admin aggregate endpoint and not for vendor login.
- All APIs return wrapped response format: `success`, `status`, `message`, `data`, `timestamp`.
- Vendor dashboard endpoint auto-resolves vendor by logged-in user (`vendorService.getVendorByUserId`).
- Currency in vendor dashboard is derived from vendor country (`INDIA -> INR`, else default path uses `USD`).
- If you call vendor endpoint with non-vendor token, it returns authorization error.
