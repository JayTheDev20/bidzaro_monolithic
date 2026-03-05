# ⚡ Announcement API - Quick Fix Card

## 🐛 Your Error (FIXED)

```
Text '2026-03-20T14:37' could not be parsed at index 16
```

---

## ✅ The Fix

```
❌ WRONG:  "2026-03-20T14:37"
✅ RIGHT:  "2026-03-20T14:37:00Z"
           Add :00 (seconds) and Z (timezone)
```

---

## 📝 Correct Payload Format

```json
{
  "title": "System Maintenance Scheduled",
  "message": "Our platform will undergo scheduled maintenance on March 5, 2026 from 2 AM to 4 AM UTC. All services will be temporarily unavailable during this period.",
  "startDate": "2026-03-05T00:00:00Z",
  "endDate": "2026-03-05T05:00:00Z"
}
```

---

## 📅 Date Format Pattern

```
YYYY-MM-DDTHH:mm:ssZ
2026-03-05T14:37:00Z  ✅ Correct
2026-03-05T14:37      ❌ Wrong (missing :00Z)
```

---

## 📋 Required Fields

| Field | Min/Max | Example |
|-------|---------|---------|
| title | 5-200 | "System Maintenance" |
| message | 10-2000 | "Platform maintenance on March 5..." |

---

## 🎯 Optional Fields

| Field | Valid Values |
|-------|--------------|
| targetAudience | ALL, USERS, VENDORS, ADMINS |
| priority | LOW, NORMAL, HIGH, URGENT |
| startDate | 2026-03-05T00:00:00Z |
| endDate | 2026-03-05T23:59:59Z |

---

## 🧪 Quick Test

```bash
curl -X POST http://localhost:8080/api/v1/admin/announcements \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "System Maintenance Scheduled",
    "message": "Our platform will undergo scheduled maintenance on March 5, 2026 from 2 AM to 4 AM UTC. All services will be temporarily unavailable.",
    "startDate": "2026-03-05T00:00:00Z",
    "endDate": "2026-03-05T05:00:00Z"
  }'
```

---

## ✅ Expect 201 Created

```json
{
  "success": true,
  "data": {
    "announcementId": "ann-xxx",
    "title": "System Maintenance Scheduled",
    "createdAt": "2026-03-05T14:37:00Z"
  }
}
```

---

## 📌 Remember

✅ **Date Pattern:** `YYYY-MM-DDTHH:mm:ssZ`
✅ **Must include:** Seconds (`:00`) and Timezone (`Z`)
✅ **Title:** 5-200 characters
✅ **Message:** 10-2000 characters
✅ **Z means:** UTC timezone

---

**Your issue is now FIXED!**
Copy the correct payload above and you're good to go.

