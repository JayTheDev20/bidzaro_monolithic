# 📢 Announcement API - Exact Test Payloads

## 🐛 Your Error Fixed

**Problem:** `Text '2026-03-20T14:37' could not be parsed at index 16`
**Solution:** Use complete ISO-8601 UTC format with seconds and timezone

---

## ✅ COPY-PASTE TEST PAYLOADS

### Payload 1: Minimal (No Dates)

```json
{
  "title": "System Maintenance Scheduled",
  "message": "Our platform will undergo scheduled maintenance on March 5, 2026 from 2 AM to 4 AM UTC. All services will be temporarily unavailable during this period. Please plan your work accordingly."
}
```

**Status Code:** 201 Created

---

### Payload 2: With Dates (TODAY)

```json
{
  "title": "STRIPE Payment Gateway Now Available",
  "message": "We are thrilled to announce STRIPE integration for USA users. Enjoy faster and more secure payment processing. Update your payment preferences in settings to start using STRIPE today.",
  "targetAudience": "USERS",
  "priority": "HIGH",
  "startDate": "2026-03-05T00:00:00Z",
  "endDate": "2026-03-20T23:59:59Z"
}
```

**Status Code:** 201 Created

---

### Payload 3: With Dates (FUTURE)

```json
{
  "title": "Commission Structure Update for Q2 2026",
  "message": "Effective April 1, 2026, we are updating our vendor commission structure. High-volume vendors will receive additional discounts. Review the detailed breakdown in your vendor dashboard or contact support for questions.",
  "targetAudience": "VENDORS",
  "priority": "NORMAL",
  "startDate": "2026-04-01T00:00:00Z",
  "endDate": "2026-04-30T23:59:59Z"
}
```

**Status Code:** 201 Created

---

### Payload 4: Urgent Alert

```json
{
  "title": "Critical Security Update - Re-authentication Required",
  "message": "A security patch has been deployed to our platform. You must re-authenticate immediately by logging out and logging back in. This is mandatory for your account protection. Contact support if you experience any issues.",
  "targetAudience": "ALL",
  "priority": "URGENT",
  "startDate": "2026-03-05T12:00:00Z",
  "endDate": "2026-03-06T12:00:00Z"
}
```

**Status Code:** 201 Created

---

### Payload 5: Admin Only

```json
{
  "title": "Admin Dashboard Maintenance",
  "message": "The admin dashboard will be offline for upgrades on March 7, 2026 at 10 PM UTC. Estimated duration is 2 hours. All admin functions will be unavailable during this time.",
  "targetAudience": "ADMINS",
  "priority": "HIGH",
  "startDate": "2026-03-07T22:00:00Z",
  "endDate": "2026-03-08T00:00:00Z"
}
```

**Status Code:** 201 Created

---

### Payload 6: Promotional

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

**Status Code:** 201 Created

---

## 🧪 cURL Test Commands

### Test Payload 1 (Minimal)

```bash
curl -X POST http://localhost:8080/api/v1/admin/announcements \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"System Maintenance Scheduled","message":"Our platform will undergo scheduled maintenance on March 5, 2026 from 2 AM to 4 AM UTC. All services will be temporarily unavailable during this period. Please plan your work accordingly."}'
```

---

### Test Payload 2 (With Dates)

```bash
curl -X POST http://localhost:8080/api/v1/admin/announcements \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"STRIPE Payment Gateway Now Available","message":"We are thrilled to announce STRIPE integration for USA users. Enjoy faster and more secure payment processing. Update your payment preferences in settings to start using STRIPE today.","targetAudience":"USERS","priority":"HIGH","startDate":"2026-03-05T00:00:00Z","endDate":"2026-03-20T23:59:59Z"}'
```

---

### Test Payload 4 (Urgent)

```bash
curl -X POST http://localhost:8080/api/v1/admin/announcements \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Critical Security Update - Re-authentication Required","message":"A security patch has been deployed to our platform. You must re-authenticate immediately by logging out and logging back in. This is mandatory for your account protection. Contact support if you experience any issues.","targetAudience":"ALL","priority":"URGENT","startDate":"2026-03-05T12:00:00Z","endDate":"2026-03-06T12:00:00Z"}'
```

---

## ✅ Success Response

All payloads will return 201 Created:

```json
{
  "success": true,
  "status": 201,
  "message": "Announcement created successfully",
  "data": {
    "announcementId": "ann-550e8400-e29b-41d4",
    "title": "Your Title Here",
    "message": "Your message here",
    "targetAudience": "ALL",
    "priority": "NORMAL",
    "startDate": null,
    "endDate": null,
    "isActive": true,
    "createdBy": "admin-001",
    "createdAt": "2026-03-05T14:37:00.000Z"
  }
}
```

---

## 📅 Date Format Cheat Sheet

| Scenario | Format | Example |
|----------|--------|---------|
| Start of today | YYYY-MM-DDTHH:mm:ssZ | 2026-03-05T00:00:00Z |
| Start of tomorrow | YYYY-MM-DDTHH:mm:ssZ | 2026-03-06T00:00:00Z |
| 2 AM today | YYYY-MM-DDTHH:mm:ssZ | 2026-03-05T02:00:00Z |
| Noon today | YYYY-MM-DDTHH:mm:ssZ | 2026-03-05T12:00:00Z |
| 2:30 PM today | YYYY-MM-DDTHH:mm:ssZ | 2026-03-05T14:30:00Z |
| 10 PM today | YYYY-MM-DDTHH:mm:ssZ | 2026-03-05T22:00:00Z |
| End of today | YYYY-MM-DDTHH:mm:ssZ | 2026-03-05T23:59:59Z |

---

## 🎯 What NOT to Do

```json
{
  "startDate": "2026-03-20T14:37",          ❌ Missing :00Z
  "endDate": "2026-03-05 14:37",            ❌ Wrong format
  "startDate": "March 5, 2026",             ❌ Wrong format
  "endDate": "2026-03-05T14:37:00",         ❌ Missing Z
}
```

---

## ✅ What TO Do

```json
{
  "startDate": "2026-03-05T00:00:00Z",      ✅ Complete!
  "endDate": "2026-03-20T23:59:59Z",        ✅ Complete!
  "startDate": "2026-03-05T14:30:00Z",      ✅ Complete!
  "endDate": "2026-03-05T23:59:59Z",        ✅ Complete!
}
```

---

## 🚀 You're Ready!

Pick any payload above and:
1. Copy it
2. Replace with your content
3. Use the cURL command
4. You'll get 201 Created response

**Status: ✅ READY TO TEST**

