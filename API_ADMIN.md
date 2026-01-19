# Admin API Documentation

This document lists all API endpoints available for **Admins**, including user/vendor management, platform configuration, and analytics.

## Authentication (`/auth`)

### Admin Login
**POST** `/auth/login`
*Admins log in using the standard login endpoint.*

## Dashboard (`/admin`)

### Get Stats
**GET** `/admin/dashboard`
*Returns total users, vendors, orders, and revenue.*

## User Management (`/admin/users`)

### List Users
**GET** `/admin/users`
*Query Params: page, size, status, userType*

### Manage User Status
**PATCH** `/admin/users/{userId}/status`
*Query Params: status (ACTIVE/SUSPENDED), reason*

## Vendor Management (`/admin/vendors`, `/vendors`)

### Pending Approvals
**GET** `/vendors/admin/pending`

### Approve Vendor
**POST** `/vendors/{vendorId}/approve`

### Reject Vendor
**POST** `/vendors/{vendorId}/reject?reason=Incomplete docs`

## Order Management (`/admin/orders`)

### List All Orders
**GET** `/admin/orders`
*View all orders across the platform.*

## Platform Configuration (`/admin/platform-config`)

### Get Config
**GET** `/admin/platform-config`

### Update Config
**PUT** `/admin/platform-config`
```json
{
  "biddingConfig": {
    "competitivePeriodHours": 24,
    "coolingPeriodHours": 48,
    "paymentCoolingPeriodHours": 24,
    "bidExpiryHours": 72,
    "minVendorsForCompetitive": 3,
    "maxBidRevisions": 5
  },
  "paymentConfig": {
    "tokenPercentage": 20.0,
    "enabledGateways": ["STRIPE", "RAZORPAY"],
    "defaultGateway": "STRIPE",
    "paymentTimeoutHours": 24,
    "autoRefundEnabled": true
  },
  "cancellationPolicy": {
    "cancellationWindowDays": 7,
    "refundTiers": [
      { "daysBeforeEvent": 30, "refundPercentage": 100.0 },
      { "daysBeforeEvent": 7, "refundPercentage": 50.0 }
    ]
  },
  "commissionConfig": {
    "platformFeePercentage": 5.0,
    "vendorCommissionPercentage": 10.0,
    "paymentGatewayFeePercentage": 2.9
  }
}
```
**Response:**
```json
{
  "success": true,
  "message": "Configuration updated",
  "data": {
    "commissionRate": 10.0
  }
}
```

## Promo Codes (`/promos`)

### Create Promo
**POST** `/promos`
```json
{
  "code": "SAVE20",
  "title": "Summer Sale",
  "description": "Get 20% off on your first order",
  "type": "PERCENTAGE",
  "value": 20.0,
  "maxDiscountAmount": 50.0,
  "minOrderAmount": 100.0,
  "validFrom": "2023-06-01T00:00:00Z",
  "validTo": "2023-08-31T23:59:59Z",
  "usageLimitGlobal": 1000,
  "usageLimitPerUser": 1,
  "applicableTo": "ALL",
  "firstOrderOnly": true
}
```
*Promo Types: PERCENTAGE, FLAT*
*Applicable To: ALL, SPECIFIC_VENDORS, SPECIFIC_USERS, SPECIFIC_CUISINES*

**Response:**
```json
{
  "success": true,
  "message": "Promo code created",
  "data": {
    "promoCodeId": "promo_1",
    "code": "SAVE20"
  }
}
```

### Manage Promos
*   **GET** `/promos` - List all promos
*   **PUT** `/promos/{id}` - Update promo
*   **DELETE** `/promos/{id}` - Deactivate promo

## Announcements (`/admin/announcements`)

### Create Announcement
**POST** `/admin/announcements`
```json
{
  "title": "System Maintenance",
  "message": "Downtime at midnight",
  "targetAudience": "ALL",
  "priority": "HIGH",
  "startDate": "2023-10-01T00:00:00Z",
  "endDate": "2023-10-02T00:00:00Z"
}
```
*Target Audience: ALL, USERS, VENDORS, ADMINS*
*Priority: LOW, NORMAL, HIGH, URGENT*

**Response:**
```json
{
  "success": true,
  "message": "Announcement created",
  "data": {
    "announcementId": "ann_2",
    "title": "System Maintenance"
  }
}
```

*   **GET** `/admin/announcements` - List announcements
*   **DELETE** `/admin/announcements/{id}` - Delete announcement

## Analytics & Reports (`/analytics`)

*   **GET** `/analytics/overview` - Platform overview
*   **GET** `/analytics/revenue` - Revenue charts
*   **GET** `/analytics/users` - User growth
*   **GET** `/analytics/reports?reportType=SALES&startDate=...&endDate=...` - Generate reports

## Audit Logs (`/admin/audit-logs`)

*   **GET** `/admin/audit-logs` - View system activity logs
