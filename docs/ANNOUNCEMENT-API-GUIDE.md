# 📢 Create Announcement API - Complete Guide

## ⚠️ Date Format Error You're Getting

From the logs:
```
Text '2026-03-20T14:37' could not be parsed at index 16
Cannot deserialize value of type `java.time.Instant`
```

---

## ✅ CORRECT Date Format

### Format Pattern
```
YYYY-MM-DDTHH:mm:ssZ
```

### Examples

| ❌ WRONG | ✅ CORRECT | Notes |
|---------|-----------|-------|
| `2026-03-20T14:37` | `2026-03-20T14:37:00Z` | Must include seconds and Z |
| `2026-03-05` | `2026-03-05T00:00:00Z` | Add time and timezone |
| `2026-03-05 14:30` | `2026-03-05T14:30:00Z` | Use T not space, add seconds and Z |
| `March 5, 2026` | `2026-03-05T00:00:00Z` | Must be ISO-8601 |

---

## 📝 Correct Payload with Dates

```json
{
  "title": "STRIPE Payment Gateway Now Available",
  "message": "We're thrilled to announce STRIPE integration for USA users. Enjoy faster, more secure payment processing. Update your payment preferences in settings to start using STRIPE today.",
  "targetAudience": "USERS",
  "priority": "HIGH",
  "startDate": "2026-03-05T00:00:00Z",
  "endDate": "2026-03-20T23:59:59Z"
}
```

---

## 🔗 API Endpoint
```http
POST /api/v1/admin/announcements
Authorization: Bearer {adminToken}
Content-Type: application/json
```

---

## 📋 All Field Requirements

| Field | Required | Type | Rule | Example |
|-------|----------|------|------|---------|
| `title` | ✅ YES | String | 5-200 chars | "STRIPE Payment Gateway" |
| `message` | ✅ YES | String | 10-2000 chars | "We're thrilled to announce..." |
| `targetAudience` | ❌ NO | String | ALL, USERS, VENDORS, ADMINS | "USERS" |
| `priority` | ❌ NO | String | LOW, NORMAL, HIGH, URGENT | "HIGH" |
| `startDate` | ❌ NO | ISO-8601 UTC | Format: YYYY-MM-DDTHH:mm:ssZ | "2026-03-05T00:00:00Z" |
| `endDate` | ❌ NO | ISO-8601 UTC | Format: YYYY-MM-DDTHH:mm:ssZ | "2026-03-20T23:59:59Z" |

---

## 🧪 Valid Complete Payload

```json
{
  "title": "System Maintenance Scheduled for March 5",
  "message": "Our platform will undergo scheduled maintenance on March 5, 2026 from 2 AM to 4 AM UTC. All services will be temporarily unavailable during this period. Please plan your work accordingly. We apologize for any inconvenience.",
  "targetAudience": "ALL",
  "priority": "HIGH",
  "startDate": "2026-03-05T00:00:00Z",
  "endDate": "2026-03-05T05:00:00Z"
}
```

---

## ✅ Minimal Valid Payload

```json
{
  "title": "System Maintenance Scheduled",
  "message": "Our platform will undergo scheduled maintenance on March 5, 2026 from 2 AM to 4 AM UTC. All services will be temporarily unavailable during this period. Please plan your work accordingly."
}
```

---

## 🧪 Test with cURL

```bash
curl -X POST http://localhost:8080/api/v1/admin/announcements \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "System Maintenance Scheduled",
    "message": "Our platform will undergo scheduled maintenance on March 5, 2026 from 2 AM to 4 AM UTC. All services will be temporarily unavailable during this period.",
    "startDate": "2026-03-05T00:00:00Z",
    "endDate": "2026-03-05T05:00:00Z"
  }'
```

---

## ✅ Success Response (201 Created)

```json
{
  "success": true,
  "status": 201,
  "message": "Announcement created successfully",
  "data": {
    "announcementId": "ann-550e8400-e29b",
    "title": "System Maintenance Scheduled",
    "message": "Our platform will undergo scheduled maintenance...",
    "targetAudience": "ALL",
    "priority": "NORMAL",
    "startDate": "2026-03-05T00:00:00Z",
    "endDate": "2026-03-05T05:00:00Z",
    "isActive": true,
    "createdBy": "admin-001",
    "createdAt": "2026-03-05T14:37:00Z"
  }
}
```

---

## ❌ Error: Wrong Date Format

**Your Error:**
```json
{
  "success": false,
  "status": 400,
  "message": "Malformed JSON request",
  "error": {
    "code": "JSON_PARSE_ERROR",
    "message": "Cannot deserialize value of type `java.time.Instant` from String \"2026-03-20T14:37\": Failed to deserialize java.time.Instant: Text '2026-03-20T14:37' could not be parsed at index 16"
  }
}
```

**Cause:** Missing seconds and timezone (Z)

**Fix:** Use `"2026-03-20T14:37:00Z"` instead of `"2026-03-20T14:37"`

---

## 📅 Date Format Examples

### Start of Day
```
2026-03-05T00:00:00Z
```

### End of Day
```
2026-03-05T23:59:59Z
```

### Morning (2 AM)
```
2026-03-05T02:00:00Z
```

### Afternoon (2:30 PM)
```
2026-03-05T14:30:00Z
```

### Evening (11:59 PM)
```
2026-03-05T23:59:59Z
```

---

## 🎯 Quick Fix

When you see: `Text 'XXXX' could not be parsed at index 16`

**It means your date is incomplete. Add:**
1. `:00` for seconds
2. `Z` for UTC timezone

---

## 🚀 Ready to Use

Copy the correct payload above with proper dates in ISO-8601 UTC format (YYYY-MM-DDTHH:mm:ssZ) and you're good to go!

**Status: ✅ FIXED AND READY**

