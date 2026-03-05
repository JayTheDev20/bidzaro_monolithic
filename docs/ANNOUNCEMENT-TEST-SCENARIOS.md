# 🧪 Announcement CRUD - Test Scenarios & Examples

## 📋 Test Scenarios

### Scenario 1: Create Announcement for System Maintenance

**Request:**
```bash
POST /api/v1/admin/announcements
Authorization: Bearer admin_token_123
Content-Type: application/json

{
  "title": "System Maintenance Scheduled - March 5, 2026",
  "message": "We will perform scheduled maintenance on March 5, 2026 from 2 AM to 4 AM UTC. During this time, all services will be temporarily unavailable. We apologize for any inconvenience. Please plan accordingly.",
  "targetAudience": "ALL",
  "priority": "HIGH",
  "startDate": "2026-03-05T02:00:00Z",
  "endDate": "2026-03-05T04:00:00Z"
}
```

**Expected Response (201 Created):**
```json
{
  "success": true,
  "status": 201,
  "message": "Announcement created successfully",
  "data": {
    "announcementId": "ann-a1b2c3d4-e5f6",
    "title": "System Maintenance Scheduled - March 5, 2026",
    "message": "We will perform scheduled maintenance...",
    "targetAudience": "ALL",
    "priority": "HIGH",
    "startDate": "2026-03-05T02:00:00Z",
    "endDate": "2026-03-05T04:00:00Z",
    "isActive": true,
    "createdBy": "admin-001",
    "createdAt": "2026-03-05T10:00:00Z"
  }
}
```

---

### Scenario 2: Get All Announcements

**Request:**
```bash
GET /api/v1/admin/announcements?page=0&size=10
Authorization: Bearer admin_token_123
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "status": 200,
  "message": "Announcements retrieved",
  "data": [
    {
      "announcementId": "ann-a1b2c3d4-e5f6",
      "title": "System Maintenance Scheduled",
      "message": "We will perform scheduled maintenance...",
      "targetAudience": "ALL",
      "priority": "HIGH",
      "startDate": "2026-03-05T02:00:00Z",
      "endDate": "2026-03-05T04:00:00Z",
      "isActive": true,
      "createdBy": "admin-001",
      "createdAt": "2026-03-05T10:00:00Z"
    }
  ],
  "pageInfo": {
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

---

### Scenario 3: Get Announcement by ID

**Request:**
```bash
GET /api/v1/admin/announcements/ann-a1b2c3d4-e5f6
Authorization: Bearer admin_token_123
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "status": 200,
  "message": "Announcement retrieved",
  "data": {
    "announcementId": "ann-a1b2c3d4-e5f6",
    "title": "System Maintenance Scheduled",
    "message": "We will perform scheduled maintenance on March 5, 2026 from 2 AM to 4 AM UTC...",
    "targetAudience": "ALL",
    "priority": "HIGH",
    "startDate": "2026-03-05T02:00:00Z",
    "endDate": "2026-03-05T04:00:00Z",
    "isActive": true,
    "createdBy": "admin-001",
    "createdAt": "2026-03-05T10:00:00Z"
  }
}
```

---

### Scenario 4: Update Announcement

**Request:**
```bash
PUT /api/v1/admin/announcements/ann-a1b2c3d4-e5f6
Authorization: Bearer admin_token_123
Content-Type: application/json

{
  "title": "URGENT: System Maintenance Rescheduled - March 6, 2026",
  "message": "Due to unforeseen circumstances, we have rescheduled the maintenance to March 6, 2026 from 2 AM to 4 AM UTC. All services will be temporarily unavailable during this period.",
  "priority": "URGENT",
  "startDate": "2026-03-06T02:00:00Z",
  "endDate": "2026-03-06T04:00:00Z"
}
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "status": 200,
  "message": "Announcement updated",
  "data": {
    "announcementId": "ann-a1b2c3d4-e5f6",
    "title": "URGENT: System Maintenance Rescheduled - March 6, 2026",
    "message": "Due to unforeseen circumstances, we have rescheduled...",
    "targetAudience": "ALL",
    "priority": "URGENT",
    "startDate": "2026-03-06T02:00:00Z",
    "endDate": "2026-03-06T04:00:00Z",
    "isActive": true,
    "createdBy": "admin-001",
    "createdAt": "2026-03-05T10:00:00Z"
  }
}
```

---

### Scenario 5: Delete Announcement

**Request:**
```bash
DELETE /api/v1/admin/announcements/ann-a1b2c3d4-e5f6
Authorization: Bearer admin_token_123
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "status": 200,
  "message": "Announcement deleted"
}
```

---

### Scenario 6: Update with Partial Fields

**Request (Update Only Title and Priority):**
```bash
PUT /api/v1/admin/announcements/ann-a1b2c3d4-e5f6
Authorization: Bearer admin_token_123
Content-Type: application/json

{
  "title": "Maintenance Complete",
  "priority": "LOW"
}
```

**Note:** Other fields remain unchanged. This is flexible update!

**Expected Response (200 OK):**
```json
{
  "success": true,
  "status": 200,
  "message": "Announcement updated",
  "data": {
    "announcementId": "ann-a1b2c3d4-e5f6",
    "title": "Maintenance Complete",
    "message": "Original message stays the same...",
    "targetAudience": "ALL",
    "priority": "LOW",
    "startDate": "2026-03-06T02:00:00Z",
    "endDate": "2026-03-06T04:00:00Z",
    "isActive": true,
    "createdBy": "admin-001",
    "createdAt": "2026-03-05T10:00:00Z"
  }
}
```

---

## ❌ Error Scenarios

### Error 1: Invalid Title Length

**Request:**
```json
{
  "title": "Hi",
  "message": "This message is valid and has more than 10 characters"
}
```

**Response (400 Bad Request):**
```json
{
  "success": false,
  "status": 400,
  "message": "Validation failed",
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "title: size must be between 5 and 200"
  }
}
```

---

### Error 2: Missing Required Field

**Request:**
```json
{
  "title": "Maintenance"
}
```

**Response (400 Bad Request):**
```json
{
  "success": false,
  "status": 400,
  "message": "Validation failed",
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "message: Message is required"
  }
}
```

---

### Error 3: Announcement Not Found

**Request:**
```bash
GET /api/v1/admin/announcements/invalid-id
Authorization: Bearer admin_token_123
```

**Response (404 Not Found):**
```json
{
  "success": false,
  "status": 404,
  "message": "Announcement not found",
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "Announcement not found"
  }
}
```

---

### Error 4: Unauthorized (Missing Token)

**Request:**
```bash
GET /api/v1/admin/announcements
```

**Response (401 Unauthorized):**
```json
{
  "success": false,
  "status": 401,
  "message": "Unauthorized",
  "error": {
    "code": "UNAUTHORIZED",
    "message": "Missing or invalid authentication token"
  }
}
```

---

### Error 5: Forbidden (Not Admin)

**Request:**
```bash
POST /api/v1/admin/announcements
Authorization: Bearer user_token_123
Content-Type: application/json

{
  "title": "Test",
  "message": "This user is not an admin"
}
```

**Response (403 Forbidden):**
```json
{
  "success": false,
  "status": 403,
  "message": "Access Denied",
  "error": {
    "code": "FORBIDDEN",
    "message": "Only admins can create announcements"
  }
}
```

---

## 🎯 Audience Targeting Examples

### Target All Users
```json
{
  "targetAudience": "ALL"
}
```

### Target Regular Users Only
```json
{
  "targetAudience": "USERS"
}
```

### Target Vendors Only
```json
{
  "targetAudience": "VENDORS"
}
```

### Target Admins Only
```json
{
  "targetAudience": "ADMINS"
}
```

---

## 🎨 Priority Levels Examples

### Low Priority (Informational)
```json
{
  "priority": "LOW",
  "message": "New feature tip: You can now filter results..."
}
```

### Normal Priority (Regular Updates)
```json
{
  "priority": "NORMAL",
  "message": "We have updated our privacy policy..."
}
```

### High Priority (Important)
```json
{
  "priority": "HIGH",
  "message": "New payment method STRIPE is now available..."
}
```

### Urgent Priority (Critical)
```json
{
  "priority": "URGENT",
  "message": "Security issue detected, please update your password..."
}
```

---

## 📊 Real-World Use Cases

### 1. New Feature Launch
```json
{
  "title": "STRIPE Payment Gateway Now Live",
  "message": "We're excited to announce STRIPE integration for USA users. This provides faster, more secure payment processing. Update your payment preferences to use STRIPE.",
  "targetAudience": "USERS",
  "priority": "HIGH",
  "startDate": "2026-03-05T00:00:00Z",
  "endDate": "2026-03-20T23:59:59Z"
}
```

### 2. Vendor Commission Update
```json
{
  "title": "Commission Structure Update - Q2 2026",
  "message": "Effective April 1, 2026, we're updating vendor commission structure with tiered discounts for high-volume vendors.",
  "targetAudience": "VENDORS",
  "priority": "NORMAL",
  "startDate": "2026-04-01T00:00:00Z",
  "endDate": "2026-04-30T23:59:59Z"
}
```

### 3. Security Alert
```json
{
  "title": "Critical Security Update",
  "message": "A security patch has been deployed. You must re-authenticate immediately by logging out and logging back in.",
  "targetAudience": "ALL",
  "priority": "URGENT",
  "startDate": "2026-03-05T12:00:00Z",
  "endDate": "2026-03-06T12:00:00Z"
}
```

### 4. System Maintenance
```json
{
  "title": "Scheduled Maintenance",
  "message": "Our platform will be offline for upgrades on March 7, 2026 at 10 PM UTC for approximately 2 hours.",
  "targetAudience": "ALL",
  "priority": "HIGH",
  "startDate": "2026-03-07T22:00:00Z",
  "endDate": "2026-03-08T00:00:00Z"
}
```

---

## ✅ Testing Checklist

- [ ] Create announcement with all fields
- [ ] Create announcement with minimal fields
- [ ] Get all announcements (verify pagination)
- [ ] Get announcement by ID (verify correct data)
- [ ] Update with single field
- [ ] Update with multiple fields
- [ ] Update with all fields
- [ ] Delete announcement
- [ ] Test with invalid target audience
- [ ] Test with invalid priority
- [ ] Test with title too short
- [ ] Test with message too short
- [ ] Test without authentication
- [ ] Test with non-admin token
- [ ] Test with non-existent ID

---

## 🚀 Status

✅ All test scenarios documented
✅ Real-world examples provided
✅ Error cases covered
✅ Ready for QA testing

**Status: ✅ COMPLETE**

