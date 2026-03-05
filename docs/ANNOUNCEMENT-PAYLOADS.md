# 📢 Announcement API - Copy-Paste Ready Payloads

## ⚠️ Your Error Fixed

**Problem:**
```
Text '2026-03-20T14:37' could not be parsed at index 16
```

**Solution:**
```
❌ "2026-03-20T14:37"
✅ "2026-03-20T14:37:00Z"   (Add :00 and Z)
```

---

## 🔗 Endpoint
```http
POST /api/v1/admin/announcements
Authorization: Bearer {adminToken}
Content-Type: application/json
```

---

## ✅ READY-TO-USE PAYLOADS

### Payload 1: Minimal (No Dates)
```json
{
  "title": "System Maintenance Scheduled",
  "message": "Our platform will undergo scheduled maintenance on March 5, 2026 from 2 AM to 4 AM UTC. All services will be temporarily unavailable during this period."
}
```

### Payload 2: With Date Range
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

### Payload 3: Vendor Update
```json
{
  "title": "Commission Structure Update for Q2 2026",
  "message": "Effective April 1, 2026, we're updating our vendor commission structure. High-volume vendors will receive additional discounts. Review the detailed breakdown in your vendor dashboard or contact support for questions.",
  "targetAudience": "VENDORS",
  "priority": "NORMAL",
  "startDate": "2026-04-01T00:00:00Z",
  "endDate": "2026-04-30T23:59:59Z"
}
```

### Payload 4: Urgent Alert
```json
{
  "title": "Critical Security Update - Re-authentication Required",
  "message": "A security patch has been deployed. You must re-authenticate immediately by logging out and logging back in. This is mandatory for your account protection. Contact support if you experience any issues.",
  "targetAudience": "ALL",
  "priority": "URGENT",
  "startDate": "2026-03-05T12:00:00Z",
  "endDate": "2026-03-06T12:00:00Z"
}
```

### Payload 5: Admin-Only Notice
```json
{
  "title": "Admin Dashboard Maintenance",
  "message": "The admin dashboard will be offline for upgrades on March 7, 2026 at 10 PM UTC. Estimated duration: 2 hours. All admin functions will be unavailable during this time.",
  "targetAudience": "ADMINS",
  "priority": "HIGH",
  "startDate": "2026-03-07T22:00:00Z",
  "endDate": "2026-03-08T00:00:00Z"
}
```

### Payload 6: Promotional Announcement
```json
{
  "title": "March Madness - 20% Off All Orders",
  "message": "Celebrate with us this March! Get 20% off all food orders using code MARCH20. Valid from March 1-31, 2026. No minimum order required. Terms and conditions apply.",
  "targetAudience": "USERS",
  "priority": "NORMAL",
  "startDate": "2026-03-01T00:00:00Z",
  "endDate": "2026-03-31T23:59:59Z"
}
```

---

## 🧪 Test Command (Minimal Payload)

```bash
curl -X POST http://localhost:8080/api/v1/admin/announcements \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "System Maintenance Scheduled",
    "message": "Our platform will undergo scheduled maintenance on March 5, 2026 from 2 AM to 4 AM UTC. All services will be temporarily unavailable during this period."
  }'
```

---

## 🧪 Test Command (With Dates)

```bash
curl -X POST http://localhost:8080/api/v1/admin/announcements \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "STRIPE Payment Gateway Now Available",
    "message": "We are thrilled to announce STRIPE integration for USA users. Enjoy faster, more secure payment processing. Update your payment preferences in settings to start using STRIPE today.",
    "targetAudience": "USERS",
    "priority": "HIGH",
    "startDate": "2026-03-05T00:00:00Z",
    "endDate": "2026-03-20T23:59:59Z"
  }'
```

---

## 📅 Date Format Rules

**MUST USE THIS FORMAT:**
```
YYYY-MM-DDTHH:mm:ssZ
```

### Examples

| Time | Date String |
|------|------------|
| Start of day (midnight) | `2026-03-05T00:00:00Z` |
| 2 AM | `2026-03-05T02:00:00Z` |
| Noon | `2026-03-05T12:00:00Z` |
| 2 PM (14:00) | `2026-03-05T14:00:00Z` |
| 2:30 PM | `2026-03-05T14:30:00Z` |
| End of day (11:59 PM) | `2026-03-05T23:59:59Z` |

---

## ✅ Validation Rules

| Field | Required | Min/Max | Valid Values |
|-------|----------|---------|--------------|
| title | ✅ YES | 5-200 chars | Any string |
| message | ✅ YES | 10-2000 chars | Any string |
| targetAudience | ❌ NO | - | ALL, USERS, VENDORS, ADMINS |
| priority | ❌ NO | - | LOW, NORMAL, HIGH, URGENT |
| startDate | ❌ NO | - | ISO-8601 UTC: YYYY-MM-DDTHH:mm:ssZ |
| endDate | ❌ NO | - | ISO-8601 UTC: YYYY-MM-DDTHH:mm:ssZ |

---

## ✅ Success Response (201 Created)

```json
{
  "success": true,
  "status": 201,
  "message": "Announcement created successfully",
  "data": {
    "announcementId": "ann-550e8400-e29b",
    "title": "Your announcement title",
    "message": "Your announcement message",
    "targetAudience": "ALL",
    "priority": "NORMAL",
    "isActive": true,
    "createdBy": "admin-001",
    "createdAt": "2026-03-05T14:37:00Z"
  }
}
```

---

## ❌ Common Errors

### Error 1: Wrong Date Format
```
❌ "2026-03-20T14:37"
✅ "2026-03-20T14:37:00Z"
```
Add `:00` for seconds and `Z` for UTC timezone.

### Error 2: Title Too Short
```
❌ title: "News"  (4 chars)
✅ title: "System Maintenance Scheduled"  (29 chars)
```
Must be 5-200 characters.

### Error 3: Message Too Short
```
❌ message: "Update"  (6 chars)
✅ message: "Our platform will undergo maintenance..."  (100+ chars)
```
Must be 10-2000 characters.

### Error 4: Invalid Audience
```
❌ targetAudience: "CUSTOMERS"
✅ targetAudience: "USERS"
```
Valid: ALL, USERS, VENDORS, ADMINS

### Error 5: Invalid Priority
```
❌ priority: "CRITICAL"
✅ priority: "URGENT"
```
Valid: LOW, NORMAL, HIGH, URGENT

---

## 🎯 Quick Steps

1. Copy one of the 6 ready-to-use payloads above
2. Replace with your announcement content
3. **Ensure dates use format:** `YYYY-MM-DDTHH:mm:ssZ`
4. Use the cURL test command
5. Check response (should be 201 Created)

---

## 🚀 Status

✅ Date format issue fixed
✅ Multiple copy-paste payloads provided
✅ All validation rules documented
✅ cURL test commands ready
✅ Success/error responses shown

**You're ready to create announcements!**

